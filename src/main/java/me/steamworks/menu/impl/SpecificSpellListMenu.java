package me.steamworks.menu.impl;

import lombok.AllArgsConstructor;
import me.steamworks.SpellPlugin;
import me.steamworks.menu.Button;
import me.steamworks.menu.Menu;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellType;
import me.steamworks.utils.CC;
import me.steamworks.utils.TimeUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SpecificSpellListMenu extends Menu {

    private final SpellType spellType;

    @Override
    public String getTitle(Player player) {
        return "&eViewing &b" + WordUtils.capitalize(spellType.name().toLowerCase()) + " &espells.";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        List<String> open = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            open.add(String.valueOf(i));
            if (i < 9 || i >= getSize() - 9 || i % 9 == 0 || i % 9 == 8) {
                open.remove(String.valueOf(i));
                buttons.put(i, Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, (byte) 0, ""));
            }
        }

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
                ItemMeta itemMeta = itemStack.getItemMeta();

                Objects.requireNonNull(itemMeta).addEnchant(Enchantment.LUCK, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemMeta.setDisplayName(ChatColor.YELLOW + "Information");
                itemMeta.setLore(CC.color(Arrays.asList(
                    "",
                    "&7Left Click &eto enable the spell.",
                    "&7Right Click &eto learn it, if you don't already know it.",
                    ""
                )));
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }
        });

        for (Spell spell : SpellPlugin.getInstance().getSpellManager().getSpells().stream().filter(s -> s.getInfo().type().equals(spellType)).collect(Collectors.toList())) {
            buttons.put(Integer.valueOf(open.get(0)), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemStack spellItem = new ItemStack(spell.getInfo().icon());
                    ItemMeta spellMeta = spellItem.getItemMeta();
                    spellMeta.setDisplayName(ChatColor.YELLOW + spell.getName());
                    spellMeta.setLore(CC.color(new ArrayList<String>() {{
                        add("&6" + WordUtils.capitalize(spell.getInfo().type().name().toLowerCase()) + " &eClassification");
                        add("");
                        add("&eDescription &7| &f" + spell.getDescription());
                        add("&eRange &7| &f" + spell.getRange());
                        add("&eCooldown &7| &f" + TimeUtil.formatTimeSeconds(spell.getInfo().cooldown()));
                        add("");
                        add(spell.playerKnows(player) ? "&aYou know this spell." : "&cYou do not know this spell.");
                    }}));
                    spellItem.setItemMeta(spellMeta);
                    return spellItem;
                }
            });
            open.remove(0);
        }

        return buttons;
    }

    @Override
    public int getSize() {
        return 54;
    }
}
