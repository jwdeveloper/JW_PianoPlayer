package jw.pianoplayer.midi;

public interface MIDI_Event
{

    public void on_event(int note, int velocity, int channel);
}
