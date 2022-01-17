package me.steamworks.commands;

import me.steamworks.SpellPlugin;
import me.steamworks.config.ConfigurationManager;
import me.steamworks.config.impl.DataConfiguration;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellManager;
import me.steamworks.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TeachCommands implements CommandExecutor {

    private final SpellPlugin INSTANCE = SpellPlugin.getInstance();
    private final SpellManager splManager = INSTANCE.getSpellManager();

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 2 || args.length == 0) {
            return false;
        }

        String firstArg = args[0];

        if (!splManager.isSpell(firstArg.replace('_', ' '))) {
            if (!(firstArg.equalsIgnoreCase("all") || firstArg.equalsIgnoreCase("*"))) {
                sender.sendMessage(Locale.SPELL_NOT_RECOGNIZED.format());
                return false;
            }
        }

        Spell spell = splManager.fetchSpell(firstArg.replace('_', ' '));
        Player target = null;

        if ((args.length == 1) || (args[1].equalsIgnoreCase("me"))) {
            if (sender instanceof Player)
                target = (Player) sender;
            else {
                sender.sendMessage(Locale.PLAYER_NO_PLAYER.format());
                return false;
            }
        } else {
            target = Bukkit.getPlayer(args[1]);
        }

        boolean teach = label.equalsIgnoreCase("teach");

        if(target == null) {
            sender.sendMessage(Locale.PLAYER_NO_PLAYER.format());
            return false;
        }

        Set<Spell> allSpells = splManager.getSpells();
        String appendedStr = "";

        if(teach) {
            boolean global = (sender instanceof Player) && sender.hasPermission("spells.teach.known") && !sender.isOp();
            List<String> knownSpells = ((DataConfiguration) INSTANCE.getCfgManager().get(ConfigurationManager.ConfigurationType.PLAYER_DATA)).getStringListOrEmpty(sender instanceof Player ? ((Player) sender).getUniqueId().toString() : null);

            if(firstArg.equalsIgnoreCase("all") || firstArg.equals("")) {
                boolean denied = false;

                for(Spell newSpell : allSpells) {
                    if(!newSpell.playerKnows(Objects.requireNonNull(target))) { // Should never be null, but I don't like warnings in code :(
                        if(!sender.hasPermission(newSpell.getPermission())) {
                            denied = true;
                            sender.sendMessage(Locale.SPELL_UNAUTHORIZED.format());
                            continue;
                        }

                        if(global && !knownSpells.contains(newSpell.getName())) {
                            denied = true;
                            sender.sendMessage(Locale.SPELL_CANNOT_TEACH.format());
                            continue;
                        }

                        newSpell.teach(target);
                        appendedStr = appendedStr == null ? Locale.SPELL_TAUGHT.format(newSpell.getName()) : appendedStr.concat(", " + newSpell.getName());
                    }
                }

                if(appendedStr == null) {
                    if(denied)
                        return true;
                    appendedStr = Locale.PLAYER_KNOWS_ALL.format();
                }
                sender.sendMessage(appendedStr);
            } else {
                if(!sender.hasPermission(spell.getPermission())) {
                    sender.sendMessage(Locale.SPELL_UNAUTHORIZED.format());
                    return true;
                }

                if(global && !knownSpells.contains(spell.getName())) {
                    sender.sendMessage(Locale.SPELL_CANNOT_TEACH.format());
                    return true;
                }
                sender.sendMessage(spell.playerKnows(target) ? Locale.PLAYER_KNOWS.format() : Locale.PLAYER_TAUGHT.format());
                if(!spell.playerKnows(target))
                    spell.teach(target);
            }
        } else {
            if (firstArg.equalsIgnoreCase("all") || firstArg.equalsIgnoreCase("*")) {
                if (splManager.getSpellPosition(target) == null) {
                    sender.sendMessage(Locale.PLAYER_DOESNT_KNOWS_ALL.format());
                    return true;
                }
                splManager.setCurrentSpell(target, 0);
                for (Spell newSpell : allSpells) {
                    if (newSpell.playerKnows(target)) {
                        if (appendedStr == null) {
                            newSpell.unteach(target);
                            appendedStr = Locale.SPELL_FORGOTTEN.format(newSpell.getName());
                        } else {
                            newSpell.unteach(target);
                            appendedStr = appendedStr.concat(", " + newSpell.getName());
                        }
                    }
                }
                if (appendedStr == null)
                    appendedStr = Locale.PLAYER_DOESNT_KNOWS_ALL.format();

                sender.sendMessage(appendedStr);
            } else {
                sender.sendMessage(spell.playerKnows(target) ? Locale.SPELL_FORGOTTEN.format(spell.getName()) : Locale.PLAYER_DOESNT_KNOW.format(spell.getName()));
                if (spell.playerKnows(target)) {
                    spell.unteach(target);
                    sender.sendMessage(Locale.SPELL_FORGOTTEN.format(spell.getName()));
                }
            }
        }

        return false;
    }
}
