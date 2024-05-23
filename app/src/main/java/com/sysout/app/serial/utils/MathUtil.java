package com.sysout.app.serial.utils;

public class MathUtil {

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] hexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    public static String bytesToHex(byte[] inBytArr)//字节数组转转hex字符串
    {
        StringBuilder strBuilder=new StringBuilder();
        int j=inBytArr.length;
        for (int i = 0; i < j; i++)
        {
            strBuilder.append(byte2Hex(inBytArr[i]));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    public static String byte2Hex(Byte inByte)//1字节转2个Hex字符
    {
        return String.format("%02x", inByte).toUpperCase();
    }
}
