package com.sysout.app.serial;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.pl.sphelper.SPHelper;
import com.sysout.app.serial.utils.DBUtils;
import com.tencent.bugly.Bugly;
import com.tencent.mmkv.MMKV;

public class AppApplication extends Application {

    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        SPHelper.init(sInstance);
        DBUtils.getInstance().initDB(this);
        MMKV.initialize(this.getFilesDir().getAbsolutePath() + "/mmkv");
        Bugly.init(this.getApplicationContext(), "525d37cba2", BuildConfig.DEBUG);
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }
}
