package me.steamworks.spells.impl.curse;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import me.steamworks.spells.SpellType;
import me.steamworks.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpellInfo(
        name = "Reducto",
        description = "A curse that could be used to blast solid objects into pieces. It was rather easy to reduce a target to a fine mist or a pile of ashes.",
        range = 50,
        cooldown = 300,
        type = SpellType.CURSE
)
public class Reducto extends Spell implements Listener {

    private final List<Block> blocks = new ArrayList<>();

    @Override
    public boolean cast(Player player) {

        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                if (block.getType() != Material.AIR) {
                    blocks.add(block);
                    player.getWorld().createExplosion(block.getLocation(), 4);
                }
            }

            @Override
            public void entity(LivingEntity entity) {
                player.sendMessage(Locale.PLAYER_BLOCK_ONLY.format());
            }
        }, 1f, Particle.EXPLOSION_LARGE);

        return true;
    }

    @EventHandler
    public void onBlockExplodeEvent(BlockExplodeEvent event) {
        if (blocks.contains(event.getBlock())) {
            long replaceAfter = getTime("replace-blocks", 100);
            if (replaceAfter < 0)
                return;

            List<BlockState> states = event.blockList().stream().map(Block::getState).collect(Collectors.toList());
            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> {
                for (BlockState state : states)
                    state.update(true);
            }, replaceAfter);
        }
    }
}
