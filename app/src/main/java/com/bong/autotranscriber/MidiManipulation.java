package com.bong.autotranscriber;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;

//import com.leff.midi.MidiFile;
//import com.leff.midi.MidiTrack;
//import com.leff.midi.event.MidiEvent;
//import com.leff.midi.event.NoteOff;
//import com.leff.midi.event.NoteOn;
//import com.leff.midi.event.meta.MetaEvent;
//import com.leff.midi.event.meta.Tempo;

public class MidiManipulation
{
//    public static void manipulate(Context context, FileInputStream midiFileInputAStream) {
//
//        // 1. Open up a MIDI file
//        MidiFile mf = null;
//
//        try {
//            mf = new MidiFile(midiFileInputAStream);
//        } catch (IOException e) {
//            System.err.println("Error parsing MIDI file:");
//            e.printStackTrace();
//            return;
//        }
//
//        // 2. Do some editing to the file
//        // 2a. Strip out anything but notes from track 1
//        MidiTrack T = mf.getTracks().get(1);
//
//        // It's a bad idea to modify a set while iterating, so we'll collect
//        // the events first, then remove them afterwards
//        Iterator<MidiEvent> it = T.getEvents().iterator();
//        ArrayList<MidiEvent> eventsToRemove = new ArrayList<MidiEvent>();
//
//        while (it.hasNext()) {
//            MidiEvent E = it.next();
//
//            if (!E.getClass().equals(NoteOn.class) && !E.getClass().equals(NoteOff.class)) {
//                eventsToRemove.add(E);
//            }
//        }
//
//        for (MidiEvent E : eventsToRemove) {
//            T.removeEvent(E);
//        }
//
//        // 2b. Completely remove track 2
////        mf.removeTrack(2);
////        mf.
////
//        // 2c. Reduce the tempo by half
//        T = mf.getTracks().get(0);
//        TreeSet eventsOne = T.getEvents();
//        it = T.getEvents().iterator();
//        while (it.hasNext()) {
//            MidiEvent E = it.next();
//
//            if (E.getClass().equals(MetaEvent.class)) {
//                MetaEvent event = (MetaEvent) E;
////                event.
//                int noteValue = event.getSize();
////                int noteType = event.();
//
////                event.set
//                Log.v("app", "MetaEvent");
////                channelEvent.setChannel(1);
//            }
//            if (E.getClass().equals(NoteOn.class)) {
//                NoteOn event = (NoteOn) E;
////                event.
//                int noteValue = event.getNoteValue();
//                int noteType = event.getType();
//
////                event.set
//                Log.v("app", "Note value: " + noteValue + " Note type: " + noteType);
////                channelEvent.setChannel(1);
//
//            }
//
//            if (E.getClass().equals(Tempo.class)) {
//
//                Tempo tempo = (Tempo) E;
//                tempo.setBpm(tempo.getBpm() / 2);
//            }
//        }
//
//        // 2c. Reduce the tempo by half
//        T = mf.getTracks().get(1);
//        TreeSet<MidiEvent> eventTwo = T.getEvents();
//        it = T.getEvents().iterator();
//        while (it.hasNext()) {
//            MidiEvent E = it.next();
//
//            if (E.getClass().equals(MetaEvent.class)) {
//                MetaEvent event = (MetaEvent) E;
////                event.
//                int noteValue = event.getSize();
////                int noteType = event.();
////                event.se
////                event.set
//                Log.v("app", "MetaEvent");
////                channelEvent.setChannel(1);
//            }
//            if (E.getClass().equals(NoteOn.class)) {
//                NoteOn event = (NoteOn) E;
////                event.
//                int noteValue = event.getNoteValue();
//                int noteType = event.getType();
////                event.event
////                event.set
//                Log.v("app", "Note value: " + noteValue + " Note type: " + noteType);
////                channelEvent.setChannel(1);
//
//            }
//
//            if (E.getClass().equals(Tempo.class)) {
//
//                Tempo tempo = (Tempo) E;
//                tempo.setBpm(tempo.getBpm() / 2);
//            }
//        }
//
//
//        // 3. Save the file back to disk
//        try {
//            File outputFile = FileHelper.Companion.getEmptyFileInFolder(context, "midi_manipulated", "midi_manip", ".mid");
//            mf.writeToFile(outputFile);
//        } catch (IOException e) {
//            System.err.println("Error writing MIDI file:");
//            e.printStackTrace();
//        }
//    }
}
