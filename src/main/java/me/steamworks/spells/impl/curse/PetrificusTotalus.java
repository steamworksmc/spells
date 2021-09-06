package me.steamworks.spells.impl.curse;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import me.steamworks.spells.SpellType;
import me.steamworks.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpellInfo(
        name = "Petrificus Totalus",
        description = "A charm that instantly paralyses a opponent.",
        range = 50,
        cooldown = 300,
        type = SpellType.CURSE
)
public class PetrificusTotalus extends Spell implements Listener {

    public static List<UUID> AFFECTED = new ArrayList<>();

    @Override
    public boolean cast(Player player) {
        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                player.sendMessage(Locale.PLAYER_ENTITY_ONLY.format());
            }

            @Override
            public void entity(LivingEntity entity) {
                if (entity instanceof Player) {
                    Player target = (Player) entity;
                    AFFECTED.add(target.getUniqueId());

                    long duration = getTime("duration", 600L);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> {
                        AFFECTED.remove(target.getUniqueId());
                    }, duration);

                    Location location = target.getLocation().add(0, 1, 0);
                    target.getWorld().createExplosion(location, 0F);
                } else {
                    player.sendMessage(Locale.SPELL_PLAYER_ONLY.format());
                }
            }
        }, 1f, Particle.CRIT);
        return true;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (AFFECTED.contains(event.getPlayer().getUniqueId()))
            event.setTo(event.getFrom());
    }
}
