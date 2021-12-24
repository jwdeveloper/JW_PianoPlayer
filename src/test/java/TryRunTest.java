import jw.pianoplayer.data.Settings;
import jw.spigot_fluent_api.utilites.ClassTypeUtility;
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
        var types = ClassTypeUtility.fineClassesInPackage(main.getClass().getPackage().getName());
        for(var t :types)
        {
            System.out.println(t.getName()+" type");
        }

    }
}
