package me.steamworks.spells;

import lombok.RequiredArgsConstructor;
import me.steamworks.SpellPlugin;
import me.steamworks.config.ConfigurationManager;
import me.steamworks.config.impl.DataConfiguration;
import me.steamworks.utils.Locale;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import javax.annotation.Nullable;
import java.util.List;

@RequiredArgsConstructor
public abstract class Spell {

    public final SpellPlugin INSTANCE = SpellPlugin.getInstance();
    private final ConfigurationManager cfgManager = INSTANCE.getCfgManager();

    /**
     * Teaches a spell to a target.
     */
    public void teach(Player sender, Player target) {
        if (target != null) {
            if (playerKnows(target))
                sender.sendMessage(Locale.PLAYER_KNOWS.format());
            else {
                teach(target);
                sender.sendMessage(Locale.PLAYER_TAUGHT.format(target.getName(), getName()));
            }
        } else sender.sendMessage(Locale.PLAYER_NOT_FOUND.format());
    }

    /**
     * Teach the player a spell.
     */
    public void teach(Player player) {
        DataConfiguration cfg = (DataConfiguration) cfgManager.get(ConfigurationManager.ConfigurationType.PLAYER_DATA);
        List<String> spells = cfg.getStringListOrEmpty(player.getUniqueId().toString());
        spells.add(getName());
        cfg.get().set(player.getUniqueId().toString(), spells);
        cfg.save();
    }

    /**
     * Make a player forget a spell.
     */
    public void unteach(Player player) {
        DataConfiguration cfg = (DataConfiguration) cfgManager.get(ConfigurationManager.ConfigurationType.PLAYER_DATA);
        List<String> spells = cfg.getStringListOrEmpty(player.getUniqueId().toString());
        spells.remove(getName());
        cfg.get().set(player.getUniqueId().toString(), spells);
        cfg.save();
    }

    /**
     * Checks if a player knows this spell.
     *
     * @return {@code true} IF the player knows the spell.
     */
    public boolean playerKnows(Player player) {
        return (((DataConfiguration) cfgManager.get(ConfigurationManager.ConfigurationType.PLAYER_DATA)).getStringListOrEmpty(player.getUniqueId().toString())).contains(getName());
    }

    /**
     * Get the spell information for this spell.
     */
    public SpellInfo getInfo() {
        return this.getClass().getAnnotation(SpellInfo.class);
    }

    /**
     * Get the name of this spell.
     */
    public String getName() {
        SpellInfo spellInfo = getInfo();
        return spellInfo == null ? this.getClass().getSimpleName() : spellInfo.name();
    }

    /**
     * Get the description of this spell.
     */
    public String getDescription() {
        SpellInfo spellInfo = getInfo();
        return spellInfo == null ? "None" : spellInfo.description();
    }

    /**
     * Get the icon used for this spell.
     */
    public Material getIcon() {
        SpellInfo spellInfo = getInfo();
        return spellInfo == null ? Material.STICK : spellInfo.icon();
    }

    /**
     * Gets whether the spell can be cast through walls.
     */
    public boolean canTravelThroughWalls() {
        SpellInfo spellInfo = getInfo();
        return spellInfo != null && spellInfo.travelThroughWalls();
    }

    /**
     * Gets the range of this spell.
     */
    public int getRange() {
        SpellInfo spellInfo = getInfo();
        return spellInfo == null ? 25 : spellInfo.range();
    }

    /**
     * Get the cooldown of this spell.
     */
    public int getCooldown(Player player) {
        FileConfiguration configuration = cfgManager.get(ConfigurationManager.ConfigurationType.COOLDOWNS).get();
        SpellInfo spellInfo = getInfo();
        if (spellInfo == null)
            return 60;

        String name = spellInfo.name();
        if (player.hasPermission("spells.no-cooldown") || player.hasPermission("spells.no-cooldown." + name))
            return 0;

        int cooldown = configuration.getInt("cooldowns." + name, spellInfo.cooldown());
        return cooldown == -1 ? spellInfo.cooldown() : cooldown;
    }

    public Permission getPermission() {
        return new Permission("spells.spell." + this.getName().toLowerCase().replaceAll(" ", "-"), PermissionDefault.TRUE);
    }

    /**
     * A utility method used to shorten the length of code of something from our configuration.
     */
    public Object getConfig(String key, @Nullable Object dft) {
        FileConfiguration configuration = cfgManager.get(ConfigurationManager.ConfigurationType.SPELLS).get();
        key = String.format("spells.%s.%s", getName().toLowerCase().replace(" ", "-"), key.toLowerCase());
        return dft == null ? configuration.get(key) : configuration.get(key, dft);
    }

    /**
     * Gets a time from the configuration as formatted.
     */
    public long getTime(String key, long defaultTime) {
        Object time = getConfig(key, "");
        if (time instanceof String) {
            String dur = (String) time;
            if (dur.equals(""))
                return defaultTime;

            int duration = 0;
            if (dur.endsWith("t")) {
                String ticks = dur.substring(0, dur.length() - 1);
                duration = Integer.parseInt(ticks);
            } else duration = Integer.parseInt(dur) * 20; // Convert to ticks.
        } else if (time instanceof Integer) {
            return ((Number) time).longValue() * 20; // Convert to ticks.
        }
        return defaultTime;
    }

    /**
     * Attempt to convert an Object to a double.
     */
    public double handleDouble(Object handle, double defaultTime) {
        double result = defaultTime;
        try {
            if (handle instanceof Double)
                result = (Double) handle;
            else if (handle instanceof Integer || handle instanceof String)
                result = Double.parseDouble(handle.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Called when a spell is cast.
     */
    public abstract boolean cast(Player player);

}
