package jw.pianoplayer.events;

public interface PianoEvent
{
     void onKeyPress(boolean isDown, int number, int velocity, int channel);
}
