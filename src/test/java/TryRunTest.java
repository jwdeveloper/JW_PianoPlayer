import jw.pianoplayer.Main;
import jw.pianoplayer.data.Settings;
import jw.spigot_fluent_api.dependency_injection.InjectionManager;
import jw.spigot_fluent_api.dependency_injection.InjectionType;
import jw.spigot_fluent_api.initialization.FluentPlugin;
import jw.spigot_fluent_api.initialization.FluentPluginConfiguration;
import jw.spigot_fluent_api.utilites.ClassTypeUtility;
import jw.spigot_fluent_api.utilites.ObjectUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TryRunTest
{
   @Test
    public void test()
    {
        Settings main = new Settings();
        var types = ClassTypeUtility.getClassesInPackage(main.getClass().getPackage().getName());
        for(var t :types)
        {
            System.out.println(t.getName()+" type");
        }

    }
}
