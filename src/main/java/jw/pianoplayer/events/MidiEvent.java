package jw.pianoplayer.events;

public interface MidiEvent
{
     void execute(int note, int velocity, int channel);
}
