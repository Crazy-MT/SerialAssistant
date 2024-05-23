package com.sysout.app.serial.utils.packet;

public abstract class Packet {
    protected byte[] data;

    public Packet(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public abstract String getNetWeight();
    public abstract String getTareWeight();
    public abstract byte getStatus();
}