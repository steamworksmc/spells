package me.steamworks.spells.impl.charm;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SpellInfo(
        name = "Aguamenti",
        description = "A charm that conjures a jet of clean, drinkable water from the tip of the caster's wand.",
        cooldown = 90,
        range = 50,
        icon = Material.WATER_BUCKET
)
public class Aguamenti extends Spell {

    @Override
    public boolean cast(Player player) {
        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                if (!block.getType().isTransparent() && block.getRelative(BlockFace.UP).getType().isTransparent()) {
                    block = block.getRelative(BlockFace.UP);
                }

                block.setType(Material.WATER);
                Block finalBlock = block;
                Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> {
                    finalBlock.setType(Material.AIR);
                }, getTime("duration", 600));
            }

            @Override
            public void entity(LivingEntity entity) {
                block(entity.getEyeLocation().getBlock());
            }
        }, 1.2f, Particle.WATER_DROP);

        return true;
    }
}
