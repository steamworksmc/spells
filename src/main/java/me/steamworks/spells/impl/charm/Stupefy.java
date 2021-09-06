package me.steamworks.spells.impl.charm;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import me.steamworks.utils.Locale;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@SpellInfo(
        name = "Stupefy",
        description = "A charm that renders a victim unconscious and halts moving objects.",
        range = 50,
        cooldown = 300
)
public class Stupefy extends Spell {

    @Override
    public boolean cast(Player player) {

        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                player.sendMessage(Locale.PLAYER_ENTITY_ONLY.format());
            }

            @Override
            public void entity(LivingEntity entity) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) getTime("confusion", 200L), 1));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (int) getTime("weakness", 100L), 1));

                Vector unit = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(unit.multiply((int) getConfig("vertical-kb", 2)));
                entity.setVelocity(entity.getVelocity().setY(handleDouble(getConfig("horizontal-kb", 0.5), 0.5)));
                entity.damage((int) getConfig("damage", 2));
            }
        }, 1.05, FireworkEffect.builder().trail(false).flicker(true).withColor(Color.RED).with(FireworkEffect.Type.BURST).build());

        return true;
    }
}
