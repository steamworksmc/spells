package me.steamworks.spells.impl.charm;

import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@SpellInfo(
        name = "Accio",
        description = "A charm that summons a object toward the caster",
        cooldown = 5
)
public class Accio extends Spell {

    @Override
    public boolean cast(Player player) {
        int radius = (Integer) getConfig("radius", 5);
        boolean worked = false;

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                worked = true;

                Vector newVector = player.getLocation().add(0, 1, 0).toVector().subtract(item.getLocation().toVector()).divide(new Vector(5, 5, 5));
                item.setVelocity(newVector);
            }
        }

        return worked;
    }
}
