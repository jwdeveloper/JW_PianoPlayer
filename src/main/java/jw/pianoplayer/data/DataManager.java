package jw.pianoplayer.data;

import jw.data.repositories.RepositoryManager;

public class DataManager extends RepositoryManager
{
    public Settings settings;

    public DataManager()
    {
          settings = new Settings();
          this.AddObject(settings);
    }
}
