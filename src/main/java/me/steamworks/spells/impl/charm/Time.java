package me.steamworks.spells.impl.charm;

import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

@SpellInfo(
        name = "Time",
        description = "Manipulate the time depending on the aimed block!",
        range = 0,
        cooldown = 600,
        icon = Material.CLOCK
)
public class Time extends Spell {

    @Override
    public boolean cast(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        Material material = player.getTargetBlock(null, 50).getType();
        Objects.requireNonNull(world).setTime(
                material == Material.GLOWSTONE ? 0L : (material == Material.OBSIDIAN ? 15000L : (world.getTime() < 12000 ? world.getTime() + 12000 : world.getTime() - 12000))
        );

        if ((boolean) getConfig("lightning", true)) {
            double x = location.getX(), y = location.getY(), z = location.getZ();
            world.strikeLightningEffect(new Location(world, x, y, z - 2));
            world.strikeLightningEffect(new Location(world, x, y, z + 2));
            world.strikeLightningEffect(new Location(world, x - 2, y, z));
            world.strikeLightningEffect(new Location(world, x + 2, y, z));
        }
        return true;
    }
}
