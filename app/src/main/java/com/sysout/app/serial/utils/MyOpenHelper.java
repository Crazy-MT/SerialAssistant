package com.sysout.app.serial.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.sysout.app.serial.db.DaoMaster;
import com.sysout.app.serial.db.SerialCommandDao;

import org.greenrobot.greendao.database.Database;

/**
 * greedao数据库升级辅助类
 * 进行数据库升级以及数据迁移
 */
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //把需要管理的数据库表DAO作为最后一个参数传入到方法中, 创建一个临时表，将旧表的数据迁移到新表中
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, SerialCommandDao.class);
    }
}
