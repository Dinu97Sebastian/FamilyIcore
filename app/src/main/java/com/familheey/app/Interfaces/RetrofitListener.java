package com.familheey.app.Interfaces;

import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;

public interface RetrofitListener {
    void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag);

    void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag);

}
