package com.short_tank.message;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message {

    private byte type; 
    private ByteArrayOutputStream bos;
    private DataOutputStream dos;

    private ByteArrayInputStream bis;
    private DataInputStream dis;

    public Message(byte type) {
        this.type = type;
        this.bos = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(bos);
    }

    public Message(byte type, byte[] data) {
        this.type = type;
        this.bis = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(bis);
    }

    public byte getType() {
        return type;
    }

    public void writeInt(int v) throws IOException { dos.writeInt(v); }
    public void writeLong(long v) throws IOException { dos.writeLong(v); }
    public void writeFloat(float v) throws IOException { dos.writeFloat(v); }
    public void writeDouble(double v) throws IOException { dos.writeDouble(v); }
    public void writeBoolean(boolean v) throws IOException { dos.writeBoolean(v); }
    public void writeByte(int v) throws IOException { dos.writeByte(v); }
    public void writeUTF(String s) throws IOException { dos.writeUTF(s); }
    public void writeBytes(byte[] b) throws IOException { dos.write(b); }

    public int readInt() throws IOException { return dis.readInt(); }
    public long readLong() throws IOException { return dis.readLong(); }
    public float readFloat() throws IOException { return dis.readFloat(); }
    public double readDouble() throws IOException { return dis.readDouble(); }
    public boolean readBoolean() throws IOException { return dis.readBoolean(); }
    public byte readByte() throws IOException { return dis.readByte(); }
    public String readUTF() throws IOException { return dis.readUTF(); }
    public int read(byte[] b) throws IOException { return dis.read(b); }

    public byte[] getData() {
        return bos.toByteArray();
    }

    public void close() {
        try {
            if (bos != null) bos.close();
            if (dos != null) dos.close();
            if (bis != null) bis.close();
            if (dis != null) dis.close();
        } catch (IOException ignored) {}
    }
}
