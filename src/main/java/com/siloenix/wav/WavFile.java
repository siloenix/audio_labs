package com.siloenix.wav;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WavFile {
    private final static int FMT_CHUNK_ID = 0x20746D66;
    private final static int DATA_CHUNK_ID = 0x61746164;
    private final static int RIFF_CHUNK_ID = 0x46464952;
    private final static int RIFF_TYPE_ID = 0x45564157;

    private File file;
    private ByteBuffer buffer;

    // format
    private long formatChunkSize;
    private int compressionCode;
    private int channelAmount;
    private long sampleRate;
    private long averageBytesPerSecond;
    private int blockAlign;
    private int bitsPerSample;

    // data
    List<Frame> data = new ArrayList<>();

    WavFile(File file) throws FileNotFoundException {
        this.file = file;
    }

    public void readFile() throws IOException {
        byte[] fileData = Files.readAllBytes(file.toPath());
        buffer = ByteBuffer.wrap(fileData);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void parseFormat() {
        skip(12);
        int chunkId = buffer.getInt();
        if (chunkId != FMT_CHUNK_ID) {
            throw new RuntimeException("not format chunk");
        }

        this.formatChunkSize = buffer.getInt();
        this.compressionCode = buffer.getChar();
        this.channelAmount = buffer.getChar();
        this.sampleRate = buffer.getInt();
        this.averageBytesPerSecond = buffer.getInt();
        this.blockAlign = buffer.getChar();
        this.bitsPerSample = buffer.getChar();
        printFormat();
    }

    public void parseData() {
        if (buffer.getInt() != DATA_CHUNK_ID) {
            throw new RuntimeException("not data chunk");
        }
        long size = buffer.getInt();
        long framesAmount = size / blockAlign;
        System.out.println("Frames: " + framesAmount);
        for (int i = 0; i < framesAmount; i++) {
            byte[] bytes = new byte[blockAlign];
            buffer.get(bytes);
            data.add(Frame.wrap(bytes));
        }
    }

    public void printFormat(PrintStream out) {
        out.println("Format chunk size: " + formatChunkSize);
        out.println("Compression code: " + compressionCode);
        out.println("Number of channels: " + channelAmount);
        out.println("Sample rate: " + sampleRate);
        out.println("Average bytes per second: " + averageBytesPerSecond);
        out.println("Block align (bytes): " + blockAlign);
        out.println("Bits per sample: " + bitsPerSample);
    }

    public void printFormat() {
        printFormat(System.out);
    }

    public static WavFile readFile(String path) throws Exception {
        WavFile wav = new WavFile(new File(path));
        wav.readFile();
        wav.parseFormat();
        wav.parseData();
        return wav;
    }

    public void process(Consumer<List<Frame>> consumer) {
        consumer.accept(data);
    }

    public void writeFile(String path) throws Exception {
        buffer.flip();
        buffer.position(44); // 12 + 16 + 8
        data.forEach(frame -> buffer.put(frame.bytes()));
        Files.write(new File(path).toPath(), buffer.array(), StandardOpenOption.CREATE);
    }

    private void skip(int amount) {
        buffer.position(buffer.position() + amount);
    }

    public static void main(String[] args) throws IOException {
        WavFile file = new WavFile(new File("./wav_file.wav"));
        file.readFile();
        file.parseFormat();
        file.parseData();

        file.printFormat();
    }
}
