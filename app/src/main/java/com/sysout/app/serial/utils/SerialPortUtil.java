package com.sysout.app.serial.utils;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Author:pengs
 * Date: 2022/3/21 10:42
 * Describe:Todo
 */
public class SerialPortUtil {

    private static final String TAG = SerialPortUtil.class.getSimpleName();

    public static final String PORT_NAME = "/dev/ttyS4";
    public static final int IBAUDTATE = 115200;

    private static final int HEAD = 49358; // 0xC0CE c0 = 192 206
    private static final String HEAD_STR = SerialDataUtils.Int2Hex(HEAD); // C0CE
    private static final int HEAD_BIT_COUNT = 192 + 206;


    /**
     * 构建协议
     *
     * @param order      命令
     * @param arrayBytes 参数
     * @return
     */
    public static String getSendData(int order, byte... arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SerialDataUtils.Int2Hex(HEAD));
        stringBuffer.append(SerialDataUtils.Int2Hex(order));
        // 写入数据按小端处理（4660 0x1234 -> 0x34 0x12）
        IntBuffer intBuffer = IntBuffer.allocate(100);

        ByteBuffer bb = ByteBuffer.allocate(100);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(arrayBytes);
        // 读取
        bb.flip();
        int limit = bb.limit();
        Log.d(TAG, "limit -> " + limit);
        stringBuffer.append(SerialDataUtils.Int2Hex(limit));
        stringBuffer.append(SerialDataUtils.Int2Hex(0));

