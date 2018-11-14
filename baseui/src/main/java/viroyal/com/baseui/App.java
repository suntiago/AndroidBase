package viroyal.com.baseui;

import android.app.Application;

import viroyal.com.baseui.utils.Slog;

/**
 * Created by zy on 2018/4/25.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initLogs();
    }

    private void initLogs() {
        boolean logEnable = true;
        Slog.setDebug(logEnable, logEnable);
        viroyal.com.network.network.utils.Slog.setDebug(logEnable, logEnable);
        viroyal.com.network.network.utils.Slog.setLogCallback(
                new viroyal.com.network.network.utils.Slog.ILog() {
                    @Override
                    public void i(String tag, String msg) {
                        Slog.i(tag, msg);
                    }

                    @Override
                    public void v(String tag, String msg) {
                        Slog.v(tag, msg);
                    }

                    @Override
                    public void d(String tag, String msg) {
                        Slog.d(tag, msg);
                    }

                    @Override
                    public void e(String tag, String msg) {
                        Slog.e(tag, msg);
                    }

                    @Override
                    public void state(String packName, String state) {
                        Slog.state(packName, state);
                    }
                });
    }
}
