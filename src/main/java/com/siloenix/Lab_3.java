package com.siloenix;

import com.siloenix.midi.Midi;

public class Lab_3 {
    public static void main(String[] args) throws Exception {
        Midi midi = Midi.fromFile("./midi.txt");
        midi.play();
    }
}
