package me.steamworks.spells.impl.charm;

import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@SpellInfo(
        name = "Pack",
        description = "A charm to make items pack themselves into a trunk.",
        range = 10,
        cooldown = 10,
        icon = Material.CHEST
)
public class Pack extends Spell {

    @Override
    public boolean cast(Player player) {
        Inventory inventory = null;
        Location location = player.getLocation();
        int radius = 5;

        loop:
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z).getBlock();
                    System.out.println(block.getType().name());
                    if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getType().name().contains("_SHULKER_BOX")) {
                        inventory = ((Container) block.getState()).getInventory();
                        break loop;
                    } else if (block.getType() == Material.ENDER_CHEST) {
                        inventory = player.getEnderChest();
                        break loop;
                    }
                }
            }
        }

        System.out.println("pogge");

        if (inventory == null)
            return false;

        System.out.println("pogget");

        for (Entity e : player.getNearbyEntities(5, 5, 5)) {
            if (e instanceof Item) {
                Item item = (Item) e;
                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(item.getItemStack());
                    item.remove();
                }
            }
        }
        return true;
    }

}
