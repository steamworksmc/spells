package me.steamworks.commands;

import me.steamworks.SpellPlugin;
import me.steamworks.utils.Locale;
import me.steamworks.wand.WandManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WandCommand implements CommandExecutor {

    private final SpellPlugin INSTANCE = SpellPlugin.getInstance();
    private final WandManager wndManager = INSTANCE.getWandManager();

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if(args.length == 0) {
            if(sender instanceof Player)
                give((Player) sender, null);
            else sender.sendMessage(Locale.COMMAND_PLAYER_ONLY.format());
        } else if(args.length <= 2) {
            String firstArg = args[0];
            if(firstArg.equalsIgnoreCase("me") || firstArg.equalsIgnoreCase(sender.getName())) {
                if(sender instanceof Player) {
                    if(args.length == 2) {
                        Material providedMat = Material.matchMaterial(args[1]);
                        if(wndManager.getTypes().contains(providedMat))
                            give((Player) sender, providedMat);
                        else sender.sendMessage(Locale.WAND_INVALID.format(providedMat == null ? "NULL" : providedMat.toString()));
                    } else give((Player) sender, null);
                } else sender.sendMessage(Locale.COMMAND_PLAYER_ONLY.format());
            } else if(sender.hasPermission("spells.wand.others")) {
                Player target = Bukkit.getPlayer(firstArg);
                if(target != null) {
                    if(args.length == 2) {
                        Material providedMat = Material.matchMaterial(args[1]);
                        if(wndManager.getTypes().contains(providedMat))
                            give(target, providedMat);
                        else sender.sendMessage(Locale.WAND_INVALID.format(providedMat == null ? "NULL" : providedMat.toString()));
                    } else give(target, null);
                } else {
                    sender.sendMessage(Locale.PLAYER_NOT_FOUND.format());
                }
            }
        } else return false;
        return true;
    }

    public void give(Player player, Material wandMaterial) {
        player.sendMessage(player.getInventory().firstEmpty() == -1 ? Locale.PLAYER_NO_SPACE.format() : Locale.WAND_GIVEN.format(WordUtils.capitalize(wandMaterial.name().toLowerCase())));
        if(player.getInventory().firstEmpty() != -1)
            player.getInventory().addItem(wndManager.getWand(player, wandMaterial));
    }
}
