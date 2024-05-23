package com.sysout.app.serial.utils;

import android.util.Log;

import com.sysout.app.serial.utils.packet.Packet;
import com.sysout.app.serial.utils.packet.PacketInputStream;
import com.sysout.app.serial.utils.packet.WeightPacket;
import com.sysout.app.serial.utils.packet.WeightPacketParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android_serialport_api.SerialPort;

/**
 * @author
 *串口辅助工具类
 */
public abstract class SerialHelper{
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;

    private String sPort = "/dev/ttyS1";
    private int iBaudRate = 9600;
    private int parity = 0;
    private int dataBits = 8;
    private int stopBit = 1;

    byte[] tempBuff = new byte[2048];

    private boolean _isOpen=false;
    private List<byte[]> _bLoopDataList = new ArrayList<>();
    private int iDelay=500;
    //----------------------------------------------------
    public SerialHelper(String sPort, int iBaudRate, int parity, int dataBits, int stopBit){
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
        this.parity = parity;
        this.dataBits = dataBits;
        this.stopBit = stopBit;
    }

    public SerialHelper(String sPort, int iBaudRate){
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }
    public SerialHelper(){
        this("/dev/ttyS1",9600);
    }

    public SerialHelper(String sPort){
        this(sPort,9600);
    }

    public SerialHelper(String sPort, String sBaudRate){
        this(sPort,Integer.parseInt(sBaudRate));
    }
    //----------------------------------------------------
    public void open() throws SecurityException, IOException,InvalidParameterException {
        mSerialPort =  new SerialPort(new File(sPort), iBaudRate, parity, dataBits, stopBit, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();
        _isOpen = true;
        Log.i("SerialPort", "open serial success: port:" + sPort + ", baudRate:" + iBaudRate + ", parity:" + parity + ", dataBits:" + dataBits + ", stopBit:" + stopBit);
    }
    //----------------------------------------------------
    public void close(){
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        _isOpen=false;
        Log.i("SerialPort", "close serial success: port:" + sPort);
    }
    //----------------------------------------------------
    public void send(byte[] bOutArray){
        try
        {
            mOutputStream.write(bOutArray);
            onSendDataReceived(bOutArray);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------
    public void sendHex(String sHex){
        byte[] bOutArray = MathUtil.hexString2Bytes(sHex);
        send(bOutArray);
    }
    //----------------------------------------------------
    public void sendTxt(String sTxt){
        byte[] bOutArray =sTxt.getBytes();
        send(bOutArray);
    }
    //----------------------------------------------------
    private class ReadThread extends Thread {
        private final PacketInputStream packetInputStream;

        public ReadThread() {
            this.packetInputStream = new PacketInputStream(mInputStream, new WeightPacketParser());
        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    Packet packet = packetInputStream.readPacket();
                    if (packet != null) {
                        if (packet instanceof WeightPacket) {
                            onDataReceived((WeightPacket) packet);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    //----------------------------------------------------
    private class SendThread extends Thread{
        public boolean suspendFlag = true;// 控制线程的执行
        @Override
        public void run() {
            super.run();
            int loopIndex = 0;
            while(!isInterrupted()) {
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    if(loopIndex >= _bLoopDataList.size()){
                        loopIndex = 0;
                    }
                    send(_bLoopDataList.get(loopIndex++));
                    Thread.sleep(iDelay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        //唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }
    //----------------------------------------------------
    public int getBaudRate()
    {
        return iBaudRate;
    }
    public boolean setBaudRate(int iBaud)
    {
        if (_isOpen)
        {
            return false;
        } else
        {
            iBaudRate = iBaud;
            return true;
        }
    }
    public boolean setBaudRate(String sBaud)
    {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }
    //----------------------------------------------------
    public String getPort()
    {
        return sPort;
    }
    public boolean setPort(String sPort)
    {
        if (_isOpen)
        {
            return false;
        } else
        {
            this.sPort = sPort;
            return true;
        }
    }
    //----------------------------------------------------
    public boolean isOpen()
    {
        return _isOpen;
    }
    //----------------------------------------------------
    public void setbLoopDataList(List<byte[]> loopDataList) {
        _bLoopDataList = loopDataList;
    }
    public List<byte[]> getbLoopDataList() {
        return _bLoopDataList;
    }
    public void clearLoopDataList(){
        _bLoopDataList.clear();
    }
    //----------------------------------------------------
    public void addbLoopData(byte[] bLoopData) {
        this._bLoopDataList.add(bLoopData);
    }
    //----------------------------------------------------
    public void addTxtLoopData(String sTxt){
        this._bLoopDataList.add(sTxt.getBytes());
    }
    //----------------------------------------------------
    public void addHexLoopData(String sHex){
        this._bLoopDataList.add(MathUtil.hexString2Bytes(sHex));
    }
    //----------------------------------------------------
    public int getiDelay()
    {
        return iDelay;
    }
    //----------------------------------------------------
    public void setiDelay(int iDelay)
    {
        this.iDelay = iDelay;
    }
    //----------------------------------------------------
    public void startSend() {
        if (mSendThread != null)
        {
            mSendThread.setResume();
        }
    }
    //----------------------------------------------------
    public void stopSend() {
        if (mSendThread != null)
        {
            mSendThread.setSuspendFlag();
        }
    }
    //----------------------------------------------------
    protected abstract void onDataReceived(byte[] buff);
    protected abstract void onDataReceived(WeightPacket packet);
    protected abstract void onSendDataReceived(byte[] buff);
}