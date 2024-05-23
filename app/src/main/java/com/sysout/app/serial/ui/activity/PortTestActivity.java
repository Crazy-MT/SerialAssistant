package com.sysout.app.serial.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.sysout.app.serial.R;
import com.sysout.app.serial.utils.Order;
import com.sysout.app.serial.utils.SerialDataUtils;
import com.sysout.app.serial.utils.SerialHelper;
import com.sysout.app.serial.utils.SerialPortUtil;
import com.sysout.app.serial.utils.packet.WeightPacket;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PortTestActivity extends AppCompatActivity {

    private static final String TAG = "PortTestActivity";
    private Button mBtBack;
    private EditText mEtName;
    private EditText mEtCode;
    private Button mBtOpen;
    private Button mBtColse;
    private TextView mTvOpenStatusHint;
    private EditText mEtCode01;
    private EditText mEtCode02;
    private EditText mEtCode03;
    private EditText mEtCode04;
    private EditText mEtCode05;
    private EditText mEtCode06;
    private Button mBtSend;
    private TextView mTvSendStatusHint;
    private TextView mTvRiceverStatusHint;
    private EditText mEtOrder;
    private SerialHelper serialHelper;
    private PopupWindow mPopWindow;
    private Button mBtSelOrder;
    private LinearLayout mLlCode;
    private LinearLayout mLlSel;
    private Button mBtSel01;
    private Button mBtSel02;
    private Button mBtSel03;
    private Button mBtSel04;
    private Button mBtSel05;
    private Button mBtSel06;
    private EditText[] editTexts;
    private Button[] btSels;
    private TextView mTvOderInfo;
    private TextView mTvCodesInfo;
    private EditText mEtCode07;
    private EditText mEtCode08;
    private EditText mEtCode09;
    private EditText mEtCode10;
    private EditText mEtCode11;
    private EditText mEtCode12;
    private Button mBtSel07;
    private Button mBtSel08;
    private Button mBtSel09;
    private Button mBtSel10;
    private Button mBtSel11;
    private Button mBtSel12;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_test);
        initView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("协议指令");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void initView() {
        mBtBack = (Button) findViewById(R.id.bt_back);
        mBtBack.setOnClickListener(v -> {
            finish();
        });
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtOrder = (EditText) findViewById(R.id.et_order);
        mEtCode = (EditText) findViewById(R.id.et_code);
        mBtOpen = (Button) findViewById(R.id.bt_open);
        mBtColse = (Button) findViewById(R.id.bt_colse);
        mTvOpenStatusHint = (TextView) findViewById(R.id.tv_open_status_hint);
        mEtCode01 = (EditText) findViewById(R.id.et_code01);
        mEtCode02 = (EditText) findViewById(R.id.et_code02);
        mEtCode03 = (EditText) findViewById(R.id.et_code03);
        mEtCode04 = (EditText) findViewById(R.id.et_code04);
        mEtCode05 = (EditText) findViewById(R.id.et_code05);
        mEtCode06 = (EditText) findViewById(R.id.et_code06);
        mBtSend = (Button) findViewById(R.id.bt_send);

        mTvOderInfo = findViewById(R.id.tv_order_info);
        mBtSelOrder = (Button) findViewById(R.id.bt_sel_order);
        mLlCode = (LinearLayout) findViewById(R.id.ll_code);
        mLlSel = (LinearLayout) findViewById(R.id.ll_sel);
        mBtSel01 = (Button) findViewById(R.id.bt_sel01);
        mBtSel02 = (Button) findViewById(R.id.bt_sel02);
        mBtSel03 = (Button) findViewById(R.id.bt_sel03);
        mBtSel04 = (Button) findViewById(R.id.bt_sel04);
        mBtSel05 = (Button) findViewById(R.id.bt_sel05);
        mBtSel06 = (Button) findViewById(R.id.bt_sel06);
        mEtCode07 = (EditText) findViewById(R.id.et_code07);
        mEtCode08 = (EditText) findViewById(R.id.et_code08);
        mEtCode09 = (EditText) findViewById(R.id.et_code09);
        mEtCode10 = (EditText) findViewById(R.id.et_code10);
        mEtCode11 = (EditText) findViewById(R.id.et_code11);
        mEtCode12 = (EditText) findViewById(R.id.et_code12);
        mBtSel07 = (Button) findViewById(R.id.bt_sel07);
        mBtSel08 = (Button) findViewById(R.id.bt_sel08);
        mBtSel09 = (Button) findViewById(R.id.bt_sel09);
        mBtSel10 = (Button) findViewById(R.id.bt_sel10);
        mBtSel11 = (Button) findViewById(R.id.bt_sel11);
        mBtSel12 = (Button) findViewById(R.id.bt_sel12);

        mTvCodesInfo = (TextView) findViewById(R.id.tv_codes_info);

        editTexts = new EditText[]{mEtCode01, mEtCode02, mEtCode03, mEtCode04, mEtCode05,
                mEtCode06, mEtCode07, mEtCode08, mEtCode09, mEtCode10, mEtCode11, mEtCode12};
        btSels = new Button[]{mBtSel01, mBtSel02, mBtSel03, mBtSel04, mBtSel05,
                mBtSel06, mBtSel07, mBtSel08, mBtSel09, mBtSel10, mBtSel11, mBtSel12};


        mTvSendStatusHint = (TextView) findViewById(R.id.tv_send_status_hint);
        mTvRiceverStatusHint = (TextView) findViewById(R.id.tv_ricever_status_hint);

        mEtName.setText(SerialPortUtil.PORT_NAME);
        mEtCode.setText(String.valueOf(SerialPortUtil.IBAUDTATE));
        mEtOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String order = mEtOrder.getText().toString().trim().replaceAll(" ", "");
                if (TextUtils.isEmpty(order)) {
                    mLlSel.setVisibility(View.GONE);
                    mLlCode.setVisibility(View.GONE);
                    mTvOderInfo.setText("");
                } else {
                    for (EditText editText : editTexts) {
                        editText.setText("");
                    }
                    mLlSel.setVisibility(View.VISIBLE);
                    mLlCode.setVisibility(View.VISIBLE);
                    try {
                        int orderInt = Integer.parseInt(order);
                        Order mOrder = Order.getOrderMap().get(orderInt);
                        LogUtils.d("mOrder != null " + mOrder != null);
                        if (mOrder != null && mOrder.paramsLens != null) {
                            for (Button button : btSels) {
                                button.setVisibility(View.GONE);
                                button.setOnClickListener(null);
                            }
                            for (EditText editText : editTexts) {
                                editText.setVisibility(View.GONE);
                            }
                            mTvOderInfo.setText("指令说明：" + mOrder.cmdTitle);
                            for (int i = 0; i < mOrder.paramsLens.length; i++) {
                                Button btSel = btSels[i];
                                btSel.setVisibility(View.VISIBLE);

                                EditText editText = editTexts[i];
                                editText.setVisibility(View.VISIBLE);
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        notifyCodesInfo();
                                    }
                                });
                                editText.setHint(mOrder.options[i].info);

                                List<Order.KeyValue> values = mOrder.options[i].getValues();

                                btSel.setOnClickListener(v -> {
                                    showSelect(btSel, values, new OnItemListener() {
                                        @Override
                                        public void onOption(int position, Order.KeyValue keyValue) {
                                            editText.setText(String.valueOf(keyValue.key));
                                        }
                                    });
                                });
                                notifyCodesInfo();
                            }
                        } else {
                            mLlSel.setVisibility(View.GONE);
                            for (Button button : btSels) {
                                button.setVisibility(View.GONE);
                                button.setOnClickListener(null);
                            }
                            for (EditText editText : editTexts) {
                                editText.setVisibility(View.VISIBLE);
                                editText.setHint("请输入");
                            }
                            mTvCodesInfo.setText("参数说明无");
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        mBtSelOrder.setOnClickListener(v -> {
            showSelect(mBtSelOrder, Order.getValues(), new OnItemListener() {
                @Override
                public void onOption(int position, Order.KeyValue keyValue) {
                    mEtOrder.setText(String.valueOf(keyValue.key));
                }
            });
        });
        mBtOpen.setOnClickListener(v -> {
            Log.d(TAG, "open ======================");
            String name = mEtName.getText().toString();
            String code = mEtCode.getText().toString();
            if (TextUtils.isEmpty(name)) {
                show("【串口名称】 不能是空的");
                return;
            }

            if (TextUtils.isEmpty(code)) {
                show("【串口号】 不能是空的");
                return;
            }
            try {
                Log.d(TAG, "open =====================1111111111111111111=");
                int ibaudRate = Integer.parseInt(code);
                int openStatus = open(name, ibaudRate);
                mTvOpenStatusHint.setText("【串口名称】:" + name + "    【串口号】:" + code + "   【打开状态】:" + openStatus);
                Log.d(TAG, "open =====================222222222222222222222222=");
            } catch (Exception e) {
                mTvOpenStatusHint.setText("打开失败：" + e.getMessage());
                Log.d(TAG, "open =====================444444444444444444=" + e.getMessage());
            }

            Log.d(TAG, "open =====================3333333333333333333333=");

        });

        mBtColse.setOnClickListener(v -> {
            if (serialHelper != null) {
                serialHelper.close();
            }
            mTvOpenStatusHint.setText("已关闭");
        });


        mBtSend.setOnClickListener(v -> {

            String order = mEtOrder.getText().toString().trim();
            int orderInt = 0;
            try {
                orderInt = Integer.parseInt(order);
            } catch (Exception e) {
                mTvSendStatusHint.setText("指令参数异常：" + e.getMessage());
                return;
            }

            int len = editTexts.length;
            List<Integer> bytes = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                String content = editTexts[i].getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    try {
                        int mB = Integer.parseInt(content);
                        bytes.add(mB);
                    } catch (Exception e) {
                        mTvSendStatusHint.setText("第" + (i + 1) + "个参数异常：" + e.getMessage());
                        return;
                    }
                }
            }

            if (bytes.isEmpty()) {
                mTvSendStatusHint.setText("参数异常：无");
                return;
            }
            if (Order.getOrderMap().get(orderInt) != null &&
                    bytes.size() != Order.getOrderMap().get(orderInt).paramsLens.length) {
                for (int bin = bytes.size(); bin <= Order.getOrderMap().get(orderInt).paramsLens.length; bin++) {
                    bytes.add(0);
                }
            }
            int[] params = new int[bytes.size()];

            int pLen = params.length;
            for (int i = 0; i < pLen; i++) {
                params[i] = bytes.get(i);
            }
            String senData = SerialPortUtil.getSendData(orderInt, params);
            if (serialHelper != null && serialHelper.isOpen()) {
                serialHelper.sendHex(senData);
                Log.d(TAG, "指令：" + orderInt + "   原始参数：" + Arrays.toString(params));
                mTvSendStatusHint.setText("指令：" + SerialDataUtils.Int2Hex(orderInt) + "   原始参数：" + Arrays.toString(params)
                        + "  final发送数据：" + senData);
            } else {
                Log.d(TAG, "指令：" + orderInt + "   【发送数据失败】原始参数：" + Arrays.toString(params));
                mTvSendStatusHint.setText("指令：" + SerialDataUtils.Int2Hex(orderInt) + "   原始参数：" + Arrays.toString(params)
                        + "  final发送数据失败：" + senData);
                mTvRiceverStatusHint.setText("【假】接收到数据：" + senData);
                SerialPortUtil.setParseDataListener(new SerialPortUtil.ParseDataListener() {
                    @Override
                    public void onHandleOrder(int order, int[] bytes) {
                        mTvRiceverStatusHint.setText("【假】接收到数据：" + senData + "  \n  "
                                + "指令：" + SerialDataUtils.Int2Hex(order) + "   参数：" + Arrays.toString(bytes));
                    }
                });
                SerialPortUtil.parseOrder(senData);
            }


        });
    }

    private void notifyCodesInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("参数说明：");
        for (int e = 0; e < editTexts.length; e++) {
            if (editTexts[e].getVisibility() == View.VISIBLE) {
                sb.append(" ( ");
                sb.append(editTexts[e].getHint());
                sb.append("：");
                sb.append(editTexts[e].getText().toString().trim());
                sb.append(" )   ");
            } else {
                break;
            }
        }
        mTvCodesInfo.setText(sb.toString());
    }

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
                        mTvRiceverStatusHint.setText("接收到数据：" + hexData);
                        SerialPortUtil.setParseDataListener(new SerialPortUtil.ParseDataListener() {
                            @Override
                            public void onHandleOrder(int order, int[] bytes) {
                                mTvRiceverStatusHint.setText("接收到数据：" + hexData + "  \n  "
                                        + "指令：" + SerialDataUtils.Int2Hex(order) + "   参数：" + Arrays.toString(bytes));
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
                        Log.d(TAG, "发送的数据: hexData = " + hexData);
                        mTvSendStatusHint.setText("发送的数据：" + hexData);
                        SerialPortUtil.setParseDataListener(new SerialPortUtil.ParseDataListener() {
                            @Override
                            public void onHandleOrder(int order, int[] bytes) {
                                mTvSendStatusHint.setText("发送的数据：" + hexData + "  \n  "
                                        + "指令：" + SerialDataUtils.Int2Hex(order) + "   参数：" + Arrays.toString(bytes));
                            }
                        });
                        SerialPortUtil.parseOrder(hexData);
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


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }


    private void show(String text) {
        Toast.makeText(PortTestActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private boolean serialOpened() {
        if (serialHelper == null) return false;
        return serialHelper.isOpen();
    }

    /**
     * 选择出场动画频率
     */
    private void showSelect(View drawAt, List<Order.KeyValue> list, OnItemListener listener) {
        if (mPopWindow != null) {
            mPopWindow.dismiss();
            mPopWindow = null;
        }
        View contentView = LayoutInflater.from(PortTestActivity.this).inflate(R.layout.popwindow_list, null);
        RecyclerView recyclerView = contentView.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(PortTestActivity.this));
        OptionAdapter optionAdapter = new OptionAdapter(list);
        optionAdapter.setOnItemListener(listener);
        recyclerView.setAdapter(optionAdapter);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 点击外部取消显示
        mPopWindow.setOutsideTouchable(true);
        //  mPopWindow.setBackgroundDrawable(new BitmapDrawable()); // 括号内过时
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        mPopWindow.showAsDropDown(drawAt, 0, 0, Gravity.LEFT);
    }

    public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionHolder> {
        private List<Order.KeyValue> dataList;

        public OptionAdapter(List<Order.KeyValue> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @NotNull
        @Override
        public OptionHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new OptionHolder(LayoutInflater.from(PortTestActivity.this)
                    .inflate(R.layout.option_list_imp, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull OptionHolder holder, int position) {
            holder.mTvOption.setText(dataList.get(position).value);
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        OnItemListener onItemListener;

        public void setOnItemListener(OnItemListener onItemListener) {
            this.onItemListener = onItemListener;
        }

        public class OptionHolder extends RecyclerView.ViewHolder {

            public TextView mTvOption;

            public OptionHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                mTvOption = itemView.findViewById(R.id.tv_option);
                itemView.setOnClickListener(v -> {
                    if (mPopWindow != null) {
                        mPopWindow.dismiss();
                        mPopWindow = null;
                    }
                    if (onItemListener != null) {
                        onItemListener.onOption(getAdapterPosition(), dataList.get(getAdapterPosition()));
                    }
                });
            }
        }
    }


    public interface OnItemListener {
        void onOption(int position, Order.KeyValue keyValue);
    }

}