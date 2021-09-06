package me.steamworks.config;

import me.steamworks.SpellPlugin;
import me.steamworks.config.impl.CooldownConfiguration;
import me.steamworks.config.impl.DataConfiguration;
import me.steamworks.config.impl.SpellConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigurationManager {

    private static final String[] header = {
            "################################ #",
            "## Spells                     ## #",
            "## Created by Steamworks#1127 ## #",
            "################################ #",
            "\n",
    };

    private final SpellPlugin instance = SpellPlugin.getInstance();
    private final Map<ConfigurationType, Configuration> configurations = new HashMap<>();

    public ConfigurationManager() {
        reload();
    }

    /**
     * Get a {@link Configuration} that represents the given {@link ConfigurationType} of the configuration.
     *
     * @param type The type of the configuration that you wish to get.
     * @return The configuration file.
     */
    public Configuration get(ConfigurationType type) {
        return configurations.get(type);
    }

    /**
     * Reloads all configs.
     */
    public void reload() {
        configurations.put(ConfigurationType.PLAYER_DATA, new DataConfiguration(new File(instance.getDataFolder(), "data.yml"), instance.getResource("data.yml")));
        configurations.put(ConfigurationType.SPELLS, new SpellConfiguration(new File(instance.getDataFolder(), "spells.yml"), instance.getResource("spells.yml")));
        configurations.put(ConfigurationType.COOLDOWNS, new CooldownConfiguration(new File(instance.getDataFolder(), "cooldowns.yml"), instance.getResource("cooldowns.yml")));
    }

    /**
     * Loads the default configuration.
     */
    public void load() {
        File file = new File(instance.getDataFolder(), "config.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        FileConfiguration resource = instance.getConfig();

        if (!file.exists())
            instance.saveDefaultConfig();
        else {
            if (header != null) {
                StringBuilder bob = new StringBuilder();
                for (int i = 0; i < header.length; i++) {
                    bob.append(header[i]);
                    if (i + 2 < header.length)
                        bob.append("\n");
                }
                configuration.options().header(bob.toString());
            }
            configuration.setDefaults(Objects.requireNonNull(resource.getDefaults()));
            configuration.options().copyDefaults(true);
            configuration.options().copyHeader(true);

            try {
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * An enum representing all types of {@link Configuration} that exist.
     */
    public enum ConfigurationType {
        PLAYER_DATA,
        SPELLS,
        COOLDOWNS
    }

}
