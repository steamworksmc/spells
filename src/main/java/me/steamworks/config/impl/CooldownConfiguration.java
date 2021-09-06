package me.steamworks.config.impl;

import me.steamworks.config.Configuration;

import java.io.File;
import java.io.InputStream;

public class CooldownConfiguration extends Configuration {

    private static final String[] header = {
            "############################## #",
            "## The Cooldowns for Spells ## #",
            "############################## #",
            "",
            "Please do not use 0 for cooldowns, instead use -1.",
            "All values are parsed into seconds.",
            "Give the permission node 'spells.no-cooldown' to bypass."
    };

    public CooldownConfiguration(File file) {
        super(file);
    }

    public CooldownConfiguration(File file, InputStream stream) {
        super(file, stream, header);
    }
}
