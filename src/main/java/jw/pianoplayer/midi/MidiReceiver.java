package jw.pianoplayer.midi;

import jw.pianoplayer.events.MidiEvent;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;

@SpigotBean
@Getter
@Setter
public class MidiReceiver implements Receiver {
    public MidiEvent onNoteOn = (a, b, c) -> {
    };
    public MidiEvent OnNoteOff = (a, b, c) -> {
    };
    public MidiEvent OnPedalOn = (a, b, c) -> {
    };
    public MidiEvent OnPedalOff = (a, b, c) -> {
    };
    public boolean isPressed;

    @Override
    public void send(MidiMessage midiMessage, long l) {
        if (midiMessage instanceof ShortMessage sm) {
            switch (sm.getCommand()) {
                case ShortMessage.NOTE_OFF:
                    OnNoteOff.execute(sm.getData1(), sm.getData2(), sm.getChannel());
                    break;
                case ShortMessage.NOTE_ON:
                    if (sm.getData2() == 0)
                        OnNoteOff.execute(sm.getData1(), sm.getData2(), sm.getChannel());
                    else
                        onNoteOn.execute(sm.getData1(), sm.getData2(), sm.getChannel());
                    break;
                case ShortMessage.CONTROL_CHANGE:
                case ShortMessage.POLY_PRESSURE:
                    if (sm.getData2() == 0) {
                        isPressed = false;
                        OnPedalOff.execute(sm.getData1(), sm.getData2(), sm.getChannel());
                    } else if (!isPressed && sm.getData2() != 0) {
                        isPressed = true;
                        OnPedalOn.execute(sm.getData1(), sm.getData2(), sm.getChannel());
                    }
                    break;
            }

            //  System.out.println("ShortMessage: "+sm.getStatus()+" "+sm.getCommand()+" "+sm.getChannel()+" "+sm.getData1()+" "+sm.getData2()+" "+l);
            return;
        }
        if (midiMessage instanceof MetaMessage) {
            MetaMessage sm = (MetaMessage) midiMessage;

            //System.out.println("MetaMessage: "+sm.getStatus());
            return;
        }
        if (midiMessage instanceof SysexMessage) {
            SysexMessage sm = (SysexMessage) midiMessage;

            // System.out.println("Systex: "+sm.getStatus());
        }


        //   System.out.println("Transmiter works "+midiMessage.getStatus()+" " + midiMessage.getMessage()[0]+" " +midiMessage.getMessage()[1]+l);
    }

    @Override
    public void close() {

    }

}
