package jw.pianoplayer.midi;

import jw.pianoplayer.events.MidiEvent;
import jw.pianoplayer.midi.MidiReceiver;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.fluent_plugin.FluentPlugin;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@SpigotBean
@Getter
@Setter
public class MidiPlayerDrivers {
    private final MidiReceiver midiReceiver;
    private Sequence sequence;
    private Sequencer sequencer;
    private Synthesizer synthesizer;
    private boolean loaded = false;
    private Track[] tracks;
    private String filePath;

    private Consumer<?> OnStart;
    private Consumer<?> OnStop;

    public MidiPlayerDrivers(MidiReceiver midiReceiver)
    {
        this.midiReceiver = midiReceiver;
    }


    public boolean isPlaying() {
        if (sequencer == null) return false;
         return sequencer.isRunning();
    }
    public void onStopPlaying(Consumer<?> event)
    {
        this.midiReceiver.setOnStop(event);
    }

    public float getCurrentMS()
    {
       return sequencer.getMicrosecondPosition();
    }


    public float getMS()
    {
        return  sequencer.getMicrosecondLength();
    }

    public void setOnNotePressed(MidiEvent event)
    {
        this.midiReceiver.setOnNoteOn(event);
    }
    public void setOnNoteReleased(MidiEvent event)
    {
        this.midiReceiver.setOnNoteOff(event);
    }

    public void setOnPedalPressed(MidiEvent event)
    {
        this.midiReceiver.setOnPedalOn(event);
    }
    public void setOnPedalRelsesed(MidiEvent event)
    {
        this.midiReceiver.setOnPedalOff(event);
    }


    public void start() {

        if(isPlaying())
            return;
        getSequencer().start();
        if(getOnStart() != null)
        getOnStart().accept(null);
    }

    public void pause() {
        if (!isPlaying())
            return;

        if(getOnStop() != null)
        getSequencer().stop();
    }

    public void stop() {
        if (getSequencer() == null)
            return;

        if (sequencer.isOpen() || sequencer.isRunning())
            sequencer.stop();

        sequencer.close();
        getOnStop().accept(null);
    }

    public void loadFile(String path) {
        try
        {
            this.filePath = path;
            var file = new File(this.filePath);
            sequence = MidiSystem.getSequence(file);
            sequencer = MidiSystem.getSequencer(true);
            synthesizer = MidiSystem.getSynthesizer();
            sequencer.open();
            synthesizer.open();
            sequencer.getTransmitter().setReceiver(this.midiReceiver);
            sequencer.setSequence(sequence);
            loaded = true;
        } catch (InvalidMidiDataException | IOException | MidiUnavailableException exception)
        {
            FluentPlugin.logException("Error while loading midi file",exception);
        }
    }
    public void setVolume(int volume) {
        try {
            var channels = synthesizer.getChannels();
            for (MidiChannel channel : channels) {
                if (channel == null)
                    continue;

                channel.controlChange(7, (int) (volume * 127));
                channel.controlChange(39, (int) (volume * 127));
                channel.setMute(true);
            }
            sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException exception) {
            FluentPlugin.logError(exception.getMessage());
        }
    }

    private void addNotesToTrack(Track track, Track trk, int track_number) throws InvalidMidiDataException {

        javax.sound.midi.MidiEvent me = null;
        MidiMessage mm = null;
        for (int ii = 0; ii < track.size(); ii++) {
            me = track.get(ii);
            mm = me.getMessage();


            byte[] b = mm.getMessage();
            StringBuilder m = new StringBuilder(" ");
            for (byte value : b) {
                m.append(value).append(" ");
            }
            //       System.out.print("BYTE "+m+" :");
            if (mm instanceof ShortMessage) {
                ShortMessage sm = (ShortMessage) mm;
                int command = sm.getCommand();
                int com = -1;
                if (command == ShortMessage.NOTE_ON) {
                    com = 1;
                } else if (command == ShortMessage.NOTE_OFF) {
                    com = 2;
                }
                if (com > 0) {

                    b = sm.getMessage();
                    b[0] = (byte) track_number;
                    //     System.out.println("Track "+ (byte)track_number );
                    MetaMessage metaMessage = new MetaMessage(com, b, (b == null ? 0 : b.length));
                    javax.sound.midi.MidiEvent me2 = new javax.sound.midi.MidiEvent(metaMessage, me.getTick());
                    trk.add(me2);
                }

            }
        }
    }
}
