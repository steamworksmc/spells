package me.steamworks;

import lombok.Getter;
import me.steamworks.commands.TeachCommands;
import me.steamworks.commands.WandCommand;
import me.steamworks.config.ConfigurationManager;
import me.steamworks.listeners.SpellListener;
import me.steamworks.menu.MenuListener;
import me.steamworks.spells.SpellManager;
import me.steamworks.wand.WandManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

@Getter
public class SpellPlugin extends JavaPlugin {

    @Getter private static final Random random = new Random();
    @Getter private static SpellPlugin instance;

    private Targeter targeter;
    private ConfigurationManager cfgManager;
    private SpellManager spellManager;
    private WandManager wandManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;

        (cfgManager = new ConfigurationManager()).load();
        targeter = new Targeter();
        spellManager = new SpellManager();
        wandManager = new WandManager();
        playerManager = new PlayerManager();

        Objects.requireNonNull(getCommand("wand")).setExecutor(new WandCommand());
        Objects.requireNonNull(getCommand("teach")).setExecutor(new TeachCommands());
        Objects.requireNonNull(getCommand("unteach")).setExecutor(new TeachCommands());

        getServer().getPluginManager().registerEvents(new SpellListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        this.getLogger().info(String.format(ChatColor.GREEN + "Registered '%s' spells.", spellManager.getSpells().size()));
    }
}
