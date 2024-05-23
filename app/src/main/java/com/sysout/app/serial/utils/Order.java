package com.sysout.app.serial.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * 命令标识
 * 标识符 方向 功能描述
 * 0x80 s -> r 上报所有状态信息
 * 0x81 s -> r 上报电量
 * 0x82 s -> r 上报按键转态
 * 0x83 s -> r 上报红外接近状态
 * 0x84 s -> r 上报舵机角度
 * 0xA0 s <- r 所有状态信息查询
 * 0xA1 s <- r 舵机控制
 * 0xA2 s <- r 指示灯控制
 * 0xA3 s <- r 红外检测控制
 * 0xA4 s <- r 工厂模式控制
 */
public class Order {


    public static HashMap<Integer, Order> orderMap;

    public static HashMap<Integer, Order> getOrderMap() {
        if (orderMap == null) {
            orderMap = new HashMap<>();
            // 1,2,2,(1,1,1,1),1,1,(4,4,4)
            orderMap.put(UP_STATE, new Order(UP_STATE, 1, 2, 2, 1, 1, 1, 1, 1, 1, 4, 4, 4));
            orderMap.put(UP_ELECTRICITY_STATE, new Order(UP_ELECTRICITY_STATE, 2));
            orderMap.put(UP_BUTTON_STATE, new Order(UP_BUTTON_STATE, 1, 1));
            orderMap.put(UP_RED_ULTRA_STATE, new Order(UP_RED_ULTRA_STATE, 1));
            orderMap.put(UP_RSERVO_STATE, new Order(UP_RSERVO_STATE, 1, 4));
            orderMap.put(UP_ADAPTER_STATE, new Order(UP_ADAPTER_STATE, 1));

            orderMap.put(DOWN_STATE, new Order(DOWN_STATE, 1));
            orderMap.put(DOWN_SERVO_STATE, new Order(DOWN_SERVO_STATE, 1, 4, 4));
            orderMap.put(DOWN_LAMP_STATE, new Order(DOWN_LAMP_STATE, 1, 1));
            orderMap.put(DOWN_RED_ULTRA_STATE, new Order(DOWN_RED_ULTRA_STATE, 1));
            orderMap.put(DOWN_FACTORY_STATE, new Order(DOWN_FACTORY_STATE, 1));
            orderMap.put(DOWN_ELECTRICITY_STATE, new Order(DOWN_ELECTRICITY_STATE, 1));


        }
        return orderMap;
    }

    private int cmd;
    public String cmdTitle;
    public int[] paramsLens;
    public Option[] options;
    public int len;

    public void setOptions(Option[] options) {
        this.options = options;
    }

