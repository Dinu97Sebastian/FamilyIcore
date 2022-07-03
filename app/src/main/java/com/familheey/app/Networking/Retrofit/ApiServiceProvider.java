package com.familheey.app.Networking.Retrofit;

import android.content.Context;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.Request.CreateFamilyRequest;
import com.familheey.app.Models.Request.LoginRequest;
import com.familheey.app.Models.Request.OTPRequest;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Models.Request.RequstComment;
import com.familheey.app.Models.Request.ResendOTP;
import com.familheey.app.Models.Request.SimilarFamilyRequest;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.Networking.utils.HttpUtil;
import com.familheey.app.Utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.familheey.app.Utilities.Constants.ApiPaths.SOCKET_COMMENT_URL;

public class ApiServiceProvider extends RetrofitBase {
    private static ApiServiceProvider apiServiceProvider;
    public ApiServices apiServices;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final Gson gson;

    private ApiServiceProvider(Context context) {
        super(context, false);
        apiServices = retrofit.create(ApiServices.class);
        gson = new Gson();
    }

    public static ApiServiceProvider getInstance(Context context) {
        if (apiServiceProvider == null) {
            apiServiceProvider = new ApiServiceProvider(context);
        }
        return apiServiceProvider;
    }

    public void registerUser(LoginRequest loginRequest, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(gson.toJson(loginRequest), JSON);
        @NotNull Call<ResponseBody> call = apiServices.registerUser(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.VALIDATE_MOBILE_NUMBER);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.VALIDATE_MOBILE_NUMBER);
            }
        });
    }

    public void confirmOTP(OTPRequest otpRequest, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(gson.toJson(otpRequest), JSON);
        @NotNull Call<ResponseBody> call = apiServices.confirmOTP(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CONFIRM_OTP);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CONFIRM_OTP);
            }
        });
    }

    public void resendOTP(ResendOTP resendOTP, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        //JsonObject jsonObject = new JsonObject();
       // jsonObject.addProperty("phone", mobileNumber);
        RequestBody requestBody = RequestBody.create(gson.toJson(resendOTP), JSON);
        @NotNull Call<ResponseBody> call = apiServices.resendOTP(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.RESEND_OTP);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.RESEND_OTP);
            }
        });
    }

    public void completeUserRegistration(UserRegistrationResponse userRegistration, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {

        MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder()
                .setType(MediaType.parse("multipart/form-data"));

        if (userRegistration.getId() != null && userRegistration.getId().length() > 0)
            multiPartBodyBuilder.addFormDataPart("id", userRegistration.getId());

        if (userRegistration.getFullName() != null && userRegistration.getFullName().length() > 0)
            multiPartBodyBuilder.addFormDataPart("full_name", userRegistration.getFullName());

        if (userRegistration.getEmail() != null && userRegistration.getEmail().length() > 0)
            multiPartBodyBuilder.addFormDataPart("email", userRegistration.getEmail());

        if (userRegistration.getPassword() != null && userRegistration.getPassword().length() > 0)
            multiPartBodyBuilder.addFormDataPart("password", userRegistration.getPassword());

        if (userRegistration.getMobileNumber() != null && userRegistration.getMobileNumber().length() > 0)
            multiPartBodyBuilder.addFormDataPart("phone", userRegistration.getMobileNumber());

        if (userRegistration.getLat() != null && userRegistration.getLng() != null) {
            multiPartBodyBuilder.addFormDataPart("lat", String.valueOf(userRegistration.getLat()));
            multiPartBodyBuilder.addFormDataPart("long", String.valueOf(userRegistration.getLng()));
        }

        if (userRegistration.getCapturedImage() != null && userRegistration.getCapturedImage().length() > 0) {
            File profilePictureFile = userRegistration.getProfilePictureFile();
            final MediaType MEDIA_TYPE = userRegistration.getCapturedImage().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, profilePictureFile);
            multiPartBodyBuilder.addFormDataPart("propic", profilePictureFile.getName(), profilePictureRequestBody);
        }

        if (userRegistration.getLocation() != null && userRegistration.getLocation().length() > 0)
            multiPartBodyBuilder.addFormDataPart("location", userRegistration.getLocation());


        if (userRegistration.getOrigin() != null && userRegistration.getOrigin().length() > 0)
            multiPartBodyBuilder.addFormDataPart("origin", userRegistration.getOrigin());


        if (userRegistration.getDob() != null && userRegistration.getDob().length() > 0)
            multiPartBodyBuilder.addFormDataPart("dob", userRegistration.getDob());


        if (userRegistration.getGender() != null && userRegistration.getGender().length() > 0)
            multiPartBodyBuilder.addFormDataPart("gender", userRegistration.getGender());

        if (userRegistration.getOtp() != null && userRegistration.getOtp().length() > 0)
            multiPartBodyBuilder.addFormDataPart("otp", userRegistration.getOtp());


        multiPartBodyBuilder.addFormDataPart("is_active", String.valueOf(true));

        @NotNull Call<ResponseBody> call = (userRegistration.getId() != null && userRegistration.getId().length() > 0) ? apiServices.completeUserRegistration(multiPartBodyBuilder.build()) : apiServices.registerUser(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.COMPLETE_USER_REGISTRATION);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.COMPLETE_USER_REGISTRATION);
            }
        });
    }



    public void createFamily(CreateFamilyRequest createFamilyRequest, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(gson.toJson(createFamilyRequest), JSON);
        @NotNull Call<ResponseBody> call = apiServices.createFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_FAMILY);
            }
        });
    }

    public void updateFamily(JsonObject updateJson, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(updateJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_FAMILY);
            }
        });
    }

    public void updateFamily(MultipartBody multiPartBody, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {

        @NotNull Call<ResponseBody> call = apiServices.updateFamily(multiPartBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_FAMILY);
            }
        });
    }

    public void fetchFamiliesForLinking(JsonObject requestJson, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchFamiliesForLinking(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_FAMILY_FOR_LINKING);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_FAMILY_FOR_LINKING);
            }
        });
    }

    public void fetchSimilarFamilies(SimilarFamilyRequest similarFamilyRequest, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(GsonUtils.getInstance().toJson(similarFamilyRequest), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchSimilarFamilies(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_SIMILAR_FAMILIES);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_SIMILAR_FAMILIES);
            }
        });
    }

    public void joinFamily(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.joinFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.JOIN_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.JOIN_FAMILY);
            }
        });
    }

    public void requestFamiliesForLinking(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.requestFamiliesForLinking(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.REQUEST_FAMILY_LINKING);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.REQUEST_FAMILY_LINKING);
            }
        });
    }

    public void listAllFamily(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.listAllfamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LIST_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LIST_FAMILY);
            }
        });
    }

    public void listAllFamilyPost(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.listAllfamilyPost(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LIST_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LIST_FAMILY);
            }
        });
    }


    public void eventGroupInvite(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.eventGroupInvite(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.EVENT_GROUP_INVITE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.EVENT_GROUP_INVITE);
            }
        });
    }


    public void listMember(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.listMember(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LIST_MEMBER);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LIST_FAMILY);
            }
        });
    }


    public void searchData(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.searchData(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.SEARCH_DATA);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.SEARCH_DATA);
            }
        });
    }


    public void getFamilyDetailsByID(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getFamilyDetailsByID(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_FAMILY_DETAILS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_FAMILY_DETAILS);
            }
        });
    }
