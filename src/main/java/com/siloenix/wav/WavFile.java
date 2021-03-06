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

    public void writeSplitFiles(String pathOne, String pathTwo) throws Exception {
        int size = data.size();
        int middle = size / 2;
        int leftDataSize = middle * blockAlign;
        int rightDataSize = (size - middle) * blockAlign;

        byte[] leftStartBytes = new byte[44];
        byte[] rightStartBytes = new byte[44];

        buffer.flip();
        buffer.position(0); // 12 + 16 + 8
        buffer.get(leftStartBytes);
        buffer.position(0); // 12 + 16 + 8
        buffer.get(rightStartBytes);

        ByteBuffer left = ByteBuffer.allocate(leftDataSize + 44);
        ByteBuffer right = ByteBuffer.allocate(rightDataSize + 44);
        left.order(ByteOrder.LITTLE_ENDIAN);
        right.order(ByteOrder.LITTLE_ENDIAN);

        left.put(leftStartBytes);
        right.put(rightStartBytes);

        left.position(4);
        right.position(4);
        left.putInt(leftDataSize + 36);
        right.putInt(rightDataSize + 36);

        left.position(40);
        right.position(40);
        left.putInt(leftDataSize);
        right.putInt(rightDataSize);

        left.position(44);
        right.position(44);
        for (int i = 0; i < middle; i++) {
            left.put(data.get(i).bytes());
        }
        for (int i = middle; i < size; i++) {
            right.put(data.get(i).bytes());
        }
        Files.write(new File(pathOne).toPath(), left.array(), StandardOpenOption.CREATE);
        Files.write(new File(pathTwo).toPath(), right.array(), StandardOpenOption.CREATE);
    }

    private void skip(int amount) {
        buffer.position(buffer.position() + amount);
    }

    public static void main(String[] args) throws IOException {
        WavFile file = new WavFile(new File("./thermo.wav"));
        file.readFile();
        file.parseFormat();
        file.parseData();

        file.printFormat();
    }

    public void splitChunksToFiles() throws Exception {
        buffer.rewind();
        List<ByteBuffer> chunks = new ArrayList<>();

        skip(12);
        do {
            long type = buffer.getInt();
            long size = buffer.getInt();
            byte[] bytes = new byte[(int) size];
            ByteBuffer chunk = ByteBuffer.allocate((int) (size + 8));
            chunk.order(ByteOrder.LITTLE_ENDIAN);
            buffer.get(bytes);
            chunk.putInt((int) type);
            chunk.putInt((int) size);
            chunk.put(bytes);
            chunks.add(chunk);
        } while (buffer.hasRemaining());

        for (int i = 0; i < chunks.size(); i++) {
            Files.write(new File("chunk_" + i + ".txt").toPath(), chunks.get(i).array(), StandardOpenOption.CREATE);
        }

    }
}
