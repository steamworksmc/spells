package me.steamworks.spells.impl.charm;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SpellInfo(
        name = "Incendio",
        description = "A charm that conjures a jet of flames that can be used to set things alight.",
        range = 50,
        cooldown = 45,
        icon = Material.FIRE_CHARGE
)
public class Incendio extends Spell {
    @Override
    public boolean cast(Player player) {

        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                Block blockState = block.getRelative(BlockFace.UP);
                if (blockState.getType().isTransparent())
                    blockState.setType(Material.FIRE);
            }

            @Override
            public void entity(LivingEntity entity) {
                entity.setFireTicks((int) getTime("duration", 100L));
            }
        }, 1.05d, Effect.MOBSPAWNER_FLAMES);

        return true;
    }
}
