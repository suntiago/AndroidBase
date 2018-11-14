package viroyal.com.baseui.activity.splash;

import com.google.gson.annotations.SerializedName;

import viroyal.com.network.network.rsp.BaseResponse;

public class ApiResponse extends BaseResponse {
    @SerializedName("extra")
    public ApiModel apiModel;
}
