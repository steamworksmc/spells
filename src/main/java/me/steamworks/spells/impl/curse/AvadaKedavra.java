package me.steamworks.spells.impl.curse;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import me.steamworks.spells.SpellType;
import me.steamworks.utils.Locale;
import me.steamworks.utils.WrappedParticle;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SpellInfo(
        name = "Avada Kedavra",
        description = "Causes instantaneous death. It is accompanied by a flash of green light and a rushing noise. There is no known Counter-curse that can protect the victim from dying, except for a loving sacrifice. It is one of the three Unforgivable Curses.",
        range = 50,
        cooldown = 300,
        type = SpellType.CURSE
)
public class AvadaKedavra extends Spell {

    @Override
    public boolean cast(final Player player) {
        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                player.sendMessage(Locale.PLAYER_ENTITY_ONLY.format());
            }

            @Override
            public void entity(LivingEntity entity) {
                entity.setHealth(0);
            }
        }, 1, 0.5, 2, new WrappedParticle(Particle.REDSTONE, new Particle.DustOptions(Color.GREEN, 1)));

        return true;
    }

}
