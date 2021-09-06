package me.steamworks.spells.impl.charm;

import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpellInfo(
        name = "Wingardium Leviosa",
        description = "A charm that levitates objects. However, it does not also allow one to move said objects and requires contact with the target",
        range = 0,
        cooldown = 600
)
public class WingardiumLeviosa extends Spell implements Listener {

    private final List<UUID> affected = new ArrayList<>();

    @Override
    public boolean cast(Player player) {
        if(affected.contains(player.getUniqueId())) {
            player.setFlying(false);
            player.setAllowFlight(false);
            affected.remove(player.getUniqueId());
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);

            if((boolean) getConfig("cancel-fall-damage", true))
                affected.add(player.getUniqueId());

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> {
                if(affected.contains(player.getUniqueId())) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    affected.remove(player.getUniqueId());
                }
            }, getTime("duration", 200L));
        }
        return true;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity() instanceof Player && affected.contains(event.getEntity().getUniqueId()))
            event.setDamage(0);
    }
}
