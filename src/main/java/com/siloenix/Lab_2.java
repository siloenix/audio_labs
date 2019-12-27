package com.siloenix;

import com.siloenix.wav.WavFile;

import java.io.File;
import java.io.PrintStream;

public class Lab_2 {
    public static void main(String[] args) throws Exception {
        WavFile wavFile = WavFile.readFile("./thermo.wav");
        wavFile.printFormat(new PrintStream(new File("./format.txt")));
        wavFile.splitChunksToFiles();
        System.out.println("written to file");
    }
}

