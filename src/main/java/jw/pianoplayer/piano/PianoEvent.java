package jw.pianoplayer.piano;

public interface PianoEvent
{
     void OnKeyPress(boolean isDown,int number,int velocity,int channel);
}
