package com.suntiago.base;

import com.suntiago.baseui.App;
import com.suntiago.baseui.utils.log.CrashHandler;
import com.suntiago.lockpattern.PatternManager;


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
