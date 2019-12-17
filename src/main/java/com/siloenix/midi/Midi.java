package com.siloenix.midi;

import javax.sound.midi.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Midi {
    private Sequencer sequencer;

    public static Midi fromFile(String filename) throws Exception {
        List<String> notes = Files.readAllLines(new File(filename).toPath());
        notes.removeIf(String::isEmpty);
        Midi midi = new Midi();
        midi.setMelody(new Melody(
                notes.stream()
                        .map(Note::parse)
                        .collect(Collectors.toList())
        ));
        return midi;
    }

    public void setMelody(Melody melody) throws Exception {
        sequencer = MidiSystem.getSequencer();
        sequencer.open();

        sequencer.setSequence(melody.toSequence());

        // Specifies the beat rate in beats per minute.
        sequencer.setTempoInBPM(70);
    }

    public void play() throws Exception {
        sequencer.start();
        while (sequencer.isRunning()) {
            Thread.sleep(500);
            System.out.println("wait 500");
        }
        Thread.sleep(1000);
        sequencer.close();
//        System.exit(0);
    }
}
