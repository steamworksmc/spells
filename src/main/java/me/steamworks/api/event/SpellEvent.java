package me.steamworks.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.steamworks.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class SpellEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Spell spell;
    private Player caster;

    public HandlerList getHandlers() {
        return handlers;
    }
}
