package com.bong.autotranscriber;

//import java.io.IOException;
//import java.io.InputStream;
//
////import com.leff.midi.MidiFile;
////import com.leff.midi.event.MidiEvent;
////import com.leff.midi.event.NoteOn;
////import com.leff.midi.event.meta.Tempo;
////import com.leff.midi.util.MidiEventListener;
////import com.leff.midi.util.MidiProcessor;
//
//public class MidiListener implements MidiEventListener
//{
//    private String mLabel;
//
//    public MidiListener(String label)
//    {
//        mLabel = label;
//    }
//
//    // 0. Implement the listener functions that will be called by the
//    // MidiProcessor
//    @Override
//    public void onStart(boolean fromBeginning)
//    {
//        if(fromBeginning)
//        {
//            System.out.println(mLabel + " Started!");
//        }
//        else
//        {
//            System.out.println(mLabel + " resumed");
//        }
//    }
//
//    @Override
//    public void onEvent(MidiEvent event, long ms)
//    {
//        System.out.println(mLabel + " received event: " + event);
//    }
//
//    @Override
//    public void onStop(boolean finished)
//    {
//        if(finished)
//        {
//            System.out.println(mLabel + " Finished!");
//        }
//        else
//        {
//            System.out.println(mLabel + " paused");
//        }
//    }
//
//    public static void listenToFile(InputStream fileInputStream)
//    {
//        // 1. Read in a MidiFile
//        MidiFile midi = null;
//        try
//        {
//            midi = new MidiFile(fileInputStream);
//        }
//        catch(IOException e)
//        {
//            System.err.println(e);
//            return;
//        }
//
//        // 2. Create a MidiProcessor
//        MidiProcessor processor = new MidiProcessor(midi);
//
//        // 3. Register listeners for the events you're interested in
//        MidiListener ep = new MidiListener("Individual Listener");
//        processor.registerEventListener(ep, Tempo.class);
//        processor.registerEventListener(ep, NoteOn.class);
//
//        // or listen for all events:
//        MidiListener ep2 = new MidiListener("Listener For All");
//        processor.registerEventListener(ep2, MidiEvent.class);
//
//        // 4. Start the processor
//        processor.start();
//
//        // Listeners will be triggered in real time with the MIDI events
//        // And you can pause/resume with stop() and start()
//        try
//        {
//            Thread.sleep(10 * 1000);
//            processor.stop();
//
//            Thread.sleep(10 * 1000);
//            processor.start();
//        }
//        catch(Exception e)
//        {
//        }
//    }
//}
