package me.steamworks.api.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.steamworks.wand.WandLore;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
@Setter
public class WandCreationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player owner;
    private WandLore lore;
    private boolean enchantmentEffect;

    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean hasOwner() {
        return owner != null;
    }
    public boolean hasLore() {
        return lore != null;
    }

}