/**ticket 693**/
    public void getMobileDetailsFromUserId(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getMobileDetailsFromUserId(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.MOBILE_NUMBER_DETAILS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.MOBILE_NUMBER_DETAILS);
            }
        });
    }
    /**end**/
    /**For background create post**/
    public void createPostRequestInBackground(PostRequest requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(GsonUtils.getInstance().toJson(requestJson), JSON);
        @NotNull Call<ResponseBody> call = apiServices.createPostRequestInBackground(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_POST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_POST);
            }
        });
    }

    /**For background create post**/
    public void updatePostInBackground(PostRequest requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(GsonUtils.getInstance().toJson(requestJson), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updatePostInBackground(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_POST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_POST);
            }
        });
    }








    public void addToFamily(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.addToFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.ADD_TO_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.ADD_TO_FAMILY);
            }
        });
    }

    public void viewFamilyMembers(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.viewFamilyMembers(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.VIEW_FAMILY_MEMBERS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.VIEW_FAMILY_MEMBERS);
            }
        });
    }

    public void viewMemberRequest(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.viewMemberRequest(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.VIEW_MEMBER_REQUEST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.VIEW_MEMBER_REQUEST);
            }
        });


    }

    public void memerRequestAction(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.memberRequestAction(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.MEMBER_REQUEST_ACTION);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.MEMBER_REQUEST_ACTION);
            }
        });


    }

    public void getallRelations(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getallRelations(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_ALL_RELATIONS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_ALL_RELATIONS);
            }
        });
    }

    public void addRelation(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.addRelation(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.ADD_RELATION);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.ADD_RELATION);
            }
        });
    }

    public void updateRelation(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateRelation(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_RELATIONS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_RELATIONS);
            }
        });
    }

    public void updateUserRestrictionStatus(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateUserRestrictionStatus(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_USER_RESTRICTIONS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_USER_RESTRICTIONS);
            }
        });
    }


    public void listGroupFolders(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.listGroupFolders(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LIST_GROUP_FOLDERS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LIST_GROUP_FOLDERS);
            }
        });
    }

    public void listUserFolders(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.listUserFolders(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LIST_USER_FOLDERS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LIST_USER_FOLDERS);
            }
        });
    }

    public void createFolder(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.createFolder(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_FOLDERS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_FOLDERS);
            }
        });
    }

    public void editFolder(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.editFolder(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.EDIT_FOLDERS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.EDIT_FOLDERS);
            }
        });
    }

    public void viewUserProfile(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.viewUserProfile(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.VIEW_USER_PROFILE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.VIEW_USER_PROFILE);
            }
        });
    }

    public void unFollowtheFamily(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.unFollowtheFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UNFOLLOW);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UNFOLLOW);
            }
        });
    }

    public void followtheFamily(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.FollowtheFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FOLLOW);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FOLLOW);
            }
        });
    }


    public void requestLinkFamily(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.requestLinkFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.REQUEST_LINK_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.REQUEST_LINK_FAMILY);
            }
        });
    }


    public void uploadFile(MultipartBody.Part requestBody, RequestBody folderId, RequestBody userId, RequestBody groupId, RequestBody isSharable, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.uploadFile(requestBody, folderId, userId, groupId, isSharable);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPLOAD_IMAGE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPLOAD_IMAGE);
            }
        });
    }

    public void uploadFile(MultipartBody.Builder multiPartBodyBuilder, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        @NotNull Call<ResponseBody> call = apiServices.uploadFile(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPLOAD_IMAGE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPLOAD_IMAGE);
            }
        });
    }

    public void uploadAlbumFile(List<MultipartBody.Part> imageFiles, RequestBody folderId, RequestBody userId, RequestBody isSharable, RequestBody folderType, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.uploadAlbumFile(imageFiles, folderId, userId, isSharable, folderType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPLOAD_IMAGE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPLOAD_IMAGE);
            }
        });

    }

    public void viewContents(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.viewContents(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.VIEW_CONTENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.VIEW_CONTENTS);
            }
        });
    }

    public void fetchEventcategory(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchEventcategory(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.EVENT_CATEGORY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.EVENT_CATEGORY);
            }
        });
    }

    public void createEvents(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.createEvents(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_EVENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_EVENTS);
            }
        });
    }

    public void updateEvents(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateEvents(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_EVENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_EVENTS);
            }
        });
    }

    public void inviteViaSms(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.inviteViaSms(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.INVITE_VIA_SMS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.INVITE_VIA_SMS);
            }
        });
    }

    public void createAgenda(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.createAgenda(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_AGENDA);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_AGENDA);
            }
        });
    }

    public void listAgenda(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.listAgenda(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LIST_AGENDA);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LIST_AGENDA);
            }
        });
    }

    public void updateUserProfile(MultipartBody.Builder multiPartBodyBuilder, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        @NotNull Call<ResponseBody> call = apiServices.completeUserRegistration(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_USER_PROFILE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_USER_PROFILE);
            }
        });
    }

    public void updateFamilyHistoryImage(MultipartBody.Builder multiPartBodyBuilder, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        @NotNull Call<ResponseBody> call = apiServices.updateFamilyHistoryImage(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_FAMILY_HISTORY_IMAGE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_FAMILY_HISTORY_IMAGE);
            }
        });
    }


    public void updateFamily(MultipartBody.Builder multiPartBodyBuilder, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        @NotNull Call<ResponseBody> call = apiServices.updateFamily(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_USER_PROFILE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_USER_PROFILE);
            }
        });
    }

    public void EdiFamily(MultipartBody.Builder multiPartBodyBuilder, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        @NotNull Call<ResponseBody> call = apiServices.updateFamily(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_FAMILY);
            }
        });
    }

    public void fetchUserInvitations(JsonObject updateJson, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(updateJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchUserInvitations(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_USER_INVITATION);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_USER_INVITATION);
            }
        });
    }

    public void respondToFamilyInvitation(JsonObject requestJson, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.respondToFamilyInvitation(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.RESPOND_FAMILY_INVITATION);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.RESPOND_FAMILY_INVITATION);
            }
        });
    }

    public void exploreEvents(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.exploreEvents(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.EXPLORE_EVENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.EXPLORE_EVENTS);
            }
        });
    }

    public void updateAgenda(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateAgenda(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_AGENDA);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_AGENDA);
            }
        });
    }

    public void deleteAgenda(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateAgenda(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.DELETE_AGENDA);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.DELETE_AGENDA);
            }
        });
    }

    public void createdByMe(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.createdByMe(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATED_BY_ME);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATED_BY_ME);
            }
        });
    }

    public void getEventById(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getEventById(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_EVENT_BY_ID);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_EVENT_BY_ID);
            }
        });
    }

    public void respondToRSVP(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.respondToRSVP(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.RESPOND_TO_RSVP);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.RESPOND_TO_RSVP);
            }
        });
    }


    public void eventInvitation(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.eventInvitation(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.EVENT_INVITATION);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.EVENT_INVITATION);
            }
        });
    }

    public void getAllGroupsBasedOnUserId(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getAllGroupsBasedOnUserId(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_GROUPS_BASED_ON_USER_ID);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_GROUPS_BASED_ON_USER_ID);
            }
        });
    }


    public void getEventItems(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getEventItems(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_EVENT_ITEMS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_EVENT_ITEMS);
            }
        });
    }


    public void addEventSignup(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.addEventSignup(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.ADD_EVENT_SIGNUP);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.ADD_EVENT_SIGNUP);
            }
        });
    }

    public void callUpdateEventSignupApi(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.callUpdateEventSignupApi(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_EVENT_SIGNUP);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_EVENT_SIGNUP);
            }
        });
    }

    public void listEventFolders(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.listEventFolders(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LIST_EVENT_ALBUMS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LIST_EVENT_ALBUMS);
            }
        });
    }

    public void makePicCover(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.makePicCover(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.MAKE_PIC_COVER);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.MAKE_PIC_COVER);
            }
        });
    }

    public void deleteFile(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.deleteFile(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.DELETE_FILE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.DELETE_FILE);
            }
        });
    }

    public void addHistory(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.addHistory(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.ADD_HISTORY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.ADD_HISTORY);
            }
        });
    }

    public void fetchGroupEvents(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchGroupEvents(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_GROUP_EVENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_GROUP_EVENTS);
            }
        });
    }
