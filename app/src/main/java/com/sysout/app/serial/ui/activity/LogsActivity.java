package com.sysout.app.serial.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.sysout.app.serial.R;
import com.sysout.app.serial.constants.Constants;
import com.sysout.app.serial.db.SerialCommandServiceImpl;
import com.sysout.app.serial.entity.LogEntity;
import com.sysout.app.serial.entity.SerialCommand;
import com.sysout.app.serial.ui.adapter.CommandAdapter;
import com.sysout.app.serial.ui.adapter.LogAdapter;
import com.sysout.app.serial.ui.dialog.UpCommandDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogsActivity extends AppCompatActivity {

    private RecyclerView rvLogs;
    private List<LogEntity> logEntityList = new ArrayList<>();
    private LogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("数据记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvLogs = findViewById(R.id.rv_logs);

        initRecyclerView();
    }

    private void initRecyclerView() {
        //LinearLayoutManager是用来做列表布局，也就是单列的列表
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(layoutManager);
        //默认就是垂直方向的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //谷歌提供了一个默认的item删除添加的动画
        rvLogs.setItemAnimator(new DefaultItemAnimator());

        //谷歌提供了一个DividerItemDecoration的实现类来实现分割线
        //往往我们需要自定义分割线的效果，需要自己实现ItemDecoration接口
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvLogs.addItemDecoration(dividerItemDecoration);

        //当item改变不会重新计算item的宽高
        //调用adapter的增删改差方法的时候就不会重新计算，但是调用nofityDataSetChange的时候还是会
        //所以往往是直接先设置这个为true，当需要布局重新计算宽高的时候才调用nofityDataSetChange
//        serialRvCommand.setHasFixedSize(true);
        File pathFile = new File(Constants.LOGS_PATH);
        File[] files = pathFile.listFiles();
        if(null != files && files.length > 0){
            LogEntity logEntity;
            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                logEntity = new LogEntity();
                logEntity.setName(file.getName());
                logEntity.setPath(file.getAbsolutePath());
                logEntityList.add(logEntity);
            }
        }
        logAdapter = new LogAdapter(this, logEntityList);
        rvLogs.setAdapter(logAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        } else if (id == R.id.logs_menu_clear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setMessage(getString(R.string.serial_del_confirm)).setPositiveButton(getString(R.string.serial_commit), (dialogInterface, i) -> {
                File file = null;
                for (LogEntity logEntity : logEntityList) {
                    file = new File(logEntity.getPath());
                    if(file.exists()){
                        file.delete();
                    }
                }
                logEntityList.clear();
                logAdapter.notifyDataSetChanged();
            }).setNegativeButton(getString(R.string.serial_cancel), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

}