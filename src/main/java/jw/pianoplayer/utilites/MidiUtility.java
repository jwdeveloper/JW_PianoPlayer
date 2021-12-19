package jw.pianoplayer.utilites;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import java.util.ArrayList;

public class MidiUtility {

    public static ArrayList<String> getMidiDevicesNames() {
        ArrayList<String> result = new ArrayList<>();
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info : infos) {
            result.add(info.getName());
        }
        return result;
    }
}