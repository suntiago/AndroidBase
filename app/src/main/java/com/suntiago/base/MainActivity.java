package com.suntiago.base;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.suntiago.baseui.activity.SlothActivity;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.filterview.flowLayout.ProvincePicker;
import com.suntiago.getpermission.rxpermissions.RxPermissions;
import com.suntiago.lockpattern.PatternManager;
import com.suntiago.network.network.download.DataChanger;
import com.suntiago.network.network.download.DataWatcher;
import com.suntiago.network.network.download.DownloadEntry;
import com.suntiago.network.network.download.DownloadManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends SlothActivity {

    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testPatternLok();
    }

    public void pickProvince(View view) {
        pickerProvince();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void handleToast(int i, String s) {
        
    }

    public void pickerProvince() {
        ProvincePicker provincePicker = new ProvincePicker(this, a -> {
        });
        provincePicker.showPopWindow();
    }


    /*测试图案解锁*/
    public void testPatternLok() {
        PatternManager.get().accountLogin("1234");
        PatternManager.get().setPatternCallback(new PatternManager.PatternCallback() {
            @Override
            public void patternSet() {
                Slog.d(TAG, "patternSet  []:");
            }

            @Override
            public void patternForget() {
                Slog.d(TAG, "patternForget  []:");
            }

            @Override
            public void patternChecked() {
                Slog.d(TAG, "patternChecked  []:");
            }
        });
        if (PatternManager.get().isPatternSet()) {
            PatternManager.get().checkoutPattern();
        } else {
            PatternManager.get().setPattern();
        }
    }

    private DataChanger dataChanger;

    //测试下载模块的代码
    private void testDownload() {

        dataChanger = DataChanger.getInstance(this);
        DownloadManager.getInstance(this).addObserver(new DataWatcher() {

            @Override
            public void onDataChanged(DownloadEntry data) {
                Slog.d(TAG, "onDataChanged  [data]:" + "正在下载" + data.percent + "%");
            }
        });

        RxPermissions.getInstance(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        startDownload();
                    } else {
                        Slog.d(TAG, "uploadFile  [id, filepath, pkgName, action]:");
                    }
                });
    }

    //开始下载
    private void startDownload() {
        String url = "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=401714075," +
                "168034038&fm=173&app=25&f=JPEG?w=640&h=360&s=05D1AB6C2D27261D8CB1C49E0300009B";
        DownloadEntry entry;
        if (dataChanger.containsDownloadEntry(url)) {
            entry = dataChanger.queryDownloadEntryByUrl(url);
        } else {
            entry = new DownloadEntry(url);
            entry.name = url.substring(url.lastIndexOf("/") + 1);//apk名字
        }
        DownloadManager.getInstance(this).add(entry);
    }
}
