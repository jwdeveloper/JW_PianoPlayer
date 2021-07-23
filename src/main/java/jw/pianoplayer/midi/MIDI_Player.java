package jw.pianoplayer.midi;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class MIDI_Player
{

    public String file_path;

    public Consumer OnStart;
    public Consumer OnStop;
    public Consumer<String> OnError;

    private Sequence sequence;
    private Sequencer sequencer;
    private  Synthesizer synthesizer;
    private boolean loaded =false;
    private float current_time =0;
    private Track[] tracks;
    private MIDI_Reciever midi_reciever;


    public MIDI_Player(MIDI_Reciever midi_reciever)
    {
        this.midi_reciever = midi_reciever;

        OnStart = (a)->
        {
          System.out.println("Piano Start Playing");
        };
        OnStop = (a)->
        {
            System.out.println("Piano Stop Playing");
        };


    }



    public boolean Is_Playing()
    {
          if(sequencer==null) return false; else return sequencer.isRunning() ;
    }
    public float GetTime()
    {
      return sequencer.getMicrosecondPosition();
    }
    public void Set_Time(float time)
    {
        current_time = time;
    }
    public String Get_Name()
    {
        return "MIDIFILE";
    }

    public void SetVolume(int volume)
    {
        try
        {
            MidiChannel[] channels = synthesizer.getChannels();
            for( int c = 0; c < channels.length; c++ ) {
                if(channels[c] != null)
                {
                    channels[c].controlChange( 7, (int)( volume*127) );
                    channels[c].controlChange( 39, (int)( volume*127) );
                    channels[c].setMute(true);
                }

            }
            sequencer.setSequence(sequence);
        }
        catch (InvalidMidiDataException ignored)
        {

        }
    }

    public void Start()
    {
        if(Is_Playing()==false)
        {
            sequencer.start();
            this.OnStart.accept("Start");
        }

    }
    public void Pause()
    {
        if(Is_Playing())
        {
          sequencer.stop();
        }
    }


    public void Stop()
    {
      if(sequencer!= null)
      {
          if(sequencer.isOpen() || sequencer.isRunning())
              sequencer.stop();

          sequencer.close();
          this.OnStart.accept("Stop");
      }

    }

    public void loadFile(String file_path)
    {
        try
        {
            this.file_path = file_path;

              File path = new File( this.file_path);
              sequence = MidiSystem.getSequence(path);
              sequencer = MidiSystem.getSequencer(true);
              synthesizer = MidiSystem.getSynthesizer();

            sequencer.open();
            synthesizer.open();

            sequencer.getTransmitter().setReceiver(this.midi_reciever);
            sequencer.setSequence(sequence);
            loaded=true;
        }
        catch (InvalidMidiDataException | IOException | MidiUnavailableException e1)
        {
            this.OnError.accept(e1.getMessage());
        }
    }

    private void addNotesToTrack(Track track, Track trk,int track_number) throws InvalidMidiDataException
    {

        MidiEvent me = null;
        MidiMessage mm = null;
        for (int ii = 0; ii < track.size(); ii++) {
             me = track.get(ii);
             mm = me.getMessage();


            byte[] b = mm.getMessage();
            String m = " ";
            for(int i=0;i<b.length;i++)
            {
                m+=b[i]+" ";
            }
     //       System.out.print("BYTE "+m+" :");
            if (mm instanceof ShortMessage)
            {
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
                    b[0] = (byte)track_number;
               //     System.out.println("Track "+ (byte)track_number );
                    MetaMessage metaMessage = new MetaMessage(com, b, (b == null ? 0 : b.length));
                    MidiEvent me2 = new MidiEvent(metaMessage, me.getTick());
                    trk.add(me2);
                }

            }
        }
    }

}