//Dinu(30-07-2021) for fetch events
    public void fetchFamilyGroupEvents(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchFamilyGroupEvents(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_GROUP_EVENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_GROUP_EVENTS);
            }
        });
    }

    public void getGuestCount(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getGuestCount(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GUEST_COUNT);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GUEST_COUNT);
            }
        });
    }

    public void goingGuestDetails(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.goingGuestDetails(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_GROUP_EVENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_GROUP_EVENTS);
            }
        });
    }


    public void getRsvpGuestList(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getRsvpGuestList(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.MAY_ATTENDING_GUEST_LIST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.MAY_ATTENDING_GUEST_LIST);
            }
        });
    }

    public void getInvitedGuestList(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getInvitedGuestList(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_INVITED_GUEST_LIST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_INVITED_GUEST_LIST);
            }
        });
    }

    public void fetchLinkedFamilies(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchLinkedFamilies(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_LINKED_FAMILIES);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_LINKED_FAMILIES);
            }
        });
    }


    public void fetchCalendar(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchCalendar(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_CALENDAR);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_CALENDAR);
            }
        });
    }

    public void getBlockedUsers(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getBlockedUsers(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.FETCH_BLOCKED_USERS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.FETCH_BLOCKED_USERS);
            }
        });
    }

    public void updateAgendaMultipart(MultipartBody.Part body, RequestBody user_id, RequestBody event_id, RequestBody title, RequestBody description, RequestBody start_date, RequestBody end_date, RequestBody agenda_id, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {


        @NotNull Call<ResponseBody> call = apiServices.updateAgendaMultipart(body, user_id, event_id, title, description, start_date, end_date, agenda_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_AGENDA);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_AGENDA);
            }
        });
    }

    public void createAgendaMultipart(MultipartBody.Part body, RequestBody user_id, RequestBody event_id, RequestBody title, RequestBody description, RequestBody start_date, RequestBody end_date, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {


        @NotNull Call<ResponseBody> call = apiServices.createAgendaMultipart(body, user_id, event_id, title, description, start_date, end_date);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CREATE_AGENDA);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CREATE_AGENDA);
            }
        });

    }

    public void leaveFamily(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.leaveFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.LEAVE_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.LEAVE_FAMILY);
            }
        });
    }

    public void requestUnlinkFamily(JsonObject requestJson, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.requestUnlinkFamily(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.REQUEST_UNLINK_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.REQUEST_UNLINK_FAMILY);
            }
        });
    }

    public void viewPeopleforShare(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.viewPeopleforShare(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.VIEW_PEOPLE_FOR_SHARE);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.VIEW_PEOPLE_FOR_SHARE);
            }
        });
    }

    public void addSignUpDetails(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.addSignUpDetails(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.ADD_EVENTS_SIGNUP);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.ADD_EVENTS_SIGNUP);
            }
        });
    }

    public void updateSignUpDetails(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateSignUpDetails(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_EVENTS_SIGNUP);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_EVENTS_SIGNUP);
            }
        });
    }

    public void fetchEventSignUpContributors(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.fetchEventSignUpContributors(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_EVENT_SIGNUP_CONTRIBUTORS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_EVENT_SIGNUP_CONTRIBUTORS);
            }
        });
    }

    public void addFeedback(MultipartBody.Builder multiPartBodyBuilder, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        @NotNull Call<ResponseBody> call = apiServices.addFeedback(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.ADD_FEEDBACK);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.ADD_FEEDBACK);
            }
        });
    }

    public void checkLink(String text, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", text);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.checkLink(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.CHECK_LINK);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.CHECK_LINK);
            }
        });
    }

    public void getMutualConnections(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getMutualConnections(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_MUTUAL_CONNECTIONS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_MUTUAL_CONNECTIONS);
            }
        });
    }

    public void getFamilyMutualConnections(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getFamilyMutualConnections(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_MUTUAL_CONNECTION_LIST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_MUTUAL_CONNECTION_LIST);
            }
        });
    }

    public void getUserConnections(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getUserConnections(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_OTHER_USERS_FAMILY);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_OTHER_USERS_FAMILY);
            }
        });
    }

    public void updateEventMedias(MultipartBody.Builder multiPartBodyBuilder, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        @NotNull Call<ResponseBody> call = apiServices.updateEventMedias(multiPartBodyBuilder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_EVENT);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_EVENT);
            }
        });
    }

    public void delterOrCancelEvent(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.delterOrCancelEvent(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.DELETE_CANCEL_EVENT);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.DELETE_CANCEL_EVENT);
            }
        });
    }



    public void addEventContact(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.addEventContact(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.ADD_EVENT_CONTACTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.ADD_EVENT_CONTACTS);
            }
        });
    }


    public void deleteEventContact(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.deleteEventContact(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.DEL_EVENT_CONTACTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.DEL_EVENT_CONTACTS);
            }
        });
    }


    public void editEventContact(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.editEventContact(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.EDIT_EVENT_CONTACTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.EDIT_EVENT_CONTACTS);
            }
        });
    }


    public void postComment(RequstComment requstComment, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {

        @NotNull Call<ResponseBody> call = apiServices.postComment(SOCKET_COMMENT_URL + "posts/post_comment", requstComment);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.POST_COMMENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.POST_COMMENTS);
            }
        });
    }

    public void postDelete(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.postComment(SOCKET_COMMENT_URL + "posts/delete_comment", requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.POST_COMMENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.POST_COMMENTS);
            }
        });
    }

    public void getCommentsByPost(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getComments(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_COMMENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_COMMENTS);
            }
        });
    }
    //Dinu(03-09-2021)--to fetch post comment replies
    public void getCommentReplies(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getCommentReplies(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_COMMENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_COMMENTS);
            }
        });
    }

    public void deleteSignup(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.deleteSignup(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.GET_COMMENTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.GET_COMMENTS);
            }
        });
    }

    public void removeFolder(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.removeFolder(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.REMOVE_FOLDER);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.REMOVE_FOLDER);
            }
        });
    }

    public void getMutualFamilies(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getMutualFamilies(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.MUTUAL_FAMILIES);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.MUTUAL_FAMILIES);
            }
        });
    }



    public void updateFileName(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.updateFileName(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.UPDATE_FILE_NAME);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.UPDATE_FILE_NAME);
            }
        });
    }

    public void getPendingRequests(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.getPendingRequests(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.PENDING_REQUEST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.PENDING_REQUEST);
            }
        });
    }

    public void deletePendingRequest(JsonObject jsonObject, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        @NotNull Call<ResponseBody> call = apiServices.deletePendingRequest(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.DELETE_PENDING_REQUEST);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.DELETE_PENDING_REQUEST);
            }
        });
    }


    public Call<ResponseBody> uploadSingle(RequestBody description, MultipartBody.Part part, ApiCallbackParams apiCallbackParams, RetrofitListener retrofitListener) {

        @NotNull Call<ResponseBody> call = apiServices.uploadSingle(description, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.BULK_UPDATE_CONTACTS);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.BULK_UPDATE_CONTACTS);
            }
        });
        return call;
    }
    public void validatePhoneNumber(LoginRequest loginRequest, ApiCallbackParams apiCallbackParams, final RetrofitListener retrofitListener) {
        RequestBody requestBody = RequestBody.create(gson.toJson(loginRequest), JSON);
        @NotNull Call<ResponseBody> call = apiServices.validatePhoneNumber(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                validateResponse(response, apiCallbackParams, retrofitListener, Constants.ApiFlags.VERIFIED_MOBILE_NUMBER);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                retrofitListener.onResponseError(HttpUtil.getServerErrorPojo(context), apiCallbackParams, t, Constants.ApiFlags.VALIDATE_MOBILE_NUMBER);
            }
        });
    }


}
