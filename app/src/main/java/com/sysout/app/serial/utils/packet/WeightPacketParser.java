package com.sysout.app.serial.utils.packet;

public class WeightPacketParser implements PacketParser {
    @Override
    public int getDataLength() {
        return 24;
    }

    @Override
    public boolean isValid(byte[] data) {
        return data[0] == 0x01 && data[1] == 0x02 && data[22] == 0x03 && data[23] == 0x04;
    }

    @Override
    public boolean checkChecksum(byte[] data) {
        byte checksum = 0;
        for (int i = 2; i < 21; i++) {
            checksum ^= data[i];
        }
        return checksum == data[21];
    }

    @Override
    public Packet parse(byte[] data) {
        return new WeightPacket(data);
    }
}