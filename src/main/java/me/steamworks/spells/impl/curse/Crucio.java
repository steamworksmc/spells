package me.steamworks.spells.impl.curse;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import me.steamworks.spells.SpellType;
import me.steamworks.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpellInfo(
        name = "Crucio",
        description = "A tool of the Dark Arts and was one of the three Unforgivable Curses. It was one of the most powerful and sinister spells known to wizard-kind. When cast successfully, the curse inflicted intense, excruciating pain on the victim. Prolonged exposure could cause profound brain damage.",
        range = 50,
        cooldown = 300,
        type = SpellType.CURSE
)
public class Crucio extends Spell implements Listener {

    private final Set<UUID> affected = new HashSet<>();

    @Override
    public boolean cast(final Player player) {

        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                player.sendMessage(Locale.PLAYER_ENTITY_ONLY.format());
            }

            @Override
            public void entity(LivingEntity entity) {
                if (entity instanceof Player) {
                    final Player target = (Player) entity;
                    long duration = getTime("duration", 200L);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.HARM, (int) duration, 0));
                    toggle(target, true);
                    target.teleport(target.getLocation().add(0, 2, 0));
                    affected.add(target.getUniqueId());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, new Runnable() {
                        @Override
                        public void run() {
                            affected.remove(target.getUniqueId());
                            toggle(target, false);
                        }
                    });
                    return;
                }

                player.sendMessage(Locale.SPELL_PLAYER_ONLY.format());
            }
        }, 1.2f, Particle.SUSPENDED_DEPTH);

        return true;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (affected.contains(event.getPlayer().getUniqueId())) {
            Location changeTo = event.getFrom();
            changeTo.setPitch(event.getTo().getPitch());
            changeTo.setYaw(event.getTo().getYaw());
            event.setTo(changeTo);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && affected.contains(event.getEntity().getUniqueId())) {
            Player player = (Player) event.getEntity();
            if ((player.getHealth() - event.getDamage()) < 1)
                event.setDamage(0);
        }
    }

    private void toggle(Player player, boolean allow) {
        if (!allow && player.getGameMode().equals(GameMode.CREATIVE)) {
            player.setFlying(false);
            player.setAllowFlight(true);
            return;
        }
        player.setAllowFlight(allow);
        player.setFlying(allow);
    }
}
