package com.sysout.app.serial.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.sysout.app.serial.R;
import com.sysout.app.serial.utils.Order;
import com.sysout.app.serial.utils.SerialDataUtils;
import com.sysout.app.serial.utils.SerialHelper;
import com.sysout.app.serial.utils.SerialPortUtil;
import com.sysout.app.serial.utils.packet.WeightPacket;

import java.io.IOException;
import java.text.DecimalFormat;

public class ControlParamsActivity extends AppCompatActivity {
    private static final String TAG = "ControlParamsActivity";
    private SerialHelper serialHelper;
    private Button mBtBack;
    private Button mBtShake;
    private Button mBtLeft;
    private Button mBtRight;
    private Button mBtTime;
    private SharedPreferences mPreferences;

    private static final String KEY_SHAKE_LEFT = "shake.left";
    private static final String KEY_SHAKE_RIGHT = "shake.right";
    private static final String KEY_SHAKE_TIME = "shake.time";
    private static final String KEY_SHAKE_COUNT = "shake.count";
    private static final String KEY_SHAKE_OFFSET = "shake.offset";
    private int mShakeZero = ConstantUtil.DEFAULT_INT;
    private int mShakeZeroOffset = 0;

    private int left;
    private int right;
    private int time;
    private int count;

    private static final String KEY_NOD_LEFT = "nod.left";
    private static final String KEY_NOD_RIGHT = "nod.right";
    private static final String KEY_NOD_TIME = "nod.time";
    private static final String KEY_NOD_COUNT = "nod.count";
    private static final String KEY_NOD_OFFSET = "nod.offset";
    private int mNodZero = ConstantUtil.DEFAULT_INT;
    private int mNodZeroOffset = -100;
    private int leftNod;
    private int rightNod;
    private int timeNod;
    private int countNod;
    private Button mBtRepeatCount;

    private Handler mHandler;
    private Handler mHandlerNod;
    private Runnable actionRunnable;
    private Runnable actionNodRunnable;
    private Button mBtShakeZero;
    private TextView mTvShakeHint;
    private Button mBtNod;
    private Button mBtNodZero;
    private Button mBtNodLeft;
    private Button mBtNodRight;
    private Button mBtNodTime;
    private Button mBtNodRepeatCount;
    private TextView mTvNodHint;
    private Toolbar mToolbar;
    private Button mBtShakeOffset;
    private Button mBtNodOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
        mHandlerNod = new Handler(Looper.getMainLooper());
        setContentView(R.layout.activity_control_params);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("点头&摇头");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPreferences = getSharedPreferences("ControlParams", MODE_PRIVATE);
        left = mPreferences.getInt(KEY_SHAKE_LEFT, 360);
        right = mPreferences.getInt(KEY_SHAKE_RIGHT, 360);
        time = mPreferences.getInt(KEY_SHAKE_TIME, 2000);
        count = mPreferences.getInt(KEY_SHAKE_COUNT, 1);
        mShakeZeroOffset = mPreferences.getInt(KEY_SHAKE_OFFSET, 0);

        leftNod = mPreferences.getInt(KEY_NOD_LEFT, 140);
        rightNod = mPreferences.getInt(KEY_NOD_RIGHT, 260);
        timeNod = mPreferences.getInt(KEY_NOD_TIME, 2000);
        countNod = mPreferences.getInt(KEY_NOD_COUNT, 1);
        mNodZeroOffset = mPreferences.getInt(KEY_NOD_OFFSET, -100);

