package com.sysout.app.serial.constants;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static final String MMKV_KEY_SAVE_LOG = "serial_save_log";

    public static final String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sysout/";

    public static final String LOGS_PATH = APP_PATH + "logs/";

    static{
        File file = new File(LOGS_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
    }

}
