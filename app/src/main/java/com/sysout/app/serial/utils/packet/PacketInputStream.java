package com.sysout.app.serial.utils.packet;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class PacketInputStream extends FilterInputStream {
    private PacketParser parser;
    private byte[] buffer;
    private int bufferPos = 0;
    private ByteArrayOutputStream byteArrayBuffer = new ByteArrayOutputStream();

    public PacketInputStream(InputStream in, PacketParser parser) {
        super(in);
        this.parser = parser;
        this.buffer = new byte[parser.getDataLength()];
    }

    public Packet readPacket() throws IOException {
        // 将上次剩余的无效数据写入缓冲区
        if (byteArrayBuffer.size() > 0) {
            byte[] invalidData = byteArrayBuffer.toByteArray();
            System.arraycopy(invalidData, 0, buffer, 0, invalidData.length);
            bufferPos = invalidData.length;
            byteArrayBuffer.reset();
        }

        while (bufferPos < parser.getDataLength()) {
            int read = in.read(buffer, bufferPos, parser.getDataLength() - bufferPos);
            if (read == -1) {
                return null; // EOF reached
            }
            bufferPos += read;
        }

        int start = findPacketStart(buffer);
        while (start == -1 && bufferPos >= 2) {
            System.arraycopy(buffer, 1, buffer, 0, bufferPos - 1);
            bufferPos--;
            int read = in.read(buffer, bufferPos, 1);
            if (read == -1) {
                return null; // EOF reached
            }
            bufferPos += read;
            start = findPacketStart(buffer);
        }

        if (start != 0) {
            byte[] remainingData = Arrays.copyOfRange(buffer, start, bufferPos);
            System.arraycopy(remainingData, 0, buffer, 0, remainingData.length);
            bufferPos = remainingData.length;
            return null;
        }

        if (!parser.isValid(buffer)) {
            byteArrayBuffer.write(buffer, 0, bufferPos);
            bufferPos = 0;
            return null; // 返回 null 表示无效数据包
        }

        if (!parser.checkChecksum(buffer)) {
            byteArrayBuffer.write(buffer, 0, bufferPos);
            bufferPos = 0;
            return null; // 返回 null 表示校验失败
        }

        Packet packet = parser.parse(Arrays.copyOf(buffer, parser.getDataLength()));
        bufferPos = 0;
        return packet;
    }

    private int findPacketStart(byte[] data) {
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] == 0x01 && data[i + 1] == 0x02) {
                return i;
            }
        }
        return -1;
    }
}