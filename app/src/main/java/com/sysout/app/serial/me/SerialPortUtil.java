//package com.sysout.app.serial.me;
//
//import android.serialport.SerialPort;
//import android.text.TextUtils;
//
//import com.thfw.base.utils.LogUtil;
//import com.vi.vioserial.COMSerial;
//import com.vi.vioserial.NormalSerial;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.Arrays;
//
///**
// * Author:pengs
// * Date: 2022/3/21 10:42
// * Describe:Todo
// */
//public class SerialPortUtil {
//
//    private static final String TAG = SerialPortUtil.class.getSimpleName();
//
//    public static final String PORT_NAME = "/dev/ttyS4";
//    public static final int IBAUDTATE = 115200;
//
//    private static final int HEAD = 49358; // 0xC0CE c0 = 192 206
//    private static final String HEAD_STR = com.thfw.robotheart.util.SerialDataUtils.Int2Hex(HEAD); // C0CE
//    private static final int HEAD_BIT_COUNT = 192 + 206;
//
//
//    /**
//     * 打开串口
//     *
//     * @return 0：打开串口成功
//     * -1：无法打开串口：没有串口读/写权限！
//     * -2：无法打开串口：未知错误！
//     * -3：无法打开串口：参数错误！
//     */
//    public static void open() {
//        open(PORT_NAME, IBAUDTATE);
//    }
//
//
//    public static int open(String portStr, int ibaudRate) {
//        SerialPort.setSuPath("/system/xbin/su");
//        LogUtil.d(TAG, "portStr = " + portStr + ", ibaudRate = " + ibaudRate);
//        int openCode = NormalSerial.instance().open(portStr, ibaudRate);
//        LogUtil.d(TAG, "串口打开：" + openCode);
//        return openCode;
//    }
//
//    /**
//     * 发送数据
//     *
//     * @param order
//     * @param arrayBytes
//     */
//    public static String sendOrder(int order, byte... arrayBytes) {
//        /**
//         * 注意发送的数据类型为hex，字符串需要转成hex在发送
//         * 转换方法：SerialDataUtils.stringToHexString(String s)
//         * @param portStr 串口号（即需要往哪个串口发送数据）
//         * @param hexData 发送的数据
//         */
//        String sendData = getSendData(order, arrayBytes);
//        LogUtil.d(TAG, "sendData -> " + sendData);
//        COMSerial.instance().sendHex(PORT_NAME, sendData);
//        return sendData;
//    }
//
//    /**
//     * 构建协议
//     *
//     * @param order      命令
//     * @param arrayBytes 参数
//     * @return
//     */
//    public static String getSendData(int order, byte... arrayBytes) {
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append(com.thfw.robotheart.util.SerialDataUtils.Int2Hex(HEAD));
//        stringBuffer.append(com.thfw.robotheart.util.SerialDataUtils.Int2Hex(order));
//        // 写入数据按小端处理（4660 0x1234 -> 0x34 0x12）
//        ByteBuffer bb = ByteBuffer.allocate(100);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        bb.put(arrayBytes);
//        // 读取
//        bb.flip();
//        int limit = bb.limit();
//        LogUtil.d(TAG, "limit -> " + limit);
//        stringBuffer.append(com.thfw.robotheart.util.SerialDataUtils.Int2Hex(limit));
//
//        int count = HEAD_BIT_COUNT + order + limit;
//        while (bb.position() < limit) {
//            String byte2Hex = com.thfw.robotheart.util.SerialDataUtils.Byte2Hex(Byte.valueOf(bb.get()));
//            stringBuffer.append(byte2Hex);
//            LogUtil.d(TAG, "byte2Hex -> " + byte2Hex);
//            int bitCount = Integer.parseInt(byte2Hex, 16);
//            LogUtil.d(TAG, "bitCount -> " + bitCount);
//            count = count + bitCount;
//
//        }
//        LogUtil.d(TAG, "count -> " + count);
//        int checkBitInt = count % 256;
//        LogUtil.d(TAG, "checkBitInt -> " + checkBitInt);
//        stringBuffer.append(com.thfw.robotheart.util.SerialDataUtils.Int2Hex(checkBitInt));
//        LogUtil.d(TAG, "send final -> " + stringBuffer.toString());
//        return stringBuffer.toString();
//    }
//
//    /**
//     * 解析数据
//     *
//     * @param data
//     */
//    public static void parseOrder(String data) {
//        LogUtil.d(TAG, "parseOrder - data -> " + data);
//        if (TextUtils.isEmpty(data) || !data.startsWith(HEAD_STR)) {
//            LogUtil.d(TAG, "parseOrder - data -> 数据不合法");
//            return;
//        }
//        data = data.replace(HEAD_STR, "");
//        byte[] bytes = com.thfw.robotheart.util.SerialDataUtils.HexToByteArr2(data);
//
//        String orderHex = com.thfw.robotheart.util.SerialDataUtils.Byte2Hex(Byte.valueOf(bytes[0]));
//        LogUtil.d(TAG, "parseOrder - orderHex -> " + orderHex);
//        int order = Integer.parseInt(orderHex, 16);
//        LogUtil.d(TAG, "parseOrder - order -> " + order);
//
//        String lenHex = com.thfw.robotheart.util.SerialDataUtils.Byte2Hex(Byte.valueOf(bytes[1]));
//        LogUtil.d(TAG, "parseOrder - lenHex -> " + lenHex);
//        int len = Integer.parseInt(lenHex, 16);
//        LogUtil.d(TAG, "parseOrder - len -> " + len);
//
//        int contentLen = len + 2;
//        int[] byteContents = new int[len];
//        for (int contentBegin = 2; contentBegin < contentLen; contentBegin++) {
//            String hex = com.thfw.robotheart.util.SerialDataUtils.Byte2Hex(Byte.valueOf(bytes[contentBegin]));
//            int hexInt = Integer.parseInt(hex, 16);
//            LogUtil.d(TAG, "parseOrder - hex -> " + hex);
//            LogUtil.d(TAG, "parseOrder - hexInt -> " + hexInt);
//            byteContents[contentBegin - 2] = hexInt;
//        }
//        // 处理数据
//        onHandleOrder(order, byteContents);
//    }
//
//    private static ParseDataListener parseDataListener;
//
//    public static void setParseDataListener(ParseDataListener parseDataListener) {
//        SerialPortUtil.parseDataListener = parseDataListener;
//    }
//
//    public interface ParseDataListener {
//        void onHandleOrder(int order, int[] bytes);
//    }
//
//    /**
//     * 处理数据
//     *
//     * @param order
//     * @param bytes
//     */
//    private static void onHandleOrder(int order, int[] bytes) {
//        if (parseDataListener != null) {
//            parseDataListener.onHandleOrder(order, bytes);
//        }
//        LogUtil.d(TAG, "onHandleOrder - order -> " + order);
//        LogUtil.d(TAG, "onHandleOrder - bytes -> " + Arrays.toString(bytes));
//        switch (order) {
//            case Order.DOWN_LAMP_STATE:
//                break;
//            case Order.DOWN_RED_ULTRA_STATE:
//                break;
//            case Order.DOWN_SERVO_STATE:
//                break;
//            case Order.DOWN_STATE:
//                break;
//            case Order.UP_BUTTON_STATE:
//                break;
//            case Order.UP_RED_ULTRA_STATE:
//                break;
//            case Order.UP_STATE:
//                break;
//            case Order.UP_ELECTRICITY_STATE:
//                break;
//
//        }
//    }
//
//
//    /**
//     * 命令标识
//     * 标识符 方向 功能描述
//     * 1 -- 0x80 s -> r 上报所有状态信息
//     * 2 -- 0x81 s -> r 上报电量
//     * 3 -- 0x82 s -> r 上报按键转态
//     * 4 -- 0x83 s -> r 上报红外接近状态
//     * 5 -- 0xA0 s <- r 所有状态信息查询
//     * 6 -- 0xA1 s <- r 舵机控制
//     * 7 -- 0xA2 s <- r 指示灯控制
//     * 8 -- 0xA3 s <- r 红外检测控制
//     */
//    public static class Order {
//
//        // 上报所有状态信息
//        public static final short UP_STATE = 128; // 0x80
//        // 上报电量
//        public static final short UP_ELECTRICITY_STATE = 129; // 0x81
//        // 上报按键转态
//        public static final short UP_BUTTON_STATE = 130; // 0x82
//        // 上报红外接近状态
//        public static final short UP_RED_ULTRA_STATE = 131; // 0x83
//
//        // 所有状态信息查询
//        public static final short DOWN_STATE = 160; // 0xA0
//        // 舵机控制
//        public static final short DOWN_SERVO_STATE = 161; // 0xA1
//        // 指示灯控制
//        public static final short DOWN_LAMP_STATE = 162; // 0xA2
//        // 红外检测控制
//        public static final short DOWN_RED_ULTRA_STATE = 163; // 0xA3
//
//
//    }
//
//}
