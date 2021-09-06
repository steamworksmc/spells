package me.steamworks.spells;

import com.google.common.collect.Iterables;
import lombok.Getter;
import me.steamworks.SpellPlugin;
import me.steamworks.api.event.impl.SpellPostCastEvent;
import me.steamworks.api.event.impl.SpellPreCastEvent;
import me.steamworks.config.ConfigurationManager;
import me.steamworks.config.impl.DataConfiguration;
import me.steamworks.utils.Locale;
import me.steamworks.utils.ReflectionsReplacement;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class SpellManager {

    private static final List<ItemStack> recipeResults = new ArrayList<>();
    private final SpellPlugin INSTANCE = SpellPlugin.getInstance();
    private final Map<UUID, Map<Spell, Long>> cooldowns = new HashMap<>();
    private final Comparator<Spell> comparator = Comparator.comparing(Spell::getName);
    private final SortedSet<Spell> spells = new TreeSet<>(comparator);
    private final Map<UUID, Integer> current = new HashMap<>();

    public SpellManager() {
        for (SpellType type : SpellType.values()) {
            try {
                for (Class<?> clazz : ReflectionsReplacement.getSubtypesOf(Spell.class, "me.steamworks.spells.impl." + type.name().toLowerCase(), INSTANCE.getClass().getClassLoader())) {
                    if (clazz.getAnnotation(SpellInfo.class) == null)
                        continue;

                    Spell spell;
                    try {
                        spell = (Spell) clazz.getConstructor().newInstance();

                        if (Listener.class.isAssignableFrom(clazz)) {
                            INSTANCE.getServer().getPluginManager().registerEvents((Listener) spell, INSTANCE);
                        }

                        INSTANCE.getServer().getPluginManager().addPermission(spell.getPermission());
                    } catch (Exception e) {
                        continue;
                    }
                    spells.add(spell);
                }
            } catch (NullPointerException ignored) {
                // Most likely to receive something along the lines of cannot invoke protocol because url is null.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the current spell position a player is on.
     */
    public Integer getSpellPosition(Player player) {
        DataConfiguration cfg = (DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA);
        List<String> known = cfg.getStringListOrEmpty(player.getUniqueId().toString());

        if (known.isEmpty())
            return null;

        if (!current.containsKey(player.getUniqueId()))
            return 0;

        return current.get(player.getUniqueId());
    }

    /**
     * Get the current spell a player is on.
     */
    public Spell getCurrentSpell(Player player) {
        DataConfiguration cfg = (DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA);
        Integer curr = getSpellPosition(player);
        List<String> spells = cfg.getStringListOrEmpty(player.getUniqueId().toString());
        if (spells.isEmpty())
            return null;

        return curr == null ? null : fetchSpell(Iterables.get(new ArrayList<>(spells), curr));
    }

    /**
     * Sets the current spell a player is on.
     */
    public Spell setCurrentSpell(Player player, int id) {
        DataConfiguration cfg = (DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA);
        List<String> known = cfg.getStringListOrEmpty(player.getUniqueId().toString());

        if (known == null || id >= known.size() || id < 0)
            throw new IllegalStateException("The provided Id is invalid.");

        current.put(player.getUniqueId(), id); // todo
        return getCurrentSpell(player);
    }

    public Spell setCurrentSpell(Player player, Spell spell) {
        DataConfiguration cfg = (DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA);
        Integer index = getIndex(new TreeSet<>(cfg.getStringListOrEmpty(player.getUniqueId().toString())), spell.getName());
        if (index == null)
            throw new IllegalArgumentException("The player does not know this spell.");
        setCurrentSpell(player, index);
        cfg.save();
        return getCurrentSpell(player);
    }

    /**
     * Get a spell by its name.
     */
    public Spell fetchSpell(String name) {
        for (Spell spell : spells)
            if (spell.getName().equalsIgnoreCase(name))
                return spell;
        return null;
    }

    /**
     * Validate if the provided name is a spell.
     */
    public boolean isSpell(String name) {
        return fetchSpell(name) != null;
    }

    /**
     * Adds a spell to the list.
     */
    public void add(Spell spell) {
        spells.add(spell);
    }

    /**
     * Casts a spell cleverly. Checks permissions, triggers {@link me.steamworks.api.event.SpellEvent}, sending effects, and applying cooldowns.
     */
    public void cast(Player player, Spell spell) {
        if (!player.hasPermission("spells.cast") || !spell.playerKnows(player))
            return;

        DataConfiguration cfg = (DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA);
        List<String> known = cfg.getStringListOrEmpty(player.getUniqueId().toString());
        if (known == null || known.isEmpty()) {
            player.sendMessage(Locale.SPELL_NO_KNOWN_SPELLS.format());
            return;
        }

        SpellPreCastEvent event = new SpellPreCastEvent(spell, player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            boolean cast = true;
            UUID playerUuid = player.getUniqueId();

            if (needsCooldown(playerUuid, spell)) {
                player.sendMessage(Locale.SPELL_PLAYER_WAIT.format(spell.getCooldown(player) - ((System.currentTimeMillis() - cooldowns.get(player.getUniqueId()).get(spell)) / 1000)));
                cast = false;
            }

            boolean successful = false;
            if (cast) {
                successful = spell.cast(player);
                player.getWorld().playEffect(
                        player.getLocation().add(0, 1, 0),
                        Effect.ENDER_SIGNAL,
                        0
                );
            }

            Bukkit.getServer().getPluginManager().callEvent(new SpellPostCastEvent(spell, player, successful));
            if (cast && successful && spell.getCooldown(player) > 0)
                setCooldown(playerUuid, spell);
        }
    }

    /**
     * Gets ifa cooldown is needed by a player for a certain spell.
     */
    public boolean needsCooldown(UUID uuid, Spell spell) {
        if (cooldowns.containsKey(uuid) && cooldowns.get(uuid).containsKey(spell)) {
            long current = System.currentTimeMillis();
            long last = cooldowns.get(uuid).get(spell);
            long difference = (current - last) / 1000;
            return difference < spell.getCooldown(Bukkit.getPlayer(uuid));
        }
        return false;
    }

    /**
     * Sets the cooldown of a certain spell for a certain player for right now.
     */
    public void setCooldown(UUID uuid, Spell spell) {
        setCooldown(uuid, spell, null);
    }

    /**
     * Sets the cooldown of a certain spell for a certain player.
     */
    public void setCooldown(UUID uuid, Spell spell, Long milliseconds) {
        if (milliseconds == null)
            milliseconds = 0L;

        if (cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).put(spell, System.currentTimeMillis() + milliseconds);
        } else {
            Long finalMilliseconds = milliseconds;
            cooldowns.put(uuid, new HashMap<Spell, Long>() {{
                put(spell, System.currentTimeMillis() + finalMilliseconds);
            }});
        }
    }

    /**
     * Gets the index of a set.
     */
    private Integer getIndex(Set<? extends Object> set, Object value) {
        int result = 0;
        for (Object entry : set) {
            if (entry.equals(value))
                return result;
            result++;
        }
        return null;
    }

}
