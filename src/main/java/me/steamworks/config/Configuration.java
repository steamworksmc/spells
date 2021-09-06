package me.steamworks.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Configuration {

    private FileConfiguration fConfiguration = null;
    private File file = null;

    public Configuration(File file) {
        this(file, null, null);
    }

    public Configuration(File file, InputStream stream, String[] header) {
        this.file = file;
        this.fConfiguration = YamlConfiguration.loadConfiguration(file);

        if (stream != null) {
            YamlConfiguration dft = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            fConfiguration.setDefaults(dft);
            fConfiguration.options().copyDefaults(true);
        }

        if (header != null) {
            StringBuilder bob = new StringBuilder();
            for (String s : header)
                bob.append(s).append("\n");
            fConfiguration.options().header(bob.toString());
            fConfiguration.options().copyHeader(true);
        }
        save();
    }

    /**
     * Gets the custom config.
     *
     * @return The {@link FileConfiguration} represented by this class.
     */
    public FileConfiguration get() {
        return fConfiguration;
    }

    /**
     * Saves this config to the disk.
     */
    public void save() {
        try {
            this.get().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