    public Order(int cmd, int... paramsLens) {
        this.cmd = cmd;
        this.paramsLens = paramsLens;
        this.options = new Option[paramsLens.length];
        int len = 0;
        for (int l : paramsLens) {
            len += l;
        }
        this.len = len;
        this.cmdTitle = cmd + " -> [" + SerialDataUtils.Int2Hex(cmd) + "]";
        String info = null;
        switch (cmd) {
            case UP_STATE:
                info = "上报状态信息(23字节)";
                options[0] = new Option("类型")
                        .add("所有", 0)
                        .add("电压", 1)
                        .add("按键", 2)
                        .add("红外", 3)
                        .add("舵机", 4)
                        .add("适配器", 5);
                options[1] = new Option("故障位")
                        .add("待定0", 0);

                options[2] = new Option("电池电压")
                        .add("1000mv", 1000)
                        .add("3000mv", 3000)
                        .add("7271mv", 7271)
                        .add("9000mv", 9000);

                options[3] = new Option("按键一")
                        .add("按下1", 1)
                        .add("未知0", 0);
                options[4] = new Option("按键二")
                        .add("按下1", 1)
                        .add("未知0", 0);
                options[5] = new Option("按键三")
                        .add("按下1", 1)
                        .add("未知0", 0);
                options[6] = new Option("按键四")
                        .add("按下1", 1)
                        .add("未知0", 0);

                options[7] = new Option("红外状态")
                        .add("有人接近", 1)
                        .add("无人接近", 0);

                options[8] = new Option("适配器状态")
                        .add("充电", 1)
                        .add("未充电", 0);

                options[9] = new Option("舵机一")
                        .add("-700(-70度)", -700)
                        .add("-500(-50度)", -500)
                        .add("-300(-30度)", -300)
                        .add("-200(-20度)", -200)
                        .add("-100(-10度)", -100)
                        .add("100(10度)", 100)
                        .add("200(20度)", 200)
                        .add("300(30度)", 300)
                        .add("500(50度)", 500)
                        .add("700(70度)", 700);

                options[10] = new Option("舵机二")
                        .add("-700(-70度)", -700)
                        .add("-500(-50度)", -500)
                        .add("-300(-30度)", -300)
                        .add("-200(-20度)", -200)
                        .add("-100(-10度)", -100)
                        .add("100(10度)", 100)
                        .add("200(20度)", 200)
                        .add("300(30度)", 300)
                        .add("500(50度)", 500)
                        .add("700(70度)", 700);

                options[11] = new Option("舵机三")
                        .add("-700(-70度)", -700)
                        .add("-500(-50度)", -500)
                        .add("-300(-30度)", -300)
                        .add("-200(-20度)", -200)
                        .add("-100(-10度)", -100)
                        .add("100(10度)", 100)
                        .add("200(20度)", 200)
                        .add("300(30度)", 300)
                        .add("500(50度)", 500)
                        .add("700(70度)", 700);
                break;
            case UP_ELECTRICITY_STATE:
                info = "上报电池电压(2字节)";
                options[0] = new Option("电池电压")
                        .add("1000mv", 1000)
                        .add("3000mv", 3000)
                        .add("7271mv", 7271)
                        .add("9000mv", 9000);
                break;
            case UP_BUTTON_STATE:
                info = " 上报按键状态(2字节)";

                options[0] = new Option("按键序号")
                        .add("序号1", 1)
                        .add("序号2", 2)
                        .add("序号3", 3)
                        .add("序号4", 4)
                        .add("序号5", 5);

                options[1] = new Option("按键状态")
                        .add("按下1", 1)
                        .add("未知0", 0);
                break;
            case UP_RED_ULTRA_STATE:
                info = "上报红外状态(1字节)";
                options[0] = new Option("红外状态")
                        .add("有人接近", 1)
                        .add("无人接近", 0);
                break;
            case UP_RSERVO_STATE:
                info = "上报舵机角度(5字节)";
                options[0] = new Option("舵机序号")
                        .add("序号1", 1)
                        .add("序号2", 2)
                        .add("序号3", 3);

                options[1] = new Option("转动角度")
                        .add("-700(-70度)", -700)
                        .add("-500(-50度)", -500)
                        .add("-300(-30度)", -300)
                        .add("-200(-20度)", -200)
                        .add("-100(-10度)", -100)
                        .add("100(10度)", 100)
                        .add("200(20度)", 200)
                        .add("300(30度)", 300)
                        .add("500(50度)", 500)
                        .add("700(70度)", 700);
                break;
            case UP_ADAPTER_STATE:
                info = "上报适配器状态(1字节)";
                options[0] = new Option("适配器状态")
                        .add("充电", 1)
                        .add("未充电", 0);

                break;
            case DOWN_STATE:
                info = "状态信息查询(1字节)";
                options[0] = new Option("信息序号")
                        .add("所有信息", 0)
                        .add("电压", 1)
                        .add("按键", 2)
                        .add("红外", 3)
                        .add("舵机角度", 4)
                        .add("适配器", 5);;
                break;
            case DOWN_SERVO_STATE:
                info = "舵机控制(9字节)";
                options[0] = new Option("舵机序号")
                        .add("序号1", 1)
                        .add("序号2", 2)
                        .add("序号3", 3);
                options[1] = new Option("转动角度")
                        .add("-700(-70度)", -700)
                        .add("-500(-50度)", -500)
                        .add("-300(-30度)", -300)
                        .add("-200(-20度)", -200)
                        .add("-100(-10度)", -100)
                        .add("100(10度)", 100)
                        .add("200(20度)", 200)
                        .add("300(30度)", 300)
                        .add("500(50度)", 500)
                        .add("700(70度)", 700);

                options[2] = new Option("转动时间")
                        .add("100(ms)", 100)
                        .add("200(ms)", 200)
                        .add("300(ms)", 300)
                        .add("500(ms)", 500)
                        .add("700(ms)", 700)
                        .add("1000(ms)", 1000)
                        .add("2000(ms)", 2000)
                        .add("5000(ms)", 5000);
                break;
            case DOWN_LAMP_STATE:
                info = "指示灯控制(2个字节)";
                options[0] = new Option("指示灯颜色")
                        .add("红", 1)
                        .add("绿", 2)
                        .add("蓝", 3);
                options[1] = new Option("指示灯状态")
                        .add("灯亮", 1)
                        .add("灯灭", 0);

                break;
            case DOWN_RED_ULTRA_STATE:
                info = "红外检测控制(1字节)";
                options[0] = new Option("控制")
                        .add("关闭", 0)
                        .add("使能", 1);
                break;
            case DOWN_FACTORY_STATE:
                info = "工厂模式控制(1字节)";
                options[0] = new Option("控制")
                        .add("关闭", 0)
                        .add("使能", 1);
                break;
            case DOWN_ELECTRICITY_STATE:
                info = "舵机断|上电(1字节)";
                options[0] = new Option("控制")
                        .add("上电", 1)
                        .add("断电", 0);
                break;
        }
        this.cmdTitle += info;
    }

