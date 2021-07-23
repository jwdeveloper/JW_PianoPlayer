package jw.pianoplayer.piano;

import jw.gui.button.Button;
import jw.pianoplayer.Main;
import jw.pianoplayer.data.Settings;
import jw.pianoplayer.midi.MIDI_Helper;
import jw.pianoplayer.midi.MIDI_Player;
import jw.pianoplayer.midi.MIDI_Reciever;
import jw.task.TaskTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.function.Consumer;

public class PianoPlayer {

    private Settings settings;
    private MIDI_Player midiPlayer;
    private HashMap<Integer, PianoKey> pianoKeys;
    private MIDI_Reciever midiReciever;

    public PianoPlayer() {

        this.midiReciever = MIDI_Helper.getTestReciever();
        this.settings = Main.getPlugin(Main.class).dataManager.settings;
        this.midiPlayer = new MIDI_Player(midiReciever);
        this.pianoKeys = new HashMap<>();

        this.midiPlayer.OnStart = o ->
        {
            this.settings.isPlayingBind.setAsync(true);
        };
        this.midiPlayer.OnStop = o ->
        {
            this.settings.isPlayingBind.setAsync(false);
        };

    }

    private void MIDIEventHandler() {
        midiReciever.OnNoteOn = (number, velocity, channel) ->
        {
            if (settings.isPianoPlaced) {
                pianoKeys.get(number - 21).OnKeyPress(velocity != 0, number, velocity, channel);
            }

        };
        midiReciever.OnNoteOff = (a, b, c) ->
        {
            if (settings.isPianoPlaced) {
                pianoKeys.get(a - 21).OnKeyPress(false, a, b, c);

            }
        };


        midiReciever.OnPedalOff = (note, velocity, channel) ->
        {

        };

        midiReciever.OnPedalOn = (note, velocity, channel) ->
        {

        };
    }

    public void Create(Location location) {

        if (location == null)
            return;

        Destroy();
        int key = 1;
        boolean isBlack = false;
        for (int i = 1; i <= 88; i++) {
            if (i > 3 && i < 88) {
                key = (i - 4) % 12;
            }
            if (i <= 3) {
                key = i + 8;
            }
            switch (key) {
                case 1:
                case 3:
                case 6:
                case 8:
                case 10:
                    isBlack = true;
                    break;
                default:
                    isBlack = false;
                    break;
            }
            Location location1 = location.clone().add(0, 0, i);
            pianoKeys.put(i, new PianoKey(settings, location1, isBlack));
        }
        MIDIEventHandler();
        settings.location = location;
    }

    public void Destroy() {
            Stop();
            pianoKeys.forEach((a, b) ->
            {
                b.RemoveKey();
            });
            pianoKeys.clear();
    }

    private void ResetKeys() {
        pianoKeys.forEach((a, b) ->
        {
            b.Reset();
        });
    }

    public void OnPlay(Consumer<String> consumer) {
        this.midiPlayer.OnStart = consumer;
    }

    public void OnStop(Consumer<String> consumer) {
        this.midiPlayer.OnStop = consumer;
    }

    public void OnError(Consumer<String> consumer) {
        this.midiPlayer.OnError = consumer;
    }

    public void Play(String path) {
        if (!settings.isPianoPlaced)
            return;

        if (midiPlayer.Is_Playing()) {
            Stop();
        }
        Bukkit.getConsoleSender().sendMessage(path);
        midiPlayer.loadFile(path);
        midiPlayer.Start();
        this.settings.isPlayingBind.setAsync(true);
    }

    public void Stop() {
        ResetKeys();
        midiPlayer.Stop();
        this.settings.isPlayingBind.setAsync(false);
    }
}