        initView();
        try {
            Log.d(TAG, "open =====================1111111111111111111=");
            int openStatus = open(SerialPortUtil.PORT_NAME, SerialPortUtil.IBAUDTATE);
            Log.d(TAG, "open =====================openStatus = " + openStatus);
        } catch (Exception e) {
            Log.d(TAG, "open =====================444444444444444444=" + e.getMessage());
        }
        mBtBack.setOnClickListener(v -> {
            finish();
        });
        initShake();
        initNod();
    }

    private void initShake() {

        mBtShake.setOnClickListener(v -> {

            mShakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
            if (ConstantUtil.isDefault(mShakeZero)) {
                toast("请先获取摇头零位");
                return;
            }
            mShakeZero = mShakeZero + mShakeZeroOffset;
            runCount = count;
            mTvShakeHint.setText("");
            isRight = true;
            send();
        });

        mBtLeft.setOnClickListener(v -> {
            left = (left + 10) % 600;
            if (left <= 10) {
                left = 10;
            }
            refreshShake();
        });

        mBtRight.setOnClickListener(v -> {
            right = (right + 10) % 600;
            if (right <= 10) {
                right = 10;
            }
            refreshShake();
        });

        mBtTime.setOnClickListener(v -> {
            time = (time + 100) % 5000;
            if (time <= 100) {
                count = 100;
            }
            refreshShake();
        });

        mBtRepeatCount.setOnClickListener(v -> {
            count = (count + 1) % 5;
            if (count <= 0) {
                count = 1;
            }
            refreshShake();
        });

        mBtShakeZero.setOnClickListener(v -> {
            mShakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
            if (ConstantUtil.isDefault(mShakeZero)) {
                toast("请先获取摇头零位");
                return;
            }

            mShakeZero = mShakeZero + mShakeZeroOffset;
            if (serialOpened()) {
                serialHelper.sendHex(SerialPortUtil.getSendData(Order.DOWN_SERVO_STATE, 2, mShakeZero, 5000));
            }

        });

        mBtShakeOffset.setOnClickListener(v -> {
            mShakeZeroOffset = (mShakeZeroOffset - 10);
            if (mShakeZeroOffset < -200) {
                mShakeZeroOffset = 200;
            } else if (mShakeZeroOffset > 200) {
                mShakeZeroOffset = -200;
            }
            refreshShake();
        });


        refreshShake();
    }

    private void initNod() {

        mBtNod.setOnClickListener(v -> {
            mNodZero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
            if (ConstantUtil.isDefault(mNodZero)) {
                toast("请先获取点头零位");
                return;
            }
            mNodZero = mNodZero + mNodZeroOffset;
            runCountNod = countNod;
            mTvNodHint.setText("");
            isBottom = true;
            sendNod();
        });

        mBtNodLeft.setOnClickListener(v -> {
            leftNod = (leftNod + 10) % 500;
            if (leftNod <= 10) {
                leftNod = 10;
            }
            refreshNod();
        });

        mBtNodRight.setOnClickListener(v -> {
            rightNod = (rightNod + 10) % 500;
            if (rightNod <= 10) {
                rightNod = 10;
            }
            refreshNod();
        });

        mBtNodTime.setOnClickListener(v -> {
            timeNod = (timeNod + 100) % 5000;
            if (timeNod <= 100) {
                timeNod = 100;
            }
            refreshNod();
        });

        mBtNodRepeatCount.setOnClickListener(v -> {
            countNod = (countNod + 1) % 5;
            if (countNod <= 0) {
                countNod = 1;
            }
            refreshNod();
        });

        mBtNodZero.setOnClickListener(v -> {
            mNodZero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
            if (ConstantUtil.isDefault(mNodZero)) {
                toast("请先获取点头零位");
                return;
            }
            mNodZero = mNodZero + mNodZeroOffset;
            if (serialOpened()) {
                serialHelper.sendHex(SerialPortUtil.getSendData(Order.DOWN_SERVO_STATE, 1, mNodZero, 5000));
            }

        });

        mBtNodOffset.setOnClickListener(v -> {
            mNodZeroOffset = (mNodZeroOffset - 10);
            if (mNodZeroOffset < -200) {
                mNodZeroOffset = 200;
            } else if (mNodZeroOffset > 200) {
                mNodZeroOffset = -200;
            }
            refreshNod();
        });


        refreshNod();
    }

    private int type = 0;
    private int runCount = 0;
    private int runCountNod = 0;
    private boolean isRight = true;
    private boolean isBottom = true;

    public void send() {
        int order = Order.DOWN_SERVO_STATE;
        int[] params;
        if (!serialOpened()) {
            toast("串口未开启");
        }
        if (actionRunnable != null) {
            mHandler.removeCallbacks(actionRunnable);
        }
        if (runCount == count) {
            // 右
            params = new int[]{2, mShakeZero + right, time / 2};
        } else if (runCount == -1) {
            // 归零
            isRight = !isRight;
            params = new int[]{2, mShakeZero, time / 2};
        } else {
            // 左or右
            isRight = !isRight;
            params = new int[]{2, isRight ? mShakeZero + right : mShakeZero - left, time};
        }
        mTvShakeHint.setText(mTvShakeHint.getText().toString()
                + "\n" + (isRight ? "【右】" : "【左】")
                + " -> 角度：" + getPeople(params[1] / 10f)
                + " -> 时间：" + params[2] + "ms");
        if (serialOpened()) {
            serialHelper.sendHex(SerialPortUtil.getSendData(order, params));
        }
        if (runCount > -1) {
            actionRunnable = new Runnable() {
                @Override
                public void run() {
                    runCount--;
                    send();
                }
            };
            mHandler.postDelayed(actionRunnable, params[2] + 100);
        }
    }

    public void sendNod() {
        int order = Order.DOWN_SERVO_STATE;
        int[] params;
        if (!serialOpened()) {
            toast("串口未开启");
        }
        if (actionNodRunnable != null) {
            mHandlerNod.removeCallbacks(actionNodRunnable);
        }
        if (runCountNod == countNod) {
            // 上
            params = new int[]{1, mNodZero + rightNod, timeNod / 2};
        } else if (runCountNod == -1) {
            // 归零
            isBottom = !isBottom;
            params = new int[]{1, mNodZero, timeNod / 2};
        } else {
            // 下or上
            isBottom = !isBottom;
            params = new int[]{1, isBottom ? mNodZero + rightNod : mNodZero - leftNod, timeNod};
        }
        mTvNodHint.setText(mTvNodHint.getText().toString()
                + "\n" + (isBottom ? "【上】" : "【下】")
                + " -> 角度：" + getPeople(params[1] / 10f)
                + " -> 时间：" + params[2] + "ms");
        if (serialOpened()) {
            serialHelper.sendHex(SerialPortUtil.getSendData(order, params));
        }
        if (runCountNod > -1) {
            actionNodRunnable = new Runnable() {
                @Override
                public void run() {
                    runCountNod--;
                    sendNod();
                }
            };
            mHandlerNod.postDelayed(actionNodRunnable, params[2] + 100);
        }
    }

    private void refreshShake() {

        mBtTime.setText(time + "ms");
        mBtLeft.setText("左:" + getPeople(left / 10f));
        mBtRight.setText("右:" + getPeople(right / 10f));
        mBtRepeatCount.setText("次数:" + count);
        if (ConstantUtil.isNotDefault(mShakeZero)) {
            mBtShakeZero.setText("归零[" + getPeople(mShakeZero / 10f) + "]");
        }
        mBtShakeOffset.setText("偏移[" + getPeople(mShakeZeroOffset / 10f) + "]");
        mPreferences.edit().putInt(KEY_SHAKE_LEFT, left).putInt(KEY_SHAKE_RIGHT, right)
                .putInt(KEY_SHAKE_TIME, time)
                .putInt(KEY_SHAKE_OFFSET, mShakeZeroOffset)
                .putInt(KEY_SHAKE_COUNT, count).commit();
    }


    private void refreshNod() {

        mBtNodTime.setText(timeNod + "ms");
        mBtNodLeft.setText("上:" + getPeople(leftNod / 10f));
        mBtNodRight.setText("下:" + getPeople(rightNod / 10f));
        mBtNodRepeatCount.setText("次数:" + countNod);
        if (ConstantUtil.isNotDefault(mNodZero)) {
            mBtNodZero.setText("归零[" + getPeople(mNodZero / 10f) + "]");
        }
        mBtNodOffset.setText("偏移[" + getPeople(mNodZeroOffset / 10f) + "]");
        mPreferences.edit().putInt(KEY_NOD_LEFT, leftNod).putInt(KEY_NOD_RIGHT, rightNod)
                .putInt(KEY_NOD_TIME, timeNod)
                .putInt(KEY_NOD_OFFSET, mNodZeroOffset)
                .putInt(KEY_NOD_COUNT, countNod).commit();
    }

    /**
     * 点头舵机：（中心角度 = 零位校准角度 - 10）
     * 上位置，中心角度 - 14；下位置，中心角度 + 26
     * 速度：2000ms
     * 摇头舵机：（中心角度 = 零位校准角度 - 0）
     * 左位置，中心角度 - 36；下位置，中心角度 + 36
     * 速度：2000ms
     *
     * @param selectPort
     * @param selectBaud
     * @return
     */
    private int open(String selectPort, int selectBaud) {
        if (serialOpened()) {
            serialHelper.close();
        }
        serialHelper = new SerialHelper(selectPort, selectBaud) {
            @Override
            protected void onDataReceived(byte[] buff) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String hexData = SerialDataUtils.ByteArrToHex(buff);
                        Log.d(TAG, "接收到数据: hexData = " + hexData);
                        SerialPortUtil.setParseDataListener(new SerialPortUtil.ParseDataListener() {


                            @Override
                            public void onHandleOrder(int order, int[] bytes) {

                                switch (order) {
                                    case Order.UP_STATE:
                                        break;
                                }
                            }
                        });
                        SerialPortUtil.parseOrder(hexData);
                    }
                });

            }

            @Override
            protected void onDataReceived(WeightPacket packet) {

            }

            @Override
            protected void onSendDataReceived(byte[] buff) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String hexData = SerialDataUtils.ByteArrToHex(buff);
                        Log.d(TAG, "onSendDataReceived = " + hexData);
                    }
                });

            }
        };
        Log.d(TAG, "open =================AAAAAAAAAAAAA");
        try {
            serialHelper.open();
            Log.d(TAG, "open =================BBBBBBBBBBBBBBBBBBB");
            return 1;
        } catch (IOException e) {
            Log.d(TAG, "open =================CCCCCCCCCCCCCCCC" + e.getMessage());
            return -1;
        }

    }


    public static String getPeople(float people) {
        if (people == 0f) {
            return "0.0";
        }
        return new DecimalFormat("##0.0").format(people);
    }


    private boolean serialOpened() {
        if (serialHelper == null) return false;
        return serialHelper.isOpen();
    }

    private void initView() {
        mBtBack = (Button) findViewById(R.id.bt_back);
        mBtShake = (Button) findViewById(R.id.bt_shake);
        mBtLeft = (Button) findViewById(R.id.bt_left);
        mBtRight = (Button) findViewById(R.id.bt_right);
        mBtTime = (Button) findViewById(R.id.bt_time);
        mBtRepeatCount = (Button) findViewById(R.id.bt_repeat_count);
        mBtShakeZero = (Button) findViewById(R.id.bt_shake_zero);
        mTvShakeHint = (TextView) findViewById(R.id.tv_shake_hint);
        mBtNod = (Button) findViewById(R.id.bt_nod);
        mBtNodZero = (Button) findViewById(R.id.bt_nod_zero);
        mBtNodLeft = (Button) findViewById(R.id.bt_nod_left);
        mBtNodRight = (Button) findViewById(R.id.bt_nod_right);
        mBtNodTime = (Button) findViewById(R.id.bt_nod_time);
        mBtNodRepeatCount = (Button) findViewById(R.id.bt_nod_repeat_count);
        mTvNodHint = (TextView) findViewById(R.id.tv_nod_hint);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBtShakeOffset = (Button) findViewById(R.id.bt_shake_offset);
        mBtNodOffset = (Button) findViewById(R.id.bt_nod_offset);
    }

    private void toast(String text) {
        Toast.makeText(ControlParamsActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mHandlerNod != null) {
            mHandlerNod.removeCallbacksAndMessages(null);
            mHandlerNod = null;
        }
    }
}