/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sysout.app.serial.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.button.MaterialButton;
import com.sysout.app.serial.R;


/**
 * ================================================
 * Created by JessYan on 16/04/2018 17:01
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class AdvancedSettingDialog extends Dialog {

    private Context mContext;

    private int parity = 0;
    private int dataBits = 8;
    private int stopBit = 1;

    private OnCenterItemClickListener listener;

    public AdvancedSettingDialog(@NonNull Context context, int parity, int dataBits, int stopBit) {
        super(context, R.style.public_dialog_progress);
        this.mContext = context;
        setContentView(R.layout.dialog_advanced_setting);
        setCanceledOnTouchOutside(true);

        this.parity = parity;
        this.dataBits = dataBits;
        this.stopBit = stopBit;

        initView();
    }

    public interface OnCenterItemClickListener {
        void OnCenterItemClick(int parity, int dataBits, int stopBit);
    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    private void initView() {
        Spinner paritySpinner = findViewById(R.id.serial_sp_parity);
        Spinner dataBitsSpinner = findViewById(R.id.serial_sp_dataBits);
        Spinner stopBitSpinner = findViewById(R.id.serial_sp_stopBit);
        MaterialButton commit = findViewById(R.id.serial_advanced_setting_commit);

        paritySpinner.setSelection(parity, true);
        paritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] dataList = mContext.getResources().getStringArray(R.array.serial_parity_array);
                if (dataList.length > 0) {
                    String data = dataList[position];
                    parity = "Even".equals(data)?2:"Odd".equals(data)?1:0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dataBitsSpinner.setSelection(dataBits - 5, true);
        dataBitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] dataList = mContext.getResources().getStringArray(R.array.serial_data_bits_array);
                if (dataList.length > 0) {
                    String data = dataList[position];
                    dataBits = Integer.parseInt(data);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stopBitSpinner.setSelection(stopBit - 1, true);
        stopBitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] dataList = mContext.getResources().getStringArray(R.array.serial_stop_bit_array);
                if (dataList.length > 0) {
                    String data = dataList[position];
                    stopBit = Integer.parseInt(data);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        commit.setOnClickListener(view -> {
            if(null != listener) {
                listener.OnCenterItemClick(parity, dataBits, stopBit);
            }
            dismiss();
        });
    }

}
