package jw.pianoplayer.midi;

import javax.sound.midi.*;
import java.util.function.Consumer;

public class MIDI_Piano
{


       private Synthesizer synthesizer;
       private  MidiChannel channel;
       private MidiDevice midiDevice;
       private MIDI_Reciever reciever;
       private MIDI_Reciever sound_reciever;
       private String name;
       public Consumer<String> OnError;


        public MIDI_Piano(String name,MIDI_Reciever reciever)
        {
            this.reciever = reciever;
            this.name = name;
        }

        public void Set_Sound(int i)
        {
         channel.programChange(i);
        }
        public void Play_sound(int node,int velocity)
        {
         channel.noteOn(node, velocity+30);
        }

        public Instrument[] Get_Instruments()
        {
          return  synthesizer.getAvailableInstruments();
        }
        private boolean Load_Piano()
        {

                for(MidiDevice.Info i :MidiSystem.getMidiDeviceInfo())
                {
                    if(i.getName().contains(name)  && !i.getDescription().contains("MIDI Port") )
                    {
                        try {
                            midiDevice = MidiSystem.getMidiDevice(i);
                            midiDevice.getTransmitter().setReceiver(reciever);
                            sound_reciever = new MIDI_Reciever("Dsd");
                             midiDevice.getTransmitter().setReceiver(sound_reciever);
                            return  true;
                            }
                          catch (MidiUnavailableException e)
                            {
                                OnError.accept(e.getMessage());
                            }
                    }
                }
                return  false;
            }



            int i=1;
        public boolean Open()
        {
            try
            {
               if(Load_Piano() == false)
               {
                   return false;
               }


                 synthesizer = MidiSystem.getSynthesizer();
                 channel = synthesizer.getChannels()[0];
                 channel.setMono(true);
                 midiDevice.open();
                 synthesizer.open();

                sound_reciever.OnNoteOn = (a,b,c)->
                {
                    channel.noteOn(a, b+30);
                };

                sound_reciever.OnNoteOff = (a,b,c)->
                {
                    if(reciever.is_pressed ==false)
                    channel.noteOff(a, b+30);
                };

                sound_reciever.OnPedalOff = (a,b,c)->
                {
                    channel.allNotesOff();
                };
                sound_reciever.OnPedalOn = (a,b,c)->
                {
                   int i =0;
                };

                return true;
            }
            catch (MidiUnavailableException ex)
            {
                OnError.accept(ex.getMessage());
            }
            return false;
        }

        public void Close()
        {
            if(sound_reciever != null)
                sound_reciever.close();
            if(midiDevice != null &&  midiDevice.isOpen())
            this.midiDevice.close();
            if(synthesizer!= null && synthesizer.isOpen())
            synthesizer.close();

        }





}
