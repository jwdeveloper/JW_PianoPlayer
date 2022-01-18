package jw.pianoplayer.services;

import jw.pianoplayer.midi.MidiPlayerDrivers;
import jw.pianoplayer.piano.PianoKey;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.desing_patterns.dependecy_injection.annotations.Inject;
import jw.spigot_fluent_api.desing_patterns.dependecy_injection.annotations.Injection;
import jw.spigot_fluent_api.fluent_plugin.FluentPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.io.File;
import java.util.HashMap;

@Injection(lazyLoad = false)
public class PianoPlayerService {
    private final int PIANO_KEYS = 88;
    private final SettingsService settingsService;
    private final MidiPlayerDrivers midiPlayerDrivers;
    private HashMap<Integer, PianoKey> pianoKeys;
    private BossBar bossBar;

    @Inject
    public PianoPlayerService(SettingsService settings, MidiPlayerDrivers midiPlayerDrivers) {

        this.midiPlayerDrivers = midiPlayerDrivers;
        this.settingsService = settings;
        this.pianoKeys = new HashMap<>();


        settings.getIsInfoBarBind().onChange(value ->
        {
            if (value)
                createInfoBar();
            else
                removeInfoBar();
        });

        midiPlayerDrivers.setOnStart((o) ->
        {
            this.settingsService.getIsPlayingBind().setAsync(true);
            if (settings.getIsInfoBarBind().get()) {
                createInfoBar();
            }
        });
        midiPlayerDrivers.setOnStop((o) ->
        {
            removeInfoBar();
            this.settingsService.getIsPlayingBind().setAsync(false);
        });
        midiPlayerDrivers.onStopPlaying(o ->
        {
            removeInfoBar();
            this.settingsService.getIsPlayingBind().setAsync(false);
        });
        midiPlayerDrivers.setOnNotePressed((note, velocity, channel) ->
        {
            if (!settingsService.getIsPianoPlacedBind().get())
                return;

            updateInfoBar();
            pianoKeys.get(note - settingsService.getStartNoteIndex()).onKeyPress(velocity != 0, note, velocity, channel);
        });
        midiPlayerDrivers.setOnNoteReleased((note, velocity, channel) ->
        {
            if (!settingsService.getIsPianoPlacedBind().get())
                return;
                updateInfoBar();
                pianoKeys.get(note - settingsService.getStartNoteIndex()).onKeyPress(false, note, velocity, channel);
        });
    }

    private void createInfoBar() {
        bossBar = Bukkit.createBossBar("MIDI File", BarColor.GREEN, BarStyle.SOLID);
        var title = midiPlayerDrivers.getFilePath();
        title = title.substring(title.lastIndexOf(File.separator) + 1);
        bossBar.setTitle(ChatColor.GREEN + title);
        for (var player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }
        bossBar.setProgress(0);
    }

    private void updateInfoBar() {
        if (bossBar == null || !midiPlayerDrivers.isPlaying())
            return;
        bossBar.setProgress(midiPlayerDrivers.getCurrentMS() / midiPlayerDrivers.getMS());
        if (bossBar.getProgress() > 0.9f)
            removeInfoBar();
    }

    private void removeInfoBar() {
        if (bossBar != null)
            bossBar.removeAll();

        bossBar = null;
    }

    public void createPiano(Location location) {
        if (location == null)
            return;

        destroyPiano();
        var key = 1;
        var isBlack = false;
        for (int i = 1; i <= PIANO_KEYS; i++) {
            if (i > 3 && i < PIANO_KEYS) {
                key = (i - 4) % 12;
            }
            if (i <= 3) {
                key = i + 8;
            }
            isBlack = switch (key) {
                case 1, 3, 6, 8, 10 -> true;
                default -> false;
            };
            Location location1 = location.clone().add(0, 0, i);
            pianoKeys.put(i, new PianoKey(settingsService, location1, isBlack));
        }
        settingsService.getLocationBind().set(location);
    }

    public void destroyPiano() {
        stop();
        pianoKeys.forEach((a, b) ->
        {
            b.removeKey();
        });
        pianoKeys.clear();
    }

    public void refreshKeys() {
        pianoKeys.forEach((a, b) ->
        {
            b.reset();
        });
    }


    public boolean play(String midiFilePath) {
        try {
            if (!settingsService.getIsPianoPlacedBind().get())
                return false;

            if (midiPlayerDrivers.isPlaying()) {
                stop();
            }
            midiPlayerDrivers.loadFile(midiFilePath);
            midiPlayerDrivers.start();
            settingsService.getIsPlayingBind().setAsync(true);
            return true;
        } catch (Exception e) {
            FluentPlugin.logException("File from path " + midiFilePath + " can not by Piano player :<",e);
            return false;
        }
    }


    public void stop() {
        pianoKeys.forEach((a, b) ->
        {
            b.reset();
        });
        midiPlayerDrivers.stop();
        this.settingsService.getIsPlayingBind().setAsync(false);
    }
}
