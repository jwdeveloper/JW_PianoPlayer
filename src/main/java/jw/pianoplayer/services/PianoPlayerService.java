package jw.pianoplayer.services;
import jw.pianoplayer.piano.PianoKey;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.initialization.FluentPlugin;
import org.bukkit.Location;
import java.util.HashMap;

@SpigotBean(lazyLoad = false)
public class PianoPlayerService
{
    private final SettingsService settingsService;
    private final MidiPlayerService midiPlayerService;
    private HashMap<Integer, PianoKey> pianoKeys;

    public PianoPlayerService(SettingsService settings, MidiPlayerService midiPlayerService)
    {

        this.midiPlayerService = midiPlayerService;
        this.settingsService = settings;
        this.pianoKeys = new HashMap<>();

        midiPlayerService.setOnStart((o)->
        {
            this.settingsService.getIsPlayingBind().setAsync(true);
        });
        midiPlayerService.setOnStop((o)->
        {
            this.settingsService.getIsPlayingBind().setAsync(false);
        });
        midiPlayerService.setOnNotePressed((note, velocity, channel) ->
        {
            if (settingsService.getIsPianoPlacedBind().get())
            {
                pianoKeys.get(note - settingsService.getStartNoteIndex()).onKeyPress(velocity != 0, note, velocity, channel);
            }
        });
        midiPlayerService.setOnNoteRelsesed((note, velocity, channel) ->
        {
            if (settingsService.getIsPianoPlacedBind().get())
            {
                pianoKeys.get(note - settingsService.getStartNoteIndex()).onKeyPress(false, note, velocity, channel);
            }
        });

        midiPlayerService.onStopPlaying(o ->
        {
            this.settingsService.getIsPlayingBind().setAsync(false);
        });
    }

    public void createPiano(Location location) {
        if (location == null)
            return;

        destroyPiano();
        var key = 1;
        var isBlack = false;
        for (int i = 1; i <= 88; i++) {
            if (i > 3 && i < 88) {
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

    public void destroyPiano()
    {
        stop();
        pianoKeys.forEach((a, b) ->
        {
            b.removeKey();
        });
        pianoKeys.clear();
    }

    public void refreshKeys()
    {
        pianoKeys.forEach((a, b) ->
        {
            b.reset();
        });
    }


    public boolean play(String midiFilePath)
    {
        try {
            if (!settingsService.getIsPianoPlacedBind().get())
                return false;

            if (midiPlayerService.isPlaying())
            {
                stop();
            }
            midiPlayerService.loadFile(midiFilePath);
            midiPlayerService.start();
            settingsService.getIsPlayingBind().setAsync(true);
            return true;
        }
        catch (Exception e)
        {
            FluentPlugin.logError("File from path "+midiFilePath+" can not by Piano player :<");
            return false;
        }
    }


    public void stop()
    {
        pianoKeys.forEach((a, b) ->
        {
            b.reset();
        });
        midiPlayerService.stop();
        this.settingsService.getIsPlayingBind().setAsync(false);
    }
}
