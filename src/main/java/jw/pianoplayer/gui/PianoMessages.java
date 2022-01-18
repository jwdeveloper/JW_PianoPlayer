package jw.pianoplayer.gui;

import jw.spigot_fluent_api.fluent_message.MessageBuilder;
import org.bukkit.ChatColor;

public class PianoMessages {
    public static String placePianoError() {
        return new MessageBuilder()
                .space(3)
                .color(ChatColor.RED)
                .bold("! At first create piano !").toString();
    }

    public static String setMidiFileError() {
        return new MessageBuilder()
                .space(8)
                .color(ChatColor.RED)
                .bold("! Select MIDI file !").toString();
    }

    public static String playMidiFileError() {
        return new MessageBuilder()
                .color(ChatColor.RED)
                .bold("This file can not be played :<").toString();
    }

    public static String playingStateStop() {
        return new MessageBuilder()
                .color(ChatColor.BOLD)
                .color(ChatColor.RED)
                .inBrackets("Stop")
                .toString();
    }

    public static String playingStatePlay() {
        return new MessageBuilder()
                .color(ChatColor.BOLD)
                .color(ChatColor.GREEN)
                .inBrackets("Play")
                .toString();
    }

    public static String audioLevel(int level) {
        return new MessageBuilder()
                .field("Level", level)
                .text("%")
                .toString();
    }

    public static String createPianoMessage() {
        return new MessageBuilder()
                .color(ChatColor.BOLD)
                .color(ChatColor.GREEN)
                .inBrackets("Create piano")
                .toString();
    }

    public static String destroyPianoMessage() {
        return new MessageBuilder()
                .color(ChatColor.BOLD)
                .color(ChatColor.RED)
                .inBrackets("Destroy piano")
                .toString();
    }
}
