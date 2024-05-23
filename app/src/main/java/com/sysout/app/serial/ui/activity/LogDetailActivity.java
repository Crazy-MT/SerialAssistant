package com.sysout.app.serial.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.sysout.app.serial.R;
import com.sysout.app.serial.ui.dialog.ProgresDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LogDetailActivity extends AppCompatActivity {

    private TextView tvLogDetail;

    private ProgresDialog progresDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);

        initView();
    }

    private void initView() {
        Intent intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(null!=intent&&!ObjectUtils.isEmpty(intent.getStringExtra("name"))?intent.getStringExtra("name"):"记录详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvLogDetail = findViewById(R.id.tv_log_detail);

        if(null != intent && !ObjectUtils.isEmpty(intent.getStringExtra("path"))){
            String path = intent.getStringExtra("path");
            readTxt(path);
        }
    }

    private void readTxt(String path){
        progresDialog = new ProgresDialog(LogDetailActivity.this);
        progresDialog.show();
        new Thread(() -> {
            StringBuilder str = new StringBuilder();
            try {
                File urlFile = new File(path);
                InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);

                String mimeTypeLine = null;
                while ((mimeTypeLine = br.readLine()) != null) {
                    str.append(mimeTypeLine);
                    str.append("\r\n");
                }
                runOnUiThread(() -> {
                    tvLogDetail.setText(str.toString());
                    progresDialog.dismiss();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

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