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
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.blankj.utilcode.util.ObjectUtils;
import com.google.android.material.button.MaterialButton;
import com.sysout.app.serial.R;
import com.sysout.app.serial.db.SerialCommandServiceImpl;
import com.sysout.app.serial.entity.SerialCommand;
import com.sysout.app.serial.ui.activity.LoadCommandActivity;


/**
 * ================================================
 * Created by JessYan on 16/04/2018 17:01
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class UpCommandDialog extends Dialog {

    private Context mContext;

    private int writeMode = 0;
    private SerialCommand serialCommand;

    private OnCenterItemClickListener listener;

    public UpCommandDialog(@NonNull Context context, int writeMode, SerialCommand serialCommand) {
        super(context, R.style.public_dialog_progress);
        this.mContext = context;
        setContentView(R.layout.dialog_up_command);
        setCanceledOnTouchOutside(true);

        this.writeMode = writeMode;
        this.serialCommand = serialCommand;
        initView();
    }

    public interface OnCenterItemClickListener {
        void OnCenterItemClick(SerialCommand serialCommand);
    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    private void initView() {
        AppCompatEditText mark = findViewById(R.id.serial_up_command_mark);
        AppCompatEditText comm = findViewById(R.id.serial_up_command_comm);
        MaterialButton commit = findViewById(R.id.serial_up_command_commit);

        if(null != serialCommand){
            mark.setText(serialCommand.getRemarks() == null ? "" : serialCommand.getRemarks());
            comm.setText(serialCommand.getCommand() == null ? "" : serialCommand.getCommand());
        }
        initSendDigits(comm, writeMode == 0);
        commit.setOnClickListener(v -> {
            String command = comm.getText().toString();
            String markStr = mark.getText().toString();
            if (ObjectUtils.isEmpty(command)) {
                Toast.makeText(mContext, "请输入命令！", Toast.LENGTH_SHORT).show();
                return;
            }
            if(null == serialCommand){
                serialCommand = new SerialCommand();
                serialCommand.setType(writeMode);
                serialCommand.setCommand(command);
                serialCommand.setRemarks(!ObjectUtils.isEmpty(markStr) ? markStr : "备注");
                long id = SerialCommandServiceImpl.getInstance().saveSerialCommand(serialCommand);
                Log.i("UpCommand", "insert id=" + id);
                serialCommand.setId(id);
            }else {
                serialCommand.setCommand(command);
                serialCommand.setRemarks(!ObjectUtils.isEmpty(markStr) ? markStr : "备注");
                SerialCommandServiceImpl.getInstance().updateSerialCommand(serialCommand);
            }
            if(listener != null) listener.OnCenterItemClick(serialCommand);
            dismiss();
        });
    }

    private void initSendDigits(AppCompatEditText editText, boolean isDigits) {
        if (isDigits) {
            KeyListener HexkeyListener = new NumberKeyListener()
            {
                public int getInputType()
                {
                    return InputType.TYPE_CLASS_TEXT;
                }
                @Override
                protected char[] getAcceptedChars()
                {
                    return new char[]{'0','1','2','3','4','5','6','7','8','9',
                            'a','b','c','d','e','f','A','B','C','D','E','F'};
                }
            };
            editText.setKeyListener(HexkeyListener);
        }
    }

}
