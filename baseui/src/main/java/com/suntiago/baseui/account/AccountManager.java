package com.suntiago.baseui.account;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.suntiago.network.network.utils.SPUtils;

/**
 * Created by zy on 2018/8/2.
 * 账户管理应用，适用于单用户系统
 */

public class AccountManager {
    private static String AccountManager_user_id = "AccountManager_user_id";
    private static String AccountManager_account_token = "AccountManager_account_token";

    private static AccountManager sAccountManager;
    private Context sContext;
    //账户ID
    private Account mAccount;
    private AccountCallback mAccountCallback;

    List<AccountStatusCallback> mAccountStatusCallbacks = new ArrayList<>();

    public static AccountManager get() {
        if (sAccountManager == null) {
            throw new NullPointerException("please init AccountManager first before getting");
        }
        return sAccountManager;
    }

    private AccountManager(Context sContext) {
        this.sContext = sContext;
        String account = SPUtils.getInstance(sContext).get(AccountManager_user_id);
        String token = SPUtils.getInstance(sContext).get(AccountManager_account_token);
        setAccount(account, token);
    }

    public static void init(Context context) {
        sAccountManager = new AccountManager(context);
    }

    public void setAccountCallback(AccountCallback accountCallback) {
        mAccountCallback = accountCallback;
        if (isLogin()) {
            checkAccount();
        }
    }


    public boolean isLogin() {
        if (mAccount != null && !TextUtils.isEmpty(mAccount.userId)) {
            return true;
        }
        return false;
    }

    private void checkAccount() {
        if (mAccount != null && mAccountCallback != null) {
            mAccountCallback.checkAccount(mAccount.userId, mAccount.token);
        }
    }

    public String getUserData(String key) {
        if (isLogin()) {
            String s = SPUtils.getInstance(sContext).get(key);
            if (s != null && s.startsWith(mAccount.userId)) {
                return s.substring(mAccount.userId.length(), s.length());
            }
        }
        return "";
    }

    public void putUserData(String key, String data) {
        if (isLogin()) {
            SPUtils.getInstance(sContext).put(key, mAccount.userId + data);
            if (mAccountStatusCallbacks.size() > 0) {
                for (AccountStatusCallback accountStatusCallback : mAccountStatusCallbacks) {
                    accountStatusCallback.userinfoChange(key);
                }
            }
        }
    }

    public void setAccount(String userId, String token) {
        SPUtils.getInstance(sContext).put(AccountManager_user_id, userId);
        SPUtils.getInstance(sContext).put(AccountManager_account_token, token);
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.userId = userId;
        mAccount.token = token;
        if (mAccountStatusCallbacks.size() > 0) {
            if (isLogin()) {
                for (AccountStatusCallback accountStatusCallback : mAccountStatusCallbacks) {
                    accountStatusCallback.loginCallback();
                }
            } else {
                for (AccountStatusCallback accountStatusCallback : mAccountStatusCallbacks) {
                    accountStatusCallback.logoutCallback();
                }
            }
        }
    }

    public void logout() {
        if (mAccountCallback != null && mAccount != null) {
            mAccountCallback.logout(mAccount.userId, mAccount.token);
        }
        setAccount("", "");
    }

    /*初次注册，立即回调一下*/
    public void registerAccountStatusCallBack(AccountStatusCallback statusCallback) {
        if (!mAccountStatusCallbacks.contains(mAccountStatusCallbacks)) {
            mAccountStatusCallbacks.add(statusCallback);
            if (isLogin()) {
                statusCallback.loginCallback();
            } else {
                statusCallback.logoutCallback();
            }
        }
    }

    public void unregisterAccountStatusCallBack(AccountStatusCallback statusCallback) {
        if (mAccountStatusCallbacks.contains(statusCallback)) {
            mAccountStatusCallbacks.remove(statusCallback);
        }
    }
}