        int count = HEAD_BIT_COUNT + order + limit;
        while (bb.position() < limit) {
            String byte2Hex = SerialDataUtils.Byte2Hex(Byte.valueOf(bb.get()));
            stringBuffer.append(byte2Hex);
            Log.d(TAG, "byte2Hex -> " + byte2Hex);
            int bitCount = Integer.parseInt(byte2Hex, 16);
            Log.d(TAG, "bitCount -> " + bitCount);
            count = count + bitCount;

        }
        Log.d(TAG, "count -> " + count);
        int checkBitInt = count % 256;
        Log.d(TAG, "checkBitInt -> " + checkBitInt);
        stringBuffer.append(SerialDataUtils.Int2Hex(checkBitInt));
        Log.d(TAG, "send final -> " + stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * 构建协议
     *
     * @param order      命令
     * @param arrayBytes 参数
     * @return
     */
    public static String getSendData(int order, int... arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SerialDataUtils.Int2Hex(HEAD));
        stringBuffer.append(SerialDataUtils.Int2Hex(order));
        // 写入数据按小端处理（4660 0x1234 -> 0x34 0x12）
        ByteBuffer bb = ByteBuffer.allocate(1000);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        Order mOrder = Order.getOrderMap().get(order);
        if (mOrder == null) {
            LogUtils.d(TAG, "不支持order = " + order);
            return "不支持该指令";
        }
        if (order == Order.UP_STATE) {
            int msgType = arrayBytes[0];
            switch (msgType) {
                case 1:
                    mOrder.paramsLens = new int[]{1, 2, 2};
                    break;
                case 2:
                    mOrder.paramsLens = new int[]{1, 2, 1, 1, 1, 1};
                    break;
                case 3:
                    mOrder.paramsLens = new int[]{1, 2, 1};
                    break;
                case 4:
                    mOrder.paramsLens = new int[]{1, 2, 4, 4, 4};
                    break;
                case 5:
                    mOrder.paramsLens = new int[]{1, 2, 1};
                    break;
            }
        }
        for (int i = 0; i < arrayBytes.length; i++) {
            if (i < mOrder.paramsLens.length) {
                if (mOrder.paramsLens[i] == 1) {
                    bb.put((byte) arrayBytes[i]);
                } else {
                    LogUtils.d(TAG, "Int2Bytes_LE = " + Arrays.toString(ByteConvert.Int2Bytes_LE(arrayBytes[i])));
                    byte[] src = ByteConvert.Int2Bytes_LE(arrayBytes[i]);
                    if (src != null && src.length >= mOrder.paramsLens[i]) {
                        bb.put(src, 0, mOrder.paramsLens[i]);
                    } else {
                        byte[] newSrc = new byte[mOrder.paramsLens[i]];
                        bb.put(newSrc);
                    }
                }
            }
        }

        // 读取
        bb.flip();
        int limit = bb.limit();
        Log.d(TAG, "limit -> " + limit);

        stringBuffer.append(SerialDataUtils.Int2Hex(mOrder.len));
        stringBuffer.append(SerialDataUtils.Int2Hex(0));

        int count = HEAD_BIT_COUNT + order + limit;
        while (bb.position() < limit) {
            String byte2Hex = SerialDataUtils.Byte2Hex(Byte.valueOf(bb.get()));
            stringBuffer.append(byte2Hex);
            Log.d(TAG, "byte2Hex -> " + byte2Hex);
            int bitCount = Integer.parseInt(byte2Hex, 16);
            Log.d(TAG, "bitCount -> " + bitCount);
            count = count + bitCount;

        }
        Log.d(TAG, "count -> " + count);
        int checkBitInt = count % 256;
        Log.d(TAG, "checkBitInt -> " + checkBitInt);
        stringBuffer.append(SerialDataUtils.Int2Hex(checkBitInt));
        Log.d(TAG, "send final -> " + stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * @param: [hex]
     * @return: int
     * @description: 按位计算，位值乘权重
     */
    public static int hexToDecimal(String hex) {
        int outcome = 0;
        for (int i = 0; i < hex.length(); i++) {
            char hexChar = hex.charAt(i);
            outcome = outcome * 16 + charToDecimal(hexChar);
        }
        return outcome;
    }

    /**
     * @param: [c]
     * @return: int
     * @description:将字符转化为数字
     */
    public static int charToDecimal(char c) {
        if (c >= 'A' && c <= 'F')
            return 10 + c - 'A';
        else
            return c - '0';
    }

    /**
     * 解析数据
     *
     * @param data
     */
    public static void parseOrder(String data) {
        Log.d(TAG, "parseOrder - data -> " + data);
        if (TextUtils.isEmpty(data)) {
            Log.d(TAG, "parseOrder - data -> null");
            return;
        }
        data = data.replaceAll(" ", "");
        if (TextUtils.isEmpty(data) || !data.startsWith(HEAD_STR)) {
            Log.d(TAG, "parseOrder - data -> 数据不合法");
            return;
        }
        data = data.replace(HEAD_STR, "");
        byte[] bytes = SerialDataUtils.HexToByteArr2(data);

        String orderHex = SerialDataUtils.Byte2Hex(Byte.valueOf(bytes[0]));
        Log.d(TAG, "parseOrder - orderHex -> " + orderHex);
        int order = Integer.parseInt(orderHex, 16);
        Log.d(TAG, "parseOrder - order -> " + order);
        Order mOrder = Order.getOrderMap().get(order);
        if (mOrder == null) {
            // 处理数据
            onHandleOrder(order, new int[]{});
            return;
        }
        String lenHex = SerialDataUtils.Byte2Hex(Byte.valueOf(bytes[1]));
        Log.d(TAG, "parseOrder - lenHex -> " + lenHex);
        int len = Integer.parseInt(lenHex, 16);
        Log.d(TAG, "parseOrder - len -> " + len);

        byte[] newBytes = Arrays.copyOfRange(bytes, 3, bytes.length);
        if (order == Order.UP_STATE) {
            int msgType = newBytes[0];
            Log.d(TAG, "parseOrder - msgType -> " + msgType);
            switch (msgType) {
                case 1:
                    mOrder.paramsLens = new int[]{1, 2, 2};
                    break;
                case 2:
                    mOrder.paramsLens = new int[]{1, 2, 1, 1, 1, 1};
                    break;
                case 3:
                    mOrder.paramsLens = new int[]{1, 2, 1};
                    break;
                case 4:
                    mOrder.paramsLens = new int[]{1, 2, 4, 4, 4};
                    break;
                case 5:
                    mOrder.paramsLens = new int[]{1, 2, 1};
                    break;
            }
        }
        int[] intContents = new int[mOrder.paramsLens.length];
        int from = 0;
        for (int i = 0; i < mOrder.paramsLens.length; i++) {
            byte[] byteNumbers = Arrays.copyOf(Arrays.copyOfRange(newBytes, from, from + mOrder.paramsLens[i]), 4);
            intContents[i] = ByteConvert.Bytes2Int_LE(byteNumbers);
            from += mOrder.paramsLens[i];
        }
        // 处理数据
        onHandleOrder(order, intContents);
    }

    private static ParseDataListener parseDataListener;

    public static void setParseDataListener(ParseDataListener parseDataListener) {
        SerialPortUtil.parseDataListener = parseDataListener;
    }

    public interface ParseDataListener {
        void onHandleOrder(int order, int[] bytes);
    }

    /**
     * 处理数据
     *
     * @param order
     * @param bytes
     */
    private static void onHandleOrder(int order, int[] bytes) {
        if (parseDataListener != null) {
            parseDataListener.onHandleOrder(order, bytes);
        }
        Log.d(TAG, "onHandleOrder - order -> " + order);
        Log.d(TAG, "onHandleOrder - bytes -> " + Arrays.toString(bytes));
        switch (order) {
            case Order.DOWN_LAMP_STATE:
                break;
            case Order.DOWN_RED_ULTRA_STATE:
                break;
            case Order.DOWN_SERVO_STATE:
                break;
            case Order.DOWN_STATE:
                break;
            case Order.UP_BUTTON_STATE:
                break;
            case Order.UP_RED_ULTRA_STATE:
                break;
            case Order.UP_STATE:
                break;
            case Order.UP_ELECTRICITY_STATE:
                break;

        }
    }


}
