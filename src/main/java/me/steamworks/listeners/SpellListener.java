package me.steamworks.listeners;

import me.steamworks.SpellPlugin;
import me.steamworks.api.event.impl.SpellPreCastEvent;
import me.steamworks.config.ConfigurationManager;
import me.steamworks.config.impl.DataConfiguration;
import me.steamworks.menu.impl.SpellListMenu;
import me.steamworks.spells.Spell;
import me.steamworks.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SpellListener implements Listener {

    public static final Permission CAST_PERMISSION = new Permission("spells.cast", PermissionDefault.OP);
    private final SpellPlugin INSTANCE = SpellPlugin.getInstance();
    private final List<Material> buttons = new ArrayList<>(Arrays.asList(
            Material.ACACIA_BUTTON,
            Material.BIRCH_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.STONE_BUTTON
    ));

    public SpellListener() {
        Bukkit.getServer().getPluginManager().addPermission(CAST_PERMISSION);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND))
            return;

        if (event.getPlayer().hasPermission(CAST_PERMISSION) && INSTANCE.getWandManager().isWand(event.getPlayer().getInventory().getItemInMainHand())) {
            DataConfiguration cfg = (DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA);

            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                event.setCancelled(true);

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Integer knows = cfg.getStringListOrEmpty(event.getPlayer().getUniqueId().toString()).size() - 1, cur = INSTANCE.getSpellManager().getSpellPosition(event.getPlayer()), neww;

                if (knows == -1 || cur == null) {
                    event.getPlayer().sendMessage(Locale.SPELL_NO_KNOWN_SPELLS.format());
                    return;
                }


                if (event.getPlayer().isSneaking()) {
                    if (cur == 0)
                        neww = knows;
                    else
                        neww = cur - 1;
                } else {
                    if (cur.equals(knows))
                        neww = 0;
                    else
                        neww = cur + 1;
                }

                try {
                    INSTANCE.getPlayerManager().newSpell(event.getPlayer(), INSTANCE.getSpellManager().setCurrentSpell(event.getPlayer(), neww).getName());
                    if (INSTANCE.getConfig().getBoolean("wand.lore.current-spell")) {
                        int wIndex = 0;
                        ItemStack wand = INSTANCE.getWandManager().getWand(event.getPlayer());
                        ItemMeta itemMeta = wand.getItemMeta();
                        List<String> lore = Objects.requireNonNull(itemMeta).getLore();
                        for (String line : Objects.requireNonNull(lore)) {
                            if (ChatColor.stripColor(line).contains("Spell")) {
                                int index = lore.indexOf(line);
                                Spell spell = INSTANCE.getSpellManager().getCurrentSpell(event.getPlayer());
                                for (String line1 : INSTANCE.getConfig().getStringList("wand.lore.format")) {
                                    if (ChatColor.stripColor(line1).contains("{spell}")) {
                                        line = ChatColor.translateAlternateColorCodes('&', line);
                                        line = line.replace("{spell}", spell == null ? "None" : spell.getName());
                                        lore.set(index, line);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        itemMeta.setLore(lore);
                        wand.setItemMeta(itemMeta);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onPlayerInteractEvent(event);
                }

                return;
            }

            if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (event.getPlayer().isSneaking()) {
                    new SpellListMenu().openMenu(event.getPlayer());
                    return;
                }
                Spell curr = INSTANCE.getSpellManager().getCurrentSpell(event.getPlayer());
                if (curr != null)
                    INSTANCE.getSpellManager().cast(event.getPlayer(), curr);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getPlayer().hasPermission(CAST_PERMISSION) && INSTANCE.getWandManager().isWand(event.getPlayer().getInventory().getItemInMainHand())) {
            DataConfiguration cfg = (DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA);

            Integer knownSpells = cfg.getStringListOrEmpty(event.getPlayer().getUniqueId().toString()).size() - 1,
                    curr = INSTANCE.getSpellManager().getSpellPosition(event.getPlayer()),
                    newList;

            if (knownSpells == -1 || curr == null) {
                event.getPlayer().sendMessage(Locale.SPELL_NO_KNOWN_SPELLS.format());
                return;
            }

            if (event.getPlayer().isSneaking()) {
                if (curr == 0) newList = knownSpells;
                else newList = curr - 1;
            } else {
                if (curr.equals(knownSpells))
                    newList = 0;
                else newList = curr + 1;
            }

            try {
                INSTANCE.getPlayerManager().newSpell(event.getPlayer(), INSTANCE.getSpellManager().setCurrentSpell(event.getPlayer(), newList).getName());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {
                onPlayerInteractEntityEvent(event);
            }
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent e) {
        if (INSTANCE.getWandManager().isWand(e.getRecipe().getResult())) {
            e.setCurrentItem(INSTANCE.getWandManager().getWand((Player) e.getWhoClicked()));
            final Player p = (Player) e.getWhoClicked();
            Bukkit.getScheduler().runTask(INSTANCE, p::updateInventory);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpellCast(SpellPreCastEvent e) {
        if (!e.getCaster().hasPermission(e.getSpell().getPermission())) {
            e.setCancelled(true);
            e.getCaster().sendMessage(Locale.SPELL_UNAUTHORIZED.format());
        }
    }

}
