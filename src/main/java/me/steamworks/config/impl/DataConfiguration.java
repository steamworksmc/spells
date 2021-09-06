package me.steamworks.config.impl;

import me.steamworks.config.Configuration;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class that manges the {@code data.yml} file. <br>
 * The file is used to store all player data.
 */
public class DataConfiguration extends Configuration {


    public DataConfiguration(File file, InputStream stream) {
        super(file, stream, null);
    }

    /**
     * A utility that is meant to be used instead of {@link org.bukkit.configuration.file.FileConfiguration#getStringList(String)}. <br>
     * This is needed because the Bukkit counterpart returns {@code null} instead of returning an empty list.
     *
     * @param s The path to the string list.
     * @return The string list at the location or any empty {@link ArrayList}
     */
    public List<String> getStringListOrEmpty(String s) {
        return get().getStringList(s) == null ? Collections.emptyList() : get().getStringList(s);
    }
}
