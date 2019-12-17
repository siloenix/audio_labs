package com.siloenix.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.List;

import static com.siloenix.midi.Util.makeEvent;
import static com.siloenix.midi.Util.noteToMidi;

public class Melody {
    public static final int START = 144;
    public static final int STOP = 128;
    public static final int STATIC_CHANNEL = 1;
    public static final int STATIC_VELOCITY = 100;

    private List<Note> notes;

    public Melody(List<Note> notes) {
        this.notes = notes;
    }

    public Sequence toSequence() throws Exception {
        int tick = 4;
        Sequence sequence = new Sequence(Sequence.PPQ, 4);

        Track track = sequence.createTrack();
        for (Note note : notes) {
            track.add(
                    makeEvent(
                            START,
                            STATIC_CHANNEL,
                            noteToMidi(note.name, note.octave),
                            STATIC_VELOCITY,
                            tick
                    )
            );
            tick += note.duration;
            track.add(
                    makeEvent(
                            STOP,
                            STATIC_CHANNEL,
                            noteToMidi(note.name, note.octave),
                            STATIC_VELOCITY,
                            tick
                    )
            );
        }
        return sequence;
    }
}
