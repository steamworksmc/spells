package me.steamworks.api.event.impl;

import lombok.Getter;
import me.steamworks.api.event.SpellEvent;
import me.steamworks.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@Getter
public class SpellPostCastEvent extends SpellEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final boolean successful;

    public SpellPostCastEvent(Spell spell, Player caster, boolean successful) {
        super(spell, caster);
        this.successful = successful;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
