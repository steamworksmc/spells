package me.steamworks.utils;

import lombok.AllArgsConstructor;
import me.steamworks.SpellPlugin;
import org.bukkit.ChatColor;

import java.text.MessageFormat;
import java.util.Objects;

@AllArgsConstructor
public enum Locale {

    PLAYER_NOT_FOUND("PLAYER.NOT_fOUND"),
    PLAYER_NO_PLAYER("PLaYER.NO_PLAYER"),
    PLAYER_KNOWS("PLAYER.KNOWS"),
    PLAYER_TAUGHT("PLAYER.TAUgHT"),
    PLAYER_ENTITY_ONLY("PLAYER.ENTITY_ONLY"),
    PLAYER_BLOCK_ONLY("PLAYER.BLOCK_ONLY"),
    PLAYER_KNOWS_ALL("PLAYER.KNOWS_ALL"),
    PLAYER_DOESNT_KNOWS_ALL("PLAYER.DOESNT_KNOWS_ALL"),
    PLAYER_DOESNT_KNOW("PLAYER.DOESNT_KNOW"),

    SPELL_PLAYER_ONLY("SPELL.PLAYER_ONLY"),
    SPELL_NO_KNOWN_SPELLS("SPELL.NO_KNOWN_SPELLS"),
    SPELL_PLAYER_WAIT("SPELL.PLAYER_WAIT"),
    SPELL_SELECTED("SPELL.SELECTED"),
    SPELL_UNAUTHORIZED("SPELL.UNAUTHORIZED"),
    SPELL_NOT_RECOGNIZED("SPELL.NOT_RECOGNIZED"),
    SPELL_CANNOT_TEACH("SPELL.CANNOT_TEACH"),
    SPELL_TAUGHT("SPELL.TAUGHT"),
    SPELL_FORGOTTEN("SPELL.FORGOTTEN"),

    PLAYER_NO_SPACE("PLAYER.NO_SPACE"),

    WAND_GIVEN("WAND.GIVEN"),
    WAND_INVALID("WAND.INVALID"),

    COMMAND_PLAYER_ONLY("COMMAND.PLAYER_ONLY");

    private String path;

    public String format(Object... objects) {
        System.out.println("messages." + this.path.toLowerCase());
        return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(SpellPlugin.getInstance().getConfig().getString("messages." + path.toLowerCase()))).format(objects));
    }

}
