package com.familheey.app.Topic

import android.content.Context
import android.content.Intent
import com.familheey.app.Activities.LoginActivity
import com.familheey.app.BuildConfig
import com.familheey.app.Models.Appinfo
import com.familheey.app.Models.Response.UserRegistrationResponse
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.Networking.utils.GsonUtils
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.SharedPref
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object RetrofitUtil {
    const val API_BASE_URL = BuildConfig.BASE_URL
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }






    fun <S> createRxResourceSoket(context: Context, serviceClass: Class<S>): S {
        val client = getOkHttpClient(context)
        val retrofit = Retrofit.Builder()
                .baseUrl(ApiPaths.SOCKET_COMMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

        return retrofit.create(serviceClass)
    }

    fun <S> createRxResource(context: Context, serviceClass: Class<S>): S {
        val client = getOkHttpClient(context)
        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

        return retrofit.create(serviceClass)
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(120, TimeUnit.SECONDS)
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                    .method(original.method, original.body)

            val appInfo = Appinfo(context)
            builder.header("app_info", appInfo.date)
            if (SharedPref.getUserRegistration() != null) {
                val token = SharedPref.getUserRegistration().accessToken
                if (token != null && token.isNotEmpty()) builder.header("Authorization", "Bearer $token")
                if (SharedPref.getUserRegistration().id != null) builder.header("logined_user_id", SharedPref.getUserRegistration().id)
            }
            val request = builder.build()
            chain.proceed(request)
        }

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }

        httpClient.authenticator(JwtAuthenticator(context))
        return httpClient.build()
    }


    private class JwtAuthenticator(private val context: Context) : Authenticator {

        @Throws(IOException::class)
        override fun authenticate(route: Route?, response: Response): Request? {
            if (responseCount(response) >= 2) {

                return null
            }

            // We need a new client, since we don't want to make another call using our authenticated client
            val apiServices = RetrofitBase.createRxResourceWithoutAuth(ApiServices::class.java, context)
            try {
                val jsonObject1 = JsonObject()
                jsonObject1.addProperty("refresh_token", SharedPref.getUserRegistration().refreshToken)
                jsonObject1.addProperty("phone", SharedPref.getUserRegistration().mobileNumber)
                val requestBody: RequestBody = jsonObject1.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val jsonObject = apiServices.delegation(requestBody).blockingFirst()

                if (jsonObject != null) {
                    RetrofitBase.saveAuthToken(jsonObject)
                    val token = SharedPref.getUserRegistration().accessToken

                    return response.request.newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                }
            } catch (ignore: Exception) {

                val userRegistrationResponse = UserRegistrationResponse()
                SharedPref.write(SharedPref.IS_REGISTERED, false)
                SharedPref.write(SharedPref.USER, GsonUtils.getInstance().gson.toJson(userRegistrationResponse))
                SharedPref.setUserRegistration(userRegistrationResponse)

                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            return null
        }

        private fun responseCount(response: Response): Int {
            var response = response
            var result = 1
            while (response.priorResponse.also { response = it!! } != null) {
                result++
            }
            return result
        }

    }


}