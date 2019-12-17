package com.siloenix.midi;

import java.util.List;

public class Note {
    public String name;
    public int octave;
    public int duration;

    public Note(String name, int octave, int duration) {
        this.name = name;
        this.octave = octave;
        this.duration = duration;
    }

    public static Note parse(String notation) {
        String[] parts = notation.replace(";", "").split(",");
        return new Note(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
}
