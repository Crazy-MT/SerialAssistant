package com.pl.sphelper;


/**
 * Created by l4656_000 on 2015/11/30.
 */
public class ConstantUtil {
    // normal constants
    public static final String CONTENT = "content://";
    public static final String AUTHORITY = "com.pl.sphelper";
    public static final String SEPARATOR = "/";
    public static final String CONTENT_URI = CONTENT + AUTHORITY;
    public static final String TYPE_STRING_SET = "string_set";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_INT = "int";
    public static final String TYPE_LONG = "long";
    public static final String TYPE_FLOAT = "float";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String VALUE = "value";

    public static final String NULL_STRING = "null";
    public static final String TYPE_CONTAIN = "contain";
    public static final String TYPE_CLEAN = "clean";
    public static final String TYPE_GET_ALL = "get_all";

    public static final String CURSOR_COLUMN_NAME = "cursor_name";
    public static final String CURSOR_COLUMN_TYPE = "cursor_type";
    public static final String CURSOR_COLUMN_VALUE = "cursor_value";

    public static final int DEFAULT_INT = -9999;

    public static boolean isDefault(int def) {
        return DEFAULT_INT == def;
    }

    public static boolean isNotDefault(int def) {
        return DEFAULT_INT != def;
    }

    public static class Key {
        // 摇头舵机 零位
        public static final String SHAKE_ZERO = "snake_zero";
        // 点头舵机 零位
        public static final String NOD_ZERO = "nod_zero";
        // 转身舵机 零位
        public static final String ROTATE_ZERO = "rotate_zero";
        // 摇头舵机【左】极限位
        public static final String SHAKE_LEFT_MAX = "shake_left_max";
        // 摇头舵机【右】极限位
        public static final String SHAKE_RIGHT_MAX = "shake_right_max";
        // 点头舵机【上|左】极限位
        public static final String NOD_LEFT_MAX = "nod_left_max";
        // 点头舵机【下|右】极限位
        public static final String NOD_RIGHT_MAX = "nod_right_max";
    }


}
