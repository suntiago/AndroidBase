package com.suntiago.baseui.account;

/**
 * Created by zy on 2018/8/2.
 */

public interface AccountCallback {
    //登出
    void logout(String userId, String token);

    //验证账户是否有效
    void checkAccount(String userId, String token);

}
