package me.steamworks.spells.impl.curse;

import me.steamworks.SpellPlugin;
import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import me.steamworks.spells.SpellType;
import me.steamworks.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SpellInfo(
        name = "Sectum Sempra",
        description = "A dangerous curse, its effect was the equivalent of an invisible sword; it was used to slash the victim from a distance, causing rather deep wounds. This curse could also be used to remove body parts from the victim, which couldn't be grown back with healing magic.",
        range = 50,
        cooldown = 300,
        type = SpellType.CURSE
)
public class SectumSempra extends Spell {

    @Override
    public boolean cast(final Player player) {
        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                player.sendMessage(Locale.PLAYER_ENTITY_ONLY.format());
            }

            @Override
            public void entity(LivingEntity entity) {
                SpellRunnable runnable = new SpellRunnable(entity);
                runnable.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(INSTANCE, runnable, 0L, 20L);
            }
        }, 1f, Particle.REDSTONE);

        return true;
    }

    private static class SpellRunnable implements Runnable {
        final int length = SpellPlugin.getRandom().nextInt(4) + 2;
        LivingEntity entity;
        int taskId;
        int iterator = 0;

        public SpellRunnable(LivingEntity entity) {
            this.entity = entity;
        }

        @Override
        public void run() {
            if (entity.isValid())
                entity.damage(1);
            else Bukkit.getServer().getScheduler().cancelTask(taskId);

            if (iterator < length)
                iterator++;
            else Bukkit.getServer().getScheduler().cancelTask(taskId);
        }
    }
}
