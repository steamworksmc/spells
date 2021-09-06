package me.steamworks.spells.impl.charm;

import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

@SpellInfo(
        name = "Morsmordre",
        description = "A dark charm used to conjure the image of the Dark Mark.",
        cooldown = 60
)
public class Morsmordre extends Spell {

    @Override
    public boolean cast(Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.CREEPER).withColor(Color.GREEN, Color.LIME, Color.GRAY).flicker(true).trail(true).withFade(Color.BLACK).build());
        fireworkMeta.setPower(3);

        firework.setFireworkMeta(fireworkMeta);
        return true;
    }

}
