package com.sysout.app.serial.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.sysout.app.serial.R;
import com.tbruyelle.rxpermissions2.RxPermissions;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            if(aBoolean){
                new Handler().postDelayed(() -> {
                    if(isDestroyed() || isFinishing()){
                        return;
                    }
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 2000);
            } else {
                Toast.makeText(SplashActivity.this, "未授权权限，部分功能无法使用！", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    if(isDestroyed() || isFinishing()){
                        return;
                    }
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 2000);
            }
        });
    }

}