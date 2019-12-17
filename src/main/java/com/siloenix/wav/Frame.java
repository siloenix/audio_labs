package com.siloenix.wav;

public class Frame {
    private byte[] bytes;

    public Frame(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Frame wrap(byte[] bytes) {
        return new Frame(bytes);
    }

    public byte[] bytes() {
        return bytes;
    }
}
