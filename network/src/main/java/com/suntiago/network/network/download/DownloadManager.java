package com.suntiago.network.network.download;


import android.content.Context;
import android.content.Intent;

import com.suntiago.network.network.utils.Slog;


/**
 *
 * @des DownloadManager
 */
public class DownloadManager {
    private final String TAG = getClass().getSimpleName();
    private static DownloadManager mInstance;
    private final Context context;
    private long lastOperateTime = 0;

    private DownloadManager(Context context) {
        this.context = context;
        context.startService(new Intent(context, DownloadService.class));
    }

    public synchronized static DownloadManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DownloadManager(context);
        }
        return mInstance;
    }

    private Intent getIntent(DownloadEntry entry, int action) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, action);
        return intent;
    }

    private boolean checkIfExecutable() {
        long temp = System.currentTimeMillis();
        if (temp - lastOperateTime > DownloadConfig.getInstance().getMin_operate_interval()) {
            lastOperateTime = temp;
            return true;
        }
        return false;
    }

    public void add(DownloadEntry entry) {
        //if (!checkIfExecutable()) return;
        Slog.d(TAG, "DownloadManager==>add()" + entry.url);
        context.startService(getIntent(entry, Constants.KEY_DOWNLOAD_ACTION_ADD));
    }

    public void pause(DownloadEntry entry) {
        //if (!checkIfExecutable()) return;
        Slog.d(TAG, "DownloadManager==>pause()");
        context.startService(getIntent(entry, Constants.KEY_DOWNLOAD_ACTION_PAUSE));
    }

    public void resume(DownloadEntry entry) {
        if (!checkIfExecutable()) return;
        Slog.d(TAG, "DownloadManager==>resume()");
        context.startService(getIntent(entry, Constants.KEY_DOWNLOAD_ACTION_RESUME));
    }

    public void cancel(DownloadEntry entry) {
        if (!checkIfExecutable()) return;
        Slog.d(TAG, "DownloadManager==>cancel()");
        context.startService(getIntent(entry, Constants.KEY_DOWNLOAD_ACTION_CANCEL));
    }

    public void pauseAll() {
        if (!checkIfExecutable()) return;
        Slog.d(TAG, "DownloadManager==>pauseAll()");
        context.startService(getIntent(null, Constants.KEY_DOWNLOAD_ACTION_PAUSE_ALL));
    }

    public void recoverAll() {
        if (!checkIfExecutable()) return;
        Slog.d(TAG, "DownloadManager==>recoverAll()");
        context.startService(getIntent(null, Constants.KEY_DOWNLOAD_ACTION_RECOVER_ALL));
    }

    public void addObserver(DataWatcher dataWatcher) {
        DataChanger.getInstance(context).addObserver(dataWatcher);
    }

    public void removeObserver(DataWatcher dataWatcher) {
        DataChanger.getInstance(context).deleteObserver(dataWatcher);
    }

    public void removeObservers() {
        DataChanger.getInstance(context).deleteObservers();
    }
}
