package me.steamworks.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class CC {

    /**
     * Translate string.
     */
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Translate strings.
     */
    public static List<String> color(List<String> strings) {
        return strings
                .stream()
                .map(CC::color)
                .collect(Collectors.toList());
    }

}
