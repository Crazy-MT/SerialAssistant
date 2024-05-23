package com.sysout.app.serial.db;

import com.sysout.app.serial.entity.SerialCommand;
import com.sysout.app.serial.utils.DBUtils;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;

public class SerialCommandServiceImpl {

    public static SerialCommandServiceImpl getInstance(){
        return SerialCommandServiceImpl.Create.portUtil;
    }

    private static class Create {
        private static SerialCommandServiceImpl portUtil = new SerialCommandServiceImpl();

        private Create() {
        }

        static {
            portUtil.init();
        }
    }

    private void init(){

    }

    public long saveSerialCommand(SerialCommand data) {
        try {
            SerialCommandDao dao = DBUtils.getDaoSession().getSerialCommandDao();
            return dao.insert(data);
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateSerialCommand(SerialCommand data){
        try {
            SerialCommandDao dao = DBUtils.getDaoSession().getSerialCommandDao();
            dao.update(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSerialCommand(SerialCommand data){
        try{
            SerialCommandDao dao = DBUtils.getDaoSession().getSerialCommandDao();
            dao.delete(data);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSerialCommandByKey(long id){
        try{
            SerialCommandDao dao = DBUtils.getDaoSession().getSerialCommandDao();
            DeleteQuery<SerialCommand> bd = dao.queryBuilder().where(SerialCommandDao.Properties.Id.eq(id)).buildDelete();
            bd.executeDeleteWithoutDetachingEntities();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<SerialCommand> queryByMode(int mode) {
        try {
            SerialCommandDao dao = DBUtils.getDaoSession().getSerialCommandDao();
            List<SerialCommand> commands =  dao.queryBuilder().where(SerialCommandDao.Properties.Type.eq(mode)).list();
            return commands;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
