package com.sysout.app.serial.utils.packet;

public class WeightPacket extends Packet {

    public WeightPacket(byte[] data) {
        super(data);
    }

    @Override
    public String getNetWeight() {
        return new String(data, 2, 7);
    }

    @Override
    public String getTareWeight() {
        return new String(data, 11, 7);
    }

    @Override
    public byte getStatus() {
        return data[20];
    }

    public boolean isStableWeight() {
        return (data[20] & 0x08) != 0;
    }

    public static String parseStatus(byte status) {
        StringBuilder sb = new StringBuilder();
        sb.append("MTMTMT Weight Overflow: ").append((status & 0x80) != 0).append("\n");
        sb.append("MTMTMT Not Zeroed on Power-up: ").append((status & 0x40) != 0).append("\n");
        sb.append("MTMTMT Tare Mode: ").append((status & 0x20) != 0).append("\n");
        sb.append("MTMTMT Weight is Zero: ").append((status & 0x10) != 0).append("\n");
        sb.append("MTMTMT Weight Stable: ").append((status & 0x08) != 0).append("\n");
        return sb.toString();
    }
}

