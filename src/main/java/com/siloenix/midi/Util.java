package com.siloenix.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

public class Util {
    private static final String[] notes = {
            "c", "c#",
            "d", "d#",
            "e",
            "f", "f#",
            "g", "g#",
            "a", "a#",
            "b",
    };

    public static int noteToMidi(String note, int octave) {
        for (int i = 0; i < notes.length; i++) {
            if (note.equals(notes[i])) {
                return 12 * (octave + 1) + i;
            }
        }
        return -1;
    }

    public static MidiEvent makeEvent(int command, int channel,
                               int note, int velocity, int tick) {

        MidiEvent event = null;

        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, note, velocity);

            event = new MidiEvent(a, tick);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return event;
    }
}
