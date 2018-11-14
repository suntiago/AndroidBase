package viroyal.com.base;

import viroyal.com.baseui.App;
import viroyal.com.baseui.lockPattern.PatternManager;
import viroyal.com.baseui.utils.CrashHandler;

/**
 * Created by zy on 2018/9/6.
 */

public class MyApp extends App {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化异常log保存策略
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), BuildConfig.APPLICATION_ID);
        PatternManager.init(this);
    }
}
