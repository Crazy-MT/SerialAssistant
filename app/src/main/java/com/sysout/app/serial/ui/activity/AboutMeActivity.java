package com.sysout.app.serial.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.sysout.app.serial.R;
import com.tencent.bugly.beta.Beta;

public class AboutMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvAppVersion = findViewById(R.id.tv_app_version);
        String versionName = AppUtils.getAppVersionName();
        tvAppVersion.setText("Version " + (versionName==null?"":versionName));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFeaturesClick(View view){
        startActivity(new Intent(this, FeaturesActivity.class));
    }

    public void onCheckUpdateClick(View view){
        Beta.checkUpgrade();
    }
}