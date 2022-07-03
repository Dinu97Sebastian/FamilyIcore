package com.familheey.app.Networking.Retrofit;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.familheey.app.Activities.LoginActivity;
import com.familheey.app.BuildConfig;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.Appinfo;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.Networking.utils.HttpUtil;
import com.familheey.app.Networking.utils.Logger;
import com.familheey.app.Parsers.ErrorParser;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBase {
    protected Retrofit retrofit;
    protected Context context;
    private Logger logger;


    public static final String API_BASE_URL = Constants.ApiPaths.BASE_URL;

    public RetrofitBase(Context context, boolean addTimeout) {
        this.context = context;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.level(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder().addInterceptor(interceptor);
        if (addTimeout) {
            httpClientBuilder.readTimeout(Constants.TimeOut.SOCKET_TIME_OUT, TimeUnit.SECONDS);
            httpClientBuilder.writeTimeout(Constants.TimeOut.IMAGE_UPLOAD_SOCKET_TIMEOUT, TimeUnit.SECONDS);
            httpClientBuilder.connectTimeout(Constants.TimeOut.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        } else {
            httpClientBuilder.readTimeout(Constants.TimeOut.IMAGE_UPLOAD_SOCKET_TIMEOUT, TimeUnit.SECONDS);
            httpClientBuilder.connectTimeout(Constants.TimeOut.IMAGE_UPLOAD_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        }
        addVersioningHeaders(httpClientBuilder, context);

        OkHttpClient httpClient = httpClientBuilder.build();
        httpClientBuilder.authenticator(new JwtAuthenticator(context));

        logger = new Logger(RetrofitBase.class.getSimpleName());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ApiPaths.BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }


    public static void saveAuthToken(JsonObject jsonObject) {
        final String accessToken = jsonObject.getAsJsonPrimitive("accessToken").getAsString();


        UserRegistrationResponse userRegistrationResponse = SharedPref.getUserRegistration();
        userRegistrationResponse.setAccessToken(accessToken);
        SharedPref.write(SharedPref.USER, GsonUtils.getInstance().getGson().toJson(userRegistrationResponse));
        SharedPref.setUserRegistration(userRegistrationResponse);

    }

    private void addVersioningHeaders(OkHttpClient.Builder builder, Context context) {
        builder.interceptors().add(chain -> {
            Request.Builder requestBuilder = chain.request().newBuilder();
            Appinfo appInfo = new Appinfo(context);
            requestBuilder.addHeader("app_info", appInfo.getDate());
            if (SharedPref.read(SharedPref.IS_REGISTERED, false)) {
                if (SharedPref.getUserRegistration() != null) {
                    final String token = SharedPref.getUserRegistration().getAccessToken();
                    if (token != null && token.length() > 0)
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                    if (SharedPref.getUserRegistration().getId() != null)
                        requestBuilder.addHeader("logined_user_id", SharedPref.getUserRegistration().getId());
                }
            }
            return chain.proceed(requestBuilder.build());
        });
    }

    public static <S> S createRxResource(Context context, Class<S> serviceClass) {

        OkHttpClient client = getOkHttpClient(context);
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit.create(serviceClass);
    }

    @NonNull
    private static OkHttpClient getOkHttpClient(Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {
            okhttp3.Request original = chain.request();
            final Request.Builder builder = original.newBuilder()
                    .method(original.method(), original.body());


            if (SharedPref.getUserRegistration() != null) {
                Appinfo appInfo = new Appinfo(context);
                builder.header("app_info",appInfo.getDate());
                final String token = SharedPref.getUserRegistration().getAccessToken();
                if (token != null && token.length() > 0)
                    builder.header("Authorization", "Bearer " + token);
                if (SharedPref.getUserRegistration().getId() != null)
                    builder.header("logined_user_id", SharedPref.getUserRegistration().getId());
            }
            okhttp3.Request request = builder.build();
            return chain.proceed(request);
        });

        if(BuildConfig.DEBUG ){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }
        httpClient.authenticator(new JwtAuthenticator(context));

        return httpClient.build();
    }


    void validateResponse(Response response, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener, int apiFlag) {
        if (response.code() == 200) {
            ResponseBody responseBody = (ResponseBody) response.body();
            try {
                retrofitListener.onResponseSuccess(responseBody.string(), apiCallbackParams, apiFlag);
            } catch (IOException e) {
                error(response, apiCallbackParams, retrofitListener, apiFlag);
            }
        } else {
            error(response, apiCallbackParams, retrofitListener, apiFlag);
        }
    }

    private void error(Response response, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener, int apiFlag) {
        ErrorData errorPojo;
        try {
            errorPojo = ErrorParser.parseError(context, (response.errorBody()).string());
            retrofitListener.onResponseError(errorPojo, apiCallbackParams, null, apiFlag);
        } catch (Exception e) {
            retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, null, apiFlag);
        }
    }

    private static class JwtAuthenticator implements Authenticator {
        private final Context context;

        public JwtAuthenticator(Context context) {
            this.context = context;
        }

        //its covwerd in pin 84
        @Override
        public Request authenticate(Route route, okhttp3.Response response) throws IOException {
            if (responseCount(response) >= 2) {
                // If both the original call and the call with refreshed token failed,
                // it will probably keep failing, so don't try again.
                return null;
            }

            // We need a new client, since we don't want to make another call using our authenticated client
            final ApiServices apiServices = createRxResourceWithoutAuth(ApiServices.class, context);

            try {

                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("refresh_token", SharedPref.getUserRegistration().getRefreshToken());
                jsonObject1.addProperty("phone", SharedPref.getUserRegistration().getMobileNumber());
                RequestBody requestBody = RequestBody.create(jsonObject1.toString(), MediaType.parse("application/json; charset=utf-8"));

                final JsonObject jsonObject = apiServices.delegation(requestBody).blockingFirst();
                if (jsonObject != null) {
                    saveAuthToken(jsonObject);
                    final String token = SharedPref.getUserRegistration().getAccessToken();

                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                }
            } catch (Exception ignore) {


                UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
                SharedPref.write(SharedPref.IS_REGISTERED, false);
                SharedPref.write(SharedPref.USER, GsonUtils.getInstance().getGson().toJson(userRegistrationResponse));
                SharedPref.setUserRegistration(userRegistrationResponse);

                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

            }

            return null;
        }

        private int responseCount(okhttp3.Response response) {
            int result = 1;
            while ((response = response.priorResponse()) != null) {
                result++;
            }
            return result;
        }
    }

    public static <S> S createRxResourceWithoutAuth(Class<S> serviceClass,Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            okhttp3.Request original = chain.request();

            Appinfo appInfo = new Appinfo(context);
            final Request.Builder builder = original.newBuilder()
                    .header("app_info",appInfo.getDate())
                    .method(original.method(), original.body());
            okhttp3.Request request = builder.build();
            return chain.proceed(request);
        });

        if(BuildConfig.DEBUG ){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        OkHttpClient client = httpClient.build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit.create(serviceClass);
    }
}
