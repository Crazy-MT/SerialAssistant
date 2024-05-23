package com.sysout.app.serial.utils.packet;

public interface PacketParser {
    int getDataLength();
    // 校验数据
    boolean isValid(byte[] data);
    // BCC 校验
    boolean checkChecksum(byte[] data);
    Packet parse(byte[] data);
}