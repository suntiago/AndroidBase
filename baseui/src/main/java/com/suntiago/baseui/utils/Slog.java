package com.suntiago.baseui.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.hwangjr.rxbus.RxBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by shengpeng on 2015-09-17.
 */
@SuppressWarnings("ALL")
public final class Slog {
    public static boolean DEBUG = true;
    public static boolean SHOW_ACTIVITY_STATE = true;


    private static String LOG_PATH = "/viroyal/com.viroyal.vdrive.update/";// 日志文件在sdcard中的路径
    private static String LOG_FILE_FIRST_NAME = "com_viroyal_vdrive_update_";
    private final static String LOG_FILE_END_NAME = "_log.txt";
    private static int sLogLevel = 5;
    public static final int VERBOSE_L = 5;
    public static final int DEBUG_L = 4;
    public static final int INFO_L = 3;
    public static final int WARN_L = 2;
    public static final int ERROR_L = 1;

    private static int SDCARD_LOG_FILE_SAVE_NUM = 5;// sd卡中日志文件的最多保存个数
    private static final String ERROR_TYPE = "ERROR";
    private static final String WARN_TYPE = "WARNING";
    private static final String INFO_TYPE = "INFO";
    private static final String DEBUG_TYPE = "DEBUG";
    private static final String VERBOSE_TYPE = "VERBOSE";
    private static String sPath;

    private static String sSn = "";
    private static Context sContext;

    public static void init(Context context, String deviceMac) {
        sContext = context;
        sSn = deviceMac;
    }

    public static void init(Context context, String deviceMac,String pkgId) {
        sContext = context;
        sSn = deviceMac;
        LOG_PATH = "/viroyal/"+pkgId+"/";
        LOG_FILE_FIRST_NAME = pkgId.replace(".","_")+"_";
    }

    public static void setDebug(boolean debug, boolean showActivityStatus) {
        DEBUG = debug;
        SHOW_ACTIVITY_STATE = showActivityStatus;
    }

    private Slog() {
    }

    public static void i(String tag, String msg) {
        send("i", tag, msg);
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        send("v", tag, msg);
        if (DEBUG)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        send("d", tag, msg);
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        send("e", tag, msg);
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static final void state(String packName, String state) {
        if (SHOW_ACTIVITY_STATE)
            Log.d(packName, state);
    }

    public static void send(String e, String tag, String msg) {
        Rxlog r = new Rxlog();
        r.type = e;
        r.time = System.currentTimeMillis();
        r.log = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                " " + e + " " + tag + " " + msg + "";
        RxBus.get().post(r);
        writeLogToFile(r.type, tag, r.log);
        delFile();
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private static void writeLogToFile(String type, String tag, String text) {// 新建或打开日志文件

        try {
            String needWriteFile = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
            File file = getFile(needWriteFile);
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            String needWriteMessage = new String(text.getBytes(), "utf-8") + "\"\n" + "\n";
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除多余的日志文件,只保留SDCARD_LOG_FILE_SAVE_NUM个
     */
    public static void delFile() {// 删除日志文件
        List<File> files = getFileSort();
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                if (i >= SDCARD_LOG_FILE_SAVE_NUM && files.get(i).exists()) {
                    files.get(i).delete();
                }
            }
        }
    }

    public static File getFile(String time) {
        File savePath = new File(getSavePath());
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        File file = new File(savePath, LOG_FILE_FIRST_NAME + time
                 + LOG_FILE_END_NAME);
        return file;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {

        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            if (subfiles != null) {
                for (File file : subfiles) {
                    if (file.isDirectory()) {
                        getFiles(file.getAbsolutePath(), files);
                    } else {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    /**
     * 获取目录下所有文件(按时间倒序排序)
     *
     * @return
     */
    public static List<File> getFileSort() {
        String savePath = getSavePath();
        File save = new File(savePath);
        if (!save.exists()) {
            return null;
        }
        List<File> list = getFiles(savePath, new ArrayList<File>());

        if (list != null && list.size() > 0) {

            Collections.sort(list, (file, newFile) -> {
                if (file.lastModified() > newFile.lastModified()) {
                    return -1;
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    return 1;
                }

            });

        }

        return list;
    }

    public static String getSavePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            if (sContext != null) {
                StorageList storageList = new StorageList(sContext);
                sPath = storageList.getVolumePaths()[1];
            }
        }
        return sPath + LOG_PATH;
    }

    public static class StorageList {
        private Context mContext;
        private StorageManager mStorageManager;
        private Method mMethodGetPaths;

        public StorageList(Context context) {
            mContext = context;
            if (mContext != null) {
                mStorageManager = (StorageManager) mContext
                        .getSystemService(Activity.STORAGE_SERVICE);
                try {
                    mMethodGetPaths = mStorageManager.getClass()
                            .getMethod("getVolumePaths");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        public String[] getVolumePaths() {
            String[] paths = null;
            try {
                paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return paths;
        }
    }
}
