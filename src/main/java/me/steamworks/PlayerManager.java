package me.steamworks;

import me.steamworks.spells.SpellNotification;
import me.steamworks.utils.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;

public class PlayerManager {

    private final String prefix = "&7[&eSpells&7] ";
    private final ChatColor info = ChatColor.BLUE, warning = ChatColor.YELLOW;

    /**
     * A way to send notifications to players.
     */
    public void sendNotification(Player player, SpellNotification notification) {
        if (player == null || notification == null)
            return;

        Note note = null;
        switch (notification) {
            case SUCCESS:
                note = new Note(6);
                break;
            case MISSED:
                note = new Note(4);
                break;
            case FAILED:
                note = new Note(2);
                break;
        }
        player.playNote(player.getLocation(), Instrument.PIANO, note);
    }

    /**
     * Sends a player a spell notification.
     */
    public void newSpell(Player player, String spell) {
        player.sendMessage(Locale.SPELL_SELECTED.format(ChatColor.AQUA + spell));
    }

}
