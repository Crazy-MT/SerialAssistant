package com.sysout.app.serial.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sysout.app.serial.db.DaoMaster;
import com.sysout.app.serial.db.DaoSession;


public class DBUtils{
    public static DBUtils mDBUtils;
    public static SQLiteDatabase db = null;
    public static DaoSession daoSession = null;
    private DaoMaster.DevOpenHelper helper = null;
    private MyOpenHelper myOpenHelper;
    private static DaoMaster daoMaster = null;
    public  static final String DATABASE_NAME = "sysout.db";
    public static SQLiteDatabase getDb(){
        return db;
    }
    public static DaoSession getDaoSession(){
        return daoSession;
    }
    public static DBUtils getInstance(){
        if (mDBUtils == null){
            mDBUtils = new DBUtils();
        }
        return mDBUtils;
    }
    public boolean initDB(Context context){
        setUpDataBase(context);
        return true;
    }
    /**
     * 初始化要放在application中去
     * @param context
     */
    private void setUpDataBase(Context context){

        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        helper = new DaoMaster.DevOpenHelper(context, DemoConfig.UserDatabasePath+DATABASE_NAME,null);
        myOpenHelper = new MyOpenHelper(context, DATABASE_NAME,null);
        db = myOpenHelper.getWritableDatabase();
//        db = helper.getWritableDatabase();

        if(daoMaster == null){
            daoMaster = new DaoMaster(db);
        }
        if(daoSession == null){
            daoSession = daoMaster.newSession();
        }
    }

    public void dropAllTables(Context context){
        if (helper==null){
            helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
        }
        if (db==null){
            db = helper.getWritableDatabase();
        }
        if (daoMaster == null){
            daoMaster = new DaoMaster(db);
        }
        DaoMaster.dropAllTables(daoMaster.getDatabase(),true);
        DaoMaster.createAllTables(daoMaster.getDatabase(),true);
    }

}
