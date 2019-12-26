package com.siloenix;


import com.siloenix.wav.Frame;
import com.siloenix.wav.WavFile;

public class Lab_1 {
    public static void main(String[] args) throws Exception {
        WavFile wav = WavFile.readFile("./thermo.wav");
        wav.process(data -> {
            int amount = data.size();
            int back = amount - 1;
            int front = amount / 2;
            for (; front < back; front++, back--) {
                Frame frontFrame = data.get(front);
                data.set(front, data.get(back));
                data.set(back, frontFrame);
            }
        });
        wav.writeFile("./test.wav");
        wav.writeSplitFiles("./test_1.wav", "./test_2.wav");
    }
}

