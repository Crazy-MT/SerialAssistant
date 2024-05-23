package com.sysout.app.serial.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sysout.app.serial.R;
import com.sysout.app.serial.constants.Constants;
import com.tencent.bugly.beta.Beta;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    private SwitchMaterial saveLogSwitch;
    private TextView tvUpdate;
    private TextView tvAboutUs;
    private TextView tvSaveLogPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveLogSwitch = findViewById(R.id.sw_save_log);
        tvUpdate = findViewById(R.id.serial_setting_update);
        tvAboutUs = findViewById(R.id.serial_about_us);
        tvSaveLogPath = findViewById(R.id.tv_save_log_path);
    }

    private void initData(){
        tvSaveLogPath.setText(Constants.LOGS_PATH);
        boolean saveLog = MMKV.defaultMMKV().decodeBool(Constants.MMKV_KEY_SAVE_LOG, false);
        saveLogSwitch.setChecked(saveLog);
        saveLogSwitch.setOnCheckedChangeListener((compoundButton, b) -> MMKV.defaultMMKV().encode(Constants.MMKV_KEY_SAVE_LOG, b));

        tvUpdate.setOnClickListener(view -> Beta.checkUpgrade());

        tvAboutUs.setOnClickListener(view -> startActivity(new Intent(SettingActivity.this, AboutMeActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}