package me.steamworks.wand;

import lombok.Getter;
import me.steamworks.SpellPlugin;
import me.steamworks.api.event.impl.WandCreationEvent;
import me.steamworks.spells.Spell;
import me.steamworks.utils.Utilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.*;

public class WandManager {

    private final SpellPlugin INSTANCE = SpellPlugin.getInstance();

    @Getter
    private final List<Material> types = new ArrayList<>();

    public WandManager() {
        INSTANCE.getConfig().getStringList("wand.types").forEach(type -> {
            Material material = Material.matchMaterial(type);
            if (material != null && material != Material.AIR)
                types.add(material);
        });
        if (types.isEmpty())
            types.add(Material.STICK);
    }

    /**
     * Checks if a given {@link ItemStack} is useable as a wand.
     *
     * @return {@code true} If the ItemStack is useable as a wand.
     */
    public boolean isWand(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() && !types.contains(itemStack.getType()))
            return false;

        ItemMeta itemMeta = itemStack.getItemMeta();
        return Objects.requireNonNull(itemMeta).hasDisplayName() && ChatColor.stripColor(itemMeta.getDisplayName()).equals(ChatColor.stripColor(getName()));
    }

    /**
     * Get the first wand material for this player.
     */
    public ItemStack getWand(@Nullable Player player) {
        Material material = types.get(0);
        return getWand(player, material);
    }

    /**
     * Gets the wand from the material.
     */
    public ItemStack getWand(@Nullable Player player, Material material) {
        if (material == null || material.equals(Material.AIR))
            material = types.get(0);

        if (!types.contains(material))
            return null;

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        WandCreationEvent event = new WandCreationEvent(player, null, true);

        if ((boolean) getConfig("lore.enabled", true)) {
            WandLore lore = generate();
            if ((boolean) getConfig("lore.current-spell", true)) {
                Spell spell = INSTANCE.getSpellManager().getCurrentSpell(player);
                lore.setSpell(spell == null ? "None" : spell.getName());
            }
            event.setLore(lore);
        }

        event.setEnchantmentEffect(true);

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.hasLore()) {
            Objects.requireNonNull(itemMeta).setLore(event.getLore().generate());
        }

        Objects.requireNonNull(itemMeta).setDisplayName(ChatColor.RESET + getName());

        if (event.isEnchantmentEffect()) {
            try {
                itemMeta.addEnchant(Enchantment.LURE, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Gets the formatted wand name from the config.
     */
    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', (String) getConfig("lore.name", "Wand"));
    }

    /**
     * Generate random lore.
     */
    public WandLore generate() {
        WandLore lore = new WandLore();
        ConfigurationSection section = INSTANCE.getConfig().getConfigurationSection("wand.lore");
        Random random = SpellPlugin.getRandom();

        int rarity = Utilities.inBetween(1, 5);
        List<String> possibleRarities = Objects.requireNonNull(section).getStringList("types." + rarity),
                possibleRigidities = section.getStringList("rigidity");

        Map<String, Object> cores = Objects.requireNonNull(section.getConfigurationSection("cores")).getValues(false),
                manufacturers = Objects.requireNonNull(section.getConfigurationSection("manufacturers")).getValues(false);

        lore.setRarity(rarity);
        lore.setWood(possibleRarities.get(random.nextInt(possibleRarities.size())));
        lore.setRigidity(possibleRigidities.get(random.nextInt(possibleRigidities.size())));
        lore.setLength(Utilities.inBetween(section.getInt("length.minimum", 9), section.getInt("length.maximum", 18)));
        lore.setCore(WordUtils.capitalize(Utilities.getStringFromProbability(cores)));
        lore.setManufacturer(Utilities.getStringFromProbability(manufacturers));

        return lore;
    }

    private Object getConfig(String key, Object dft) {
        return INSTANCE.getConfig().get("wand." + key, dft);
    }

}
