package jw.pianoplayer;


import jw.InicializerAPI;
import jw.pianoplayer.commands.MainCommand;
import jw.pianoplayer.data.DataManager;
import jw.pianoplayer.events.PianoEventListener;
import jw.pianoplayer.piano.PianoPlayer;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public DataManager dataManager;
    public MainCommand mainCommand;
    public PianoEventListener pianoEventListener;
    public PianoPlayer pianoPlayer;
    @Override
    public void onEnable()
    {
        InicializerAPI.AttachePlugin(this);
        dataManager = new DataManager();
        pianoPlayer = new PianoPlayer();
        mainCommand = new MainCommand();
        pianoEventListener = new PianoEventListener();
        getServer().getPluginManager().registerEvents(pianoEventListener, this);
        dataManager.Load();

        pianoPlayer.Create(dataManager.settings.location);
    }

    @Override
    public void onDisable() {
        dataManager.Save();
    }

}
