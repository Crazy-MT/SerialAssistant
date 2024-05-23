package com.sysout.app.serial.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.sysout.app.serial.R;
import com.sysout.app.serial.constants.Constants;
import com.sysout.app.serial.entity.SerialCommand;
import com.sysout.app.serial.ui.dialog.AdvancedSettingDialog;
import com.sysout.app.serial.utils.MathUtil;
import com.sysout.app.serial.utils.SerialHelper;
import com.sysout.app.serial.utils.packet.WeightPacket;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    public static final int RESULT_CODE = 10000;

    private AppCompatEditText serialTvRead, serialEtSend1, serialEtSend2, serialEtSend3, serialEtSend4, serialEtSend5, serialEtSend6, serialEtDelay;
    private RadioButton serialRbReadHex, serialRbWriteHex;
    private CheckBox serialCbSend1, serialCbSend2, serialCbSend3, serialCbSend4, serialCbSend5, serialCbSend6, serialCbLoopSend;
    private RadioGroup serialRgReadMode, serialRgWriteMode;
    private AppCompatSpinner serialSpPort, serialSpBaud;
    private ToggleButton serialTbSwitch;

    private long mPressedTime = 0L;

    private SerialHelper serialHelper;
    private String selectPort = "";
    private String selectBaud = "9600";
    private int selectParity = 0;
    private int selectDataBits = 8;
    private int selectStopBit = 1;

    private String[] portPaths;
    private int readMode = 0;           // 0 - hex ; 1 - txt;
    private int writeMode = 0;          // 0 - hex; 1 - txt;
    private int readDataLine = 0;
    private String[] loopDataList = new String[]{"", "", "", "", "", ""};
    private List<byte[]> startLoopList = new ArrayList<>();

    private OutputStreamWriter logWriter = null;
    private FileOutputStream outputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
