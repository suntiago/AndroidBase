package viroyal.com.baseui.lockPattern;

import android.content.Context;
import android.content.Intent;

import viroyal.com.baseui.lockPattern.util.ACache;

/**
 * Created by zy on 2018/9/6.
 */

public class PatternManager implements Pattern {
    public final static String GESTURE_PASSWORD = "GESTURE_PASSWORD";
    private static PatternManager sPatternManager;
    private Context sContext;

    String accountID = "";
    PatternCallback mPatternCallback;

    public static Pattern get() {
        return sPatternManager;
    }

    protected static PatternManager gett() {
        return sPatternManager;
    }

    private PatternManager(Context sContext) {
        this.sContext = sContext;
    }

    public static void init(Context context) {
        sPatternManager = new PatternManager(context);
    }

    public void setPatternCallback(PatternCallback patternCallback) {
        mPatternCallback = patternCallback;
    }

    /**
     * 密码设置回调
     */
    protected void patternSet() {
        if (mPatternCallback != null) {
            mPatternCallback.patternSet();
        }
    }

    /**
     * 忘记密码回调
     */
    protected void patternForget() {
        if (mPatternCallback != null) {
            mPatternCallback.patternForget();
        }
    }

    /**
     * 主账户登录
     */
    public void accountLogin(String accountId) {
        accountID = accountId;
    }

    /**
     * 验证pattern
     */
    public void checkoutPattern() {
        Intent i = new Intent(sContext, GestureLoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sContext.startActivity(i);
    }


    /**
     * 验证pattern
     */
    protected void patternChecked() {
        if (mPatternCallback != null) {
            mPatternCallback.patternChecked();
        }
    }

    protected String getAccountID() {
        return accountID;
    }

    /*主账户登出*/
    public void accountLoginout() {
        ACache aCache = ACache.get(sContext);
        aCache.put(GESTURE_PASSWORD + getAccountID(), (byte[]) null);
        accountID = "";
    }

    public void setPattert() {
        Intent i = new Intent(sContext, CreateGestureActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sContext.startActivity(i);
    }

    /**
     * 是否已经设置了图案解锁
     */
    public boolean isPatternSet() {
        ACache aCache = ACache.get(sContext);
        byte[] gesturePassword = aCache.getAsBinary(
                GESTURE_PASSWORD + getAccountID());
        if (gesturePassword != null) {
            return true;
        }
        return false;
    }

    public interface PatternCallback {
        void patternSet();

        void patternForget();

        void patternChecked();
    }
}