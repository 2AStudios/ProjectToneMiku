import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import javax.sound.midi.*;

public class MidiPlayer {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public HashMap<Main.Tuple<Integer,Integer>, Main.Tuple<Integer,Long>> noteMap = new HashMap<>(); //Row, Col, Note, Time

    MainWindow main;
    Sequencer sequencer;
    int mainTrackIndex;

    private static final boolean debug = false;

    public MidiPlayer(String file, MainWindow main) {
        this.main = main;
        try {
            // Obtains the default Sequencer connected to a default device.
            sequencer = MidiSystem.getSequencer();

            // Opens the device, indicating that it should now acquire any
            // system resources it requires and become operational.
            sequencer.open();

            // create a stream from a file
            InputStream is = new BufferedInputStream(new FileInputStream(new File(file)));

            // Sets the current sequence on which the sequencer operates.
            // The stream must point to MIDI file data.
            sequencer.setSequence(is);
            //sequencer.start();

            MidiReader(file);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void playTrack(long startPoint, float tempo){
        sequencer.setMicrosecondPosition(startPoint);
        sequencer.setTempoFactor(tempo);
        sequencer.start();
    }

    public void pauseTrack(){
        if(sequencer.isRunning()){
            main.trackLoc = sequencer.getMicrosecondPosition();
            sequencer.stop();
        }
    }

    public void addNote(int note, long notePosition){
        ShortMessage newSM = null;
        try {
            sequencer.recordEnable(sequencer.getSequence().getTracks()[mainTrackIndex],0);
            newSM = new ShortMessage(NOTE_ON,0,note,127);
            MidiEvent me = new MidiEvent(newSM,notePosition);
            sequencer.getSequence().getTracks()[mainTrackIndex].add(me);
            newSM = new ShortMessage(NOTE_OFF,0,note,127);
            me = new MidiEvent(newSM,notePosition+main.tempo);
            sequencer.getSequence().getTracks()[mainTrackIndex].add(me);
            sequencer.recordDisable(sequencer.getSequence().getTracks()[mainTrackIndex]);

            if(debug) {
                for (int i = 0; i < sequencer.getSequence().getTracks()[mainTrackIndex].size(); i++) {
                    MidiEvent event = sequencer.getSequence().getTracks()[mainTrackIndex].get(i);
                    System.out.print("@" + event.getTick() + " ");
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        System.out.print("Channel: " + sm.getChannel() + " ");
                        if (sm.getCommand() == NOTE_ON) {
                            int key = sm.getData1();
                            int octave = (key / 12) - 1;
                            int note2 = key % 12;
                            String noteName = NOTE_NAMES[note2];
                            int velocity = sm.getData2();
                            //System.out.println(sm.getChannel());
                            System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        } else if (sm.getCommand() == NOTE_OFF) {
                            int key = sm.getData1();
                            int octave = (key / 12) - 1;
                            int note2 = key % 12;
                            String noteName = NOTE_NAMES[note2];
                            int velocity = sm.getData2();
                            System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        } else {
                            System.out.println("Command:" + sm.getCommand());
                        }
                    } else {
                        System.out.println("Other message: " + message.getClass());
                    }
                }
            }

            if(debug)System.out.println("@" + me.getTick() + " key: " + newSM.getData1() + " velocity: " + newSM.getData2());
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void MidiReader(String file) throws Exception {
        Sequence sequence = MidiSystem.getSequence(new File(file));

        long longestTime = 0;
        Set<Long> timeRate = new HashSet<>();
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                timeRate.add(event.getTick());
                if(longestTime < event.getTick())
                    longestTime = event.getTick();
            }
        }
        long soundInterval = findGCD((Long[])timeRate.toArray(new Long[timeRate.size()]),timeRate.size());
        System.out.println("Audio Interval: "  + soundInterval);

        int trackNumber = 0;
        Track mainTrack = null;
        for (Track track : sequence.getTracks()) {
            trackNumber++;
            if(debug)System.out.println("Track " + trackNumber + ": size = " + track.size());

            if(mainTrack == null){
                mainTrack = track;
            }else{
                if(mainTrack.size() < track.size()){
                    mainTrack = track;
                    mainTrackIndex = trackNumber-1;
                }
            }

            System.out.println();
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                if(debug) System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        //System.out.println(sm.getChannel());
                        if(debug)System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        noteMap.put(new Main().new Tuple<>(key-MainWindow.defaultScale[0],(int)(event.getTick()/soundInterval)+1),new Main().new Tuple<>(key,event.getTick()));
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        if(debug)System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else {
                        if(debug)System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                    if(debug)System.out.println("Other message: " + message.getClass());
                }
            }
            System.out.println();
        }
        main.tempo = soundInterval;
        main.updateGridPanel(noteMap,(int)(longestTime/soundInterval));
        /*Iterator it = noteMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Main.Tuple<Integer,Integer> key = (Main.Tuple<Integer,Integer>) pair.getKey();
            Main.Tuple<Integer,Long> value = (Main.Tuple<Integer, Long>) pair.getValue();
            System.out.println(key.x + " | " + key.y +  " = " + value.x + " | " + value.y);
            //it.remove(); // avoids a ConcurrentModificationException
        }*/
    }

    static long gcd(long a, long b)
    {
        if (a == 0)
            return b;
        return gcd(b%a, a);
    }

    static long findGCD(Long arr[], long n)
    {
        long result = arr[0];
        for (int i=1; i<n; i++)
            result = gcd(arr[i], result);

        return result;
    }
}