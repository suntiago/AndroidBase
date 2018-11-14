package viroyal.com.baseui.activity.splash;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import viroyal.com.baseui.activity.MonitorActivity;
import viroyal.com.baseui.utils.Slog;
import viroyal.com.getpermission.rxpermissions.RxPermissions;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by zy on 2018/8/2.
 */

public class SplashIpActivity extends MonitorActivity {

    //正在加载弹出框
    private AlertDialog alertDialog;
    int mAPITime = 1000 * 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("配置加载")
                .setCancelable(false)
                .setMessage("正在加载配置信息...")
                .create();
        alertDialog.show();

        RxPermissions.getInstance(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        getApi(SplashIpActivity.this);
                    } else {
                        Slog.d(TAG, "uploadFile  [id, filepath, pkgName, action]:");
                    }
                });
    }

    public void getApi(Context context) {
        Slog.d(TAG, "getApi  [context]:");
        ConfigDevice.configIp(context, "", r -> {
            if (r.error_code == 1000) {
                alertDialog.dismiss();
                splashFinish();
            } else {
                Observable.timer(mAPITime, TimeUnit.MILLISECONDS).subscribe((v) -> {
                    Slog.d(TAG, "Observable.timer:regetApi");
                    getApi(SplashIpActivity.this);
                });
            }
        });
    }

    private void splashFinish() {
//        Intent intent = new Intent(context, MainStartService.class);
//        if (apichanged) {
//            intent.putExtra("apichanged", 1);
//        }
//        startService(intent);
    }

}