//        selectPort = MMKV.defaultMMKV().decodeString("serial_port", selectPort);
//        selectBaud = MMKV.defaultMMKV().decodeString("serial_baud", selectBaud);
//        selectParity = MMKV.defaultMMKV().decodeInt("serial_parity", selectParity);
//        selectDataBits = MMKV.defaultMMKV().decodeInt("serial_data_bits", selectDataBits);
//        selectStopBit = MMKV.defaultMMKV().decodeInt("serial_stop_bit", selectStopBit);

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        portPaths = serialPortFinder.getAllDevicesPath();
        if (portPaths.length > 0) {
            if ("".equals(selectPort)) {
                selectPort = portPaths[0];
            }
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        serialTvRead = findViewById(R.id.serial_tv_read);
        serialRbReadHex = findViewById(R.id.serial_rb_read_hex);
        serialRgReadMode = findViewById(R.id.serial_rg_read_mode);
        serialRbWriteHex = findViewById(R.id.serial_rb_write_hex);
        serialRgWriteMode = findViewById(R.id.serial_rg_write_mode);
        serialSpPort = findViewById(R.id.serial_sp_port);
        serialSpBaud = findViewById(R.id.serial_sp_baud);
        serialTbSwitch = findViewById(R.id.serial_tb_switch);
        serialEtSend1 = findViewById(R.id.serial_et_send_1);
        serialCbSend1 = findViewById(R.id.serial_cb_send_1);
        serialEtSend2 = findViewById(R.id.serial_et_send_2);
        serialCbSend2 = findViewById(R.id.serial_cb_send_2);
        serialEtSend3 = findViewById(R.id.serial_et_send_3);
        serialCbSend3 = findViewById(R.id.serial_cb_send_3);
        serialEtSend4 = findViewById(R.id.serial_et_send_4);
        serialCbSend4 = findViewById(R.id.serial_cb_send_4);
        serialEtSend5 = findViewById(R.id.serial_et_send_5);
        serialCbSend5 = findViewById(R.id.serial_cb_send_5);
        serialEtSend6 = findViewById(R.id.serial_et_send_6);
        serialCbSend6 = findViewById(R.id.serial_cb_send_6);
        serialEtDelay = findViewById(R.id.serial_et_delay);
        serialCbLoopSend = findViewById(R.id.serial_cb_loop_send);

        initSerialSpinner();
        initRadioGroup();
        initSendCheckBox();
        initSendAction();
        initPortSpinner(portPaths);
    }

    public void initPortSpinner(String[] paths) {
        if (null == paths || paths.length == 0) {
            return;
        }
        List<String> pathList = new ArrayList<>(Arrays.asList(paths));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pathList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serialSpPort.setAdapter(adapter);
        serialSpPort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (null != portPaths && portPaths.length > 0) {
                    String port = portPaths[position];
                    Log.i(TAG, "port change==" + port);
                    setSerialTbSwitchNoChecked();
                    selectPort = port;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSerialSpinner() {
        serialSpBaud.setSelection(13, true);

        setSerialTbSwitchNoChecked();

        serialSpBaud.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] bauds = getResources().getStringArray(R.array.serial_baud_array);
                if (bauds.length > 0) {
                    String baud = bauds[position];
                    selectBaud = baud;
                    Log.i(TAG, "baud change==" + baud);
                    setSerialTbSwitchNoChecked();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        serialTbSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                openSerial();
            } else {
                serialHelper.close();
                serialHelper = null;
                Toast.makeText(this, "关闭串口成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openSerial() {
        if (ObjectUtils.isEmpty(selectPort)) {
            Toast.makeText(this, "请先选择串口号！", Toast.LENGTH_SHORT).show();
            setSerialTbSwitchNoChecked();
            return;
        }
        if (ObjectUtils.isEmpty(selectBaud)) {
            Toast.makeText(this, "请先选择波特率！", Toast.LENGTH_SHORT).show();
            setSerialTbSwitchNoChecked();
            return;
        }
        serialHelper = new SerialHelper(selectPort, Integer.parseInt(selectBaud), selectParity, selectDataBits, selectStopBit) {
            @Override
            protected void onDataReceived(byte[] buff) {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(TimeUtils.date2String(new Date(), "HH:mm:ss.SSS"));
                    sb.append("[").append(selectPort).append("]");
                    sb.append("[Read]");
                    sb.append("[").append(readMode == 0 ? "Hex" : "Txt").append("] ");
                    if (readMode == 0) {
                        sb.append(MathUtil.bytesToHex(buff));
                    } else {
                        sb.append(new String(buff));
                    }
                    sb.append("\r\n");
                    setSerialTvRead(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onDataReceived(WeightPacket packet) {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(TimeUtils.date2String(new Date(), "HH:mm:ss.SSS"));
                    sb.append("[").append(selectPort).append("]");
                    sb.append("[Read]");
                    sb.append("[").append("Txt").append("] ");
                    sb.append(packet.getNetWeight() + " " + packet.isStableWeight());
                    sb.append("\r\n");
                    setSerialTvRead(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onSendDataReceived(byte[] buff) {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(TimeUtils.date2String(new Date(), "HH:mm:ss.SSS"));
                    sb.append("[").append(selectPort).append("]");
                    sb.append("[Send]");
                    sb.append("[").append(writeMode == 0 ? "Hex" : "Txt").append("] ");
                    if (writeMode == 0) {
                        sb.append(MathUtil.bytesToHex(buff));
                    } else {
                        sb.append(new String(buff));
                    }
                    sb.append("\r\n");
                    setSerialTvRead(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            serialHelper.open();
            Toast.makeText(this, "打开串口成功！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.i(TAG, "打开串口失败：" + e.toString());
            Toast.makeText(this, "打开失败:" + e.toString(), Toast.LENGTH_SHORT).show();
            serialHelper = null;
            setSerialTbSwitchNoChecked();
        }
    }

    private void initRadioGroup() {
        serialRbReadHex.setChecked(true);
        serialRgReadMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.serial_rb_read_hex) {
                Log.i(TAG, "mode read hex");
                readMode = 0;
            } else if (checkedId == R.id.serial_rb_read_txt) {
                Log.i(TAG, "mode read txt");
                readMode = 1;
            }
        });
        serialRbWriteHex.setChecked(true);
        initSendDigits(true);
        serialRgWriteMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.serial_rb_write_hex) {
                Log.i(TAG, "mode write hex");
                writeMode = 0;
                initSendDigits(true);
            } else if (checkedId == R.id.serial_rb_write_txt) {
                writeMode = 1;
                Log.i(TAG, "mode write txt");
                initSendDigits(false);
            }
        });
    }

    private void initSendCheckBox() {
        serialCbSend1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loopDataList[0] = isChecked ? serialEtSend1.getText().toString() : "";
            if (!isChecked && !serialEtSend1.isEnabled()) serialEtSend1.setEnabled(true);
            if (isChecked && serialCbLoopSend.isChecked()) serialEtSend1.setEnabled(false);
        });
        serialCbSend2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loopDataList[1] = isChecked ? serialEtSend2.getText().toString() : "";
            if (!isChecked && !serialEtSend2.isEnabled()) serialEtSend2.setEnabled(true);
            if (isChecked && serialCbLoopSend.isChecked()) serialEtSend2.setEnabled(false);
        });
        serialCbSend3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loopDataList[2] = isChecked ? serialEtSend3.getText().toString() : "";
            if (!isChecked && !serialEtSend3.isEnabled()) serialEtSend3.setEnabled(true);
            if (isChecked && serialCbLoopSend.isChecked()) serialEtSend3.setEnabled(false);
        });
        serialCbSend4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loopDataList[3] = isChecked ? serialEtSend4.getText().toString() : "";
            if (!isChecked && !serialEtSend4.isEnabled()) serialEtSend4.setEnabled(true);
            if (isChecked && serialCbLoopSend.isChecked()) serialEtSend4.setEnabled(false);
        });
        serialCbSend5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loopDataList[4] = isChecked ? serialEtSend5.getText().toString() : "";
            if (!isChecked && !serialEtSend5.isEnabled()) serialEtSend5.setEnabled(true);
            if (isChecked && serialCbLoopSend.isChecked()) serialEtSend5.setEnabled(false);
        });
        serialCbSend6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loopDataList[5] = isChecked ? serialEtSend6.getText().toString() : "";
            if (!isChecked && !serialEtSend6.isEnabled()) serialEtSend6.setEnabled(true);
            if (isChecked && serialCbLoopSend.isChecked()) serialEtSend6.setEnabled(false);
        });
        serialCbLoopSend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!serialOpened()) {
                    Toast.makeText(this, "请先打开串口！", Toast.LENGTH_SHORT).show();
                    serialCbLoopSend.setChecked(false);
                    return;
                }

                String delay = serialEtDelay.getText().toString();
                if (ObjectUtils.isEmpty(delay)) {
                    delay = "0";
                }

                for (String data : loopDataList) {
                    if (!ObjectUtils.isEmpty(data)) {
                        if (writeMode == 0) {
                            startLoopList.add(MathUtil.hexString2Bytes(data));
                        } else {
                            startLoopList.add(data.getBytes());
                        }
                    }
                }
                if (startLoopList.isEmpty()) {
                    Toast.makeText(this, "请先勾选需要循环发送的指令！", Toast.LENGTH_SHORT).show();
                    serialCbLoopSend.setChecked(false);
                    return;
                }

                Toast.makeText(this, "开始自动循环发送" + startLoopList.size() + "条指令，间隔时间:" + delay + "ms", Toast.LENGTH_SHORT).show();

                serialHelper.setiDelay(Integer.parseInt(delay));
                serialHelper.setbLoopDataList(startLoopList);
                serialHelper.startSend();

                if (serialCbSend1.isChecked()) {
                    serialEtSend1.setEnabled(false);
                    serialCbSend1.setEnabled(false);
                }
                if (serialCbSend2.isChecked()) {
                    serialEtSend2.setEnabled(false);
                    serialCbSend2.setEnabled(false);
                }
                if (serialCbSend3.isChecked()) {
                    serialEtSend3.setEnabled(false);
                    serialCbSend3.setEnabled(false);
                }
                if (serialCbSend4.isChecked()) {
                    serialEtSend4.setEnabled(false);
                    serialCbSend4.setEnabled(false);
                }
                if (serialCbSend5.isChecked()) {
                    serialEtSend5.setEnabled(false);
                    serialCbSend5.setEnabled(false);
                }
                if (serialCbSend6.isChecked()) {
                    serialEtSend6.setEnabled(false);
                    serialCbSend6.setEnabled(false);
                }
                serialEtDelay.setEnabled(false);
            } else {
                serialHelper.stopSend();
                startLoopList.clear();
                serialHelper.clearLoopDataList();

                if (!serialEtSend1.isEnabled()) {
                    serialEtSend1.setEnabled(true);
                    serialCbSend1.setEnabled(true);
                }
                if (!serialEtSend2.isEnabled()) {
                    serialEtSend2.setEnabled(true);
                    serialCbSend2.setEnabled(true);
                }
                if (!serialEtSend3.isEnabled()) {
                    serialEtSend3.setEnabled(true);
                    serialCbSend3.setEnabled(true);
                }
                if (!serialEtSend4.isEnabled()) {
                    serialEtSend4.setEnabled(true);
                    serialCbSend4.setEnabled(true);
                }
                if (!serialEtSend5.isEnabled()) {
                    serialEtSend5.setEnabled(true);
                    serialCbSend5.setEnabled(true);
                }
                if (!serialEtSend6.isEnabled()) {
                    serialEtSend6.setEnabled(true);
                    serialCbSend6.setEnabled(true);
                }
                serialEtDelay.setEnabled(true);
            }
        });
    }

    private void initSendAction() {
        serialEtSend1.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                serialEtSend2.setFocusable(true);
                serialEtSend2.setFocusableInTouchMode(true);
                serialEtSend2.requestFocus();
            }
            return false;
        });
        serialEtSend2.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                serialEtSend3.setFocusable(true);
                serialEtSend3.setFocusableInTouchMode(true);
                serialEtSend3.requestFocus();
            }
            return false;
        });
        serialEtSend3.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                serialEtSend4.setFocusable(true);
                serialEtSend4.setFocusableInTouchMode(true);
                serialEtSend4.requestFocus();
            }
            return false;
        });
        serialEtSend4.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                serialEtSend5.setFocusable(true);
                serialEtSend5.setFocusableInTouchMode(true);
                serialEtSend5.requestFocus();
            }
            return false;
        });
        serialEtSend5.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                serialEtSend6.setFocusable(true);
                serialEtSend6.setFocusableInTouchMode(true);
                serialEtSend6.requestFocus();
            }
            return false;
        });
        serialEtSend6.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
            return false;
        });
    }

    private boolean serialOpened() {
        if (serialHelper == null) return false;
        return serialHelper.isOpen();
    }

    private void initSendDigits(boolean isDigits) {
        if (isDigits) {
            KeyListener HexkeyListener = new NumberKeyListener() {
                public int getInputType() {
                    return InputType.TYPE_CLASS_TEXT;
                }

                @Override
                protected char[] getAcceptedChars() {
                    return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                            'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'};
                }
            };
            serialEtSend1.setKeyListener(HexkeyListener);
            serialEtSend2.setKeyListener(HexkeyListener);
            serialEtSend3.setKeyListener(HexkeyListener);
            serialEtSend4.setKeyListener(HexkeyListener);
            serialEtSend5.setKeyListener(HexkeyListener);
            serialEtSend6.setKeyListener(HexkeyListener);
            return;
        }
        KeyListener txTkeyListener = new TextKeyListener(TextKeyListener.Capitalize.NONE, false);
        serialEtSend1.setKeyListener(txTkeyListener);
        serialEtSend2.setKeyListener(txTkeyListener);
        serialEtSend3.setKeyListener(txTkeyListener);
        serialEtSend4.setKeyListener(txTkeyListener);
        serialEtSend5.setKeyListener(txTkeyListener);
        serialEtSend6.setKeyListener(txTkeyListener);
    }

    public void setSerialTvRead(String data) {
        runOnUiThread(() -> {
            serialTvRead.append(data);
            if (readDataLine >= 500) {
                serialTvRead.setText("");
                readDataLine = 0;
            } else {
                readDataLine++;
            }
        });
    }

    public void setSerialTbSwitchNoChecked() {
        if (serialTbSwitch.isChecked()) {
            serialTbSwitch.toggle();
        }
    }

    public void advancedSettingOnClick(View view) {
        if (serialOpened()) {
            return;
        }
        AdvancedSettingDialog dialog = new AdvancedSettingDialog(this, selectParity, selectDataBits, selectStopBit);
        dialog.setOnCenterItemClickListener((parity, dataBits, stopBit) -> {
            Log.i(TAG, "parity:" + parity + ", dataBits:" + dataBits + ", stopBit:" + stopBit);
            selectParity = parity;
            selectDataBits = dataBits;
            selectStopBit = stopBit;
        });
        dialog.show();
    }

    public void readClearOnClick(View view) {
        serialTvRead.setText("");
    }

    public void sendClearOnClick(View view) {
        serialEtSend1.setText("");
        serialEtSend2.setText("");
        serialEtSend3.setText("");
        serialEtSend4.setText("");
        serialEtSend5.setText("");
        serialEtSend6.setText("");
    }

    public void send1OnClick(View view) {
        if (!serialOpened()) {
            Toast.makeText(MainActivity.this, "请先打开串口！", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = serialEtSend1.getText().toString();
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        if (writeMode == 0) {
            serialHelper.send(MathUtil.hexString2Bytes(data));
        } else {
            serialHelper.send(data.getBytes());
        }
    }

    public void send2OnClick(View view) {
        if (!serialOpened()) {
            Toast.makeText(MainActivity.this, "请先打开串口！", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = serialEtSend2.getText().toString();
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        if (writeMode == 0) {
            serialHelper.send(MathUtil.hexString2Bytes(data));
        } else {
            serialHelper.send(data.getBytes());
        }
    }

    public void send3OnClick(View view) {
        if (!serialOpened()) {
            Toast.makeText(MainActivity.this, "请先打开串口！", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = serialEtSend3.getText().toString();
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        if (writeMode == 0) {
            serialHelper.send(MathUtil.hexString2Bytes(data));
        } else {
            serialHelper.send(data.getBytes());
        }
    }

    public void send4OnClick(View view) {
        if (!serialOpened()) {
            Toast.makeText(MainActivity.this, "请先打开串口！", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = serialEtSend4.getText().toString();
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        if (writeMode == 0) {
            serialHelper.send(MathUtil.hexString2Bytes(data));
        } else {
            serialHelper.send(data.getBytes());
        }
    }

    public void send5OnClick(View view) {
        if (!serialOpened()) {
            Toast.makeText(MainActivity.this, "请先打开串口！", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = serialEtSend5.getText().toString();
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        if (writeMode == 0) {
            serialHelper.send(MathUtil.hexString2Bytes(data));
        } else {
            serialHelper.send(data.getBytes());
        }
    }

    public void send6OnClick(View view) {
        if (!serialOpened()) {
            Toast.makeText(MainActivity.this, "请先打开串口！", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = serialEtSend6.getText().toString();
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        if (writeMode == 0) {
            serialHelper.send(MathUtil.hexString2Bytes(data));
        } else {
            serialHelper.send(data.getBytes());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }
}