    public static List<KeyValue> values;

    public static List<KeyValue> getValues() {
        if (values == null) {
            values = new ArrayList<>();
            Set<Integer> integerSet = getOrderMap().keySet();
            for (Integer key : integerSet) {
                KeyValue keyValue = new KeyValue();
                keyValue.key = key;
                keyValue.value = getOrderMap().get(key).cmdTitle;
                values.add(keyValue);
            }
            Collections.sort(values, new Comparator<KeyValue>() {
                @Override
                public int compare(KeyValue o1, KeyValue o2) {
                    return o1.key > o2.key ? 1 : o1.key < o2.key ? -1 : 0;
                }
            });
        }
        return values;
    }

    public static class Option {
        public TreeMap<Integer, String> optionMap = new TreeMap<>();
        public String info;

        public Option(String info) {
            this.info = info;
        }

        public Option add(String info, int value) {
            optionMap.put(value, info);
            return this;
        }


        public List<KeyValue> getValues() {
            List<KeyValue> values = new ArrayList<>();
            Set<Integer> integerSet = optionMap.keySet();
            for (Integer key : integerSet) {
                KeyValue keyValue = new KeyValue();
                keyValue.key = key;
                keyValue.value = optionMap.get(key);
                values.add(keyValue);
            }
            return values;
        }
    }

    public static class KeyValue {
        public int key;
        public String value;
    }

    // 上报所有状态信息
    public static final int UP_STATE = 128; // 0x80
    // 上报电量
    public static final int UP_ELECTRICITY_STATE = 129; // 0x81
    // 上报按键转态
    public static final int UP_BUTTON_STATE = 130; // 0x82
    // 上报红外接近状态
    public static final int UP_RED_ULTRA_STATE = 131; // 0x83
    // 上报舵机角度
    public static final int UP_RSERVO_STATE = 132; // 0x84
    // 上报适配器状态
    public static final int UP_ADAPTER_STATE = 133; // 0x85


    // 所有状态信息查询
    public static final int DOWN_STATE = 160; // 0xA0
    // 舵机控制
    public static final int DOWN_SERVO_STATE = 161; // 0xA1
    // 指示灯控制
    public static final int DOWN_LAMP_STATE = 162; // 0xA2
    // 红外检测控制
    public static final int DOWN_RED_ULTRA_STATE = 163; // 0xA3
    // 工厂模式控制
    public static final int DOWN_FACTORY_STATE = 164; // 0xA4
    // 舵机上电、断电
    public static final int DOWN_ELECTRICITY_STATE = 165; // 0xA5


}