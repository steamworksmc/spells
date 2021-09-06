package me.steamworks.api.event.impl;

import lombok.Getter;
import lombok.Setter;
import me.steamworks.api.event.SpellEvent;
import me.steamworks.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
public class SpellPreCastEvent extends SpellEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled = false;

    public SpellPreCastEvent(Spell spell, Player caster) {
        super(spell, caster);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
