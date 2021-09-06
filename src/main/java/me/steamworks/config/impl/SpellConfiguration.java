package me.steamworks.config.impl;

import me.steamworks.config.Configuration;

import java.io.File;
import java.io.InputStream;

public class SpellConfiguration extends Configuration {

    private static final String[] header = {
            "################################## #",
            "## The Configuration for Spells ## #",
            "################################## #",
            "",
            "When you are configuring your spells, we expect seconds to be parsed. If you wish to use ticks, append 't' to the value.",
            "e.g 600t = 600 ticks = 30 seconds.",
            "while if you don't append 't' it would be 600 seconds which is equal to 5 minutes."
    };

    public SpellConfiguration(File file) {
        super(file);
    }

    public SpellConfiguration(File file, InputStream stream) {
        super(file, stream, header);
    }
}
