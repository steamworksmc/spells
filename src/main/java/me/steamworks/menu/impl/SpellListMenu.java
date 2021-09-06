package me.steamworks.menu.impl;

import me.steamworks.menu.Button;
import me.steamworks.menu.Menu;
import me.steamworks.spells.SpellType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpellListMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW + "Spells";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return new HashMap<Integer, Button>() {{
            for (int i = 0; i < getSize(); i++)
                put(i, Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, (byte) 0, ""));
            for (int i = 0; i < SpellType.values().length; i++) {
                SpellType type = SpellType.values()[i];
                put(i + 11, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        ItemStack itemStack = new ItemStack(Material.STICK);
                        ItemMeta itemMeta = itemStack.getItemMeta();

                        Objects.requireNonNull(itemMeta).setDisplayName(ChatColor.YELLOW + WordUtils.capitalize(type.name().toLowerCase()));
                        itemMeta.setLore(Collections.singletonList(
                                ChatColor.GRAY + "Click to view all &e" + type.name().toLowerCase() + ChatColor.GRAY + " spells."
                        ));
                        itemMeta.addEnchant(Enchantment.LURE, 1, true);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                        itemStack.setItemMeta(itemMeta);
                        return itemStack;
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        new SpecificSpellListMenu(type).openMenu(player);
                    }
                });
            }
        }};
    }

    @Override
    public int getSize() {
        return 27;
    }
}
