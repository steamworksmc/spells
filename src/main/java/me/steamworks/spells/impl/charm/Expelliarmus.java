package me.steamworks.spells.impl.charm;

import me.steamworks.Targeter;
import me.steamworks.spells.Spell;
import me.steamworks.spells.SpellInfo;
import me.steamworks.utils.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpellInfo(
        name = "Expelliarmus",
        description = "A charm that forces whatever an opponent is holding to fly out of their hand. It is common to see this spell used in duels to make an opponent lose their wand.",
        range = 50,
        cooldown = 45,
        icon = Material.FIRE_CHARGE
)
public class Expelliarmus extends Spell {

    @Override
    public boolean cast(Player player) {

        INSTANCE.getTargeter().register(player, new Targeter.SpellHitEvent() {
            @Override
            public void block(Block block) {
                player.sendMessage(Locale.PLAYER_ENTITY_ONLY.format());
            }

            @Override
            public void entity(LivingEntity entity) {
                Location location = entity.getLocation();
                List<Material> disarm = new ArrayList<>();
                if ((boolean) getConfig("disarm-weapons", true))
                    disarm.addAll(Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.BOW));

                ItemStack itemStack = Objects.requireNonNull(entity.getEquipment()).getItemInMainHand();
                if (disarm.contains(itemStack.getType()) || INSTANCE.getWandManager().isWand(itemStack)) {
                    Item item = entity.getWorld().dropItem(location, entity.getEquipment().getItemInMainHand());
                    entity.getEquipment().setItemInMainHand(null);
                    Vector vector = location.getDirection();
                    item.setVelocity(new Vector(vector.getX() * 2, vector.getY() * 2, vector.getZ() * 2));
                }
            }
        }, 1f, 0.5f, 10, Particle.DRIP_LAVA);

        return true;
    }

}
