package me.steamworks;

import me.steamworks.utils.FireworkEffectPlayer;
import me.steamworks.utils.WrappedParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Targeter {

    private final SpellPlugin instance = SpellPlugin.getInstance();

    /**
     * Registers a new {@link SpellHitEvent} to be called when a spell something.
     */
    public void register(final Player caster, final SpellHitEvent event, final double speed, final Effect effect) {
        register(caster, event, speed, effect, null);
    }

    /**
     * Registers a new {@link SpellHitEvent} to be called when a spell hits something using the default offset and count.
     */
    public void register(final Player caster, final SpellHitEvent onHit, final double spellSpeed, final Particle effect) {
        register(caster, onHit, spellSpeed, 0.5f, 1, effect);
    }

    /**
     * Registers a new {@link SpellHitEvent} to be called when a spell hits something.
     */
    public void register(final Player caster, final SpellHitEvent event, final double speed, final Effect effect, final Integer effectArg) {
        new BukkitRunnable() {
            final Location location = caster.getEyeLocation();
            final Vector direction = location.getDirection().multiply(speed);
            final long max = -1;
            boolean running = false;
            long tracker = 0;

            @Override
            public void run() {
                if (!running) {
                    runTaskTimer(instance, 0L, 1L);
                    running = true;
                }

                location.add(direction);
                location.getWorld().playEffect(location, effect, effectArg == null ? 0 : effectArg);

                if (!location.getBlock().getType().isTransparent()) {
                    event.block(location.getBlock());
                    cancel();
                    return;
                }

                tracker++;
            }
        }.run();
    }

    /**
     * Registers a new {@link SpellHitEvent} to be called when a spell hits something.
     */
    public void register(final Player caster, final SpellHitEvent event, final double speed, final FireworkEffect effect) {
        new BukkitRunnable() {
            final Location location = caster.getEyeLocation();
            final Vector direction = location.getDirection().multiply(speed);
            boolean running = false;
            long tracker = 0;

            @Override
            public void run() {
                if (!running) {
                    runTaskTimer(instance, 0L, 1L);
                    running = true;
                }

                location.add(direction);
                try {
                    FireworkEffectPlayer.playFirework(location.getWorld(), location, effect);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!location.getBlock().getType().isTransparent()) {
                    event.block(location.getBlock());
                    cancel();
                    return;
                }

                List<LivingEntity> entities = getNearbyEntities(location, 2, caster);
                if (entities.size() != 0) {
                    event.entity(entities.get(0));
                    cancel();
                    return;
                }
                tracker++;
            }
        }.run();
    }

    /**
     * Registers a new {@link SpellHitEvent} to be called when a spell hits something.
     */
    public void register(final Player caster, final SpellHitEvent event, final double speed, final float offset, final int count, final Particle... effects) {
        new BukkitRunnable() {
            final Location location = caster.getEyeLocation();
            final Vector direction = location.getDirection().multiply(speed);
            boolean running = false;
            long tracker = 0;

            @Override
            public void run() {
                if (!running) {
                    runTaskTimer(instance, 0L, 1L);
                    running = true;
                }

                location.add(direction);
                try {
                    for (Particle particle : effects) {
                        if (particle.equals(Particle.REDSTONE)) {
                            Objects.requireNonNull(location.getWorld()).spawnParticle(
                                    particle,
                                    location,
                                    count,
                                    offset,
                                    offset,
                                    offset,
                                    speed,
                                    new Particle.DustOptions(Color.RED, 1)
                            );
                        } else
                            Objects.requireNonNull(location.getWorld()).spawnParticle(particle, location, count, offset, offset, offset, speed);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!location.getBlock().getType().isTransparent()) {
                    event.block(location.getBlock());
                    cancel();
                    return;
                }

                List<LivingEntity> entities = getNearbyEntities(location, 2, caster);
                if (entities.size() != 0) {
                    event.entity(entities.get(0));
                    cancel();
                    return;
                }

                tracker++;
            }
        }.run();
    }

    /**
     * Registers a new {@link SpellHitEvent} to be called when a spell hits something using the default offset and count.
     */
    public void register(final Player caster, final SpellHitEvent onHit, final double spellSpeed, final WrappedParticle particle) {
        register(caster, onHit, spellSpeed, 0.5f, 1, particle);
    }

    /**
     * Registers a new {@link SpellHitEvent} to be called when a spell hits something.
     */
    public void register(final Player caster, final SpellHitEvent onHit, final double spellSpeed, final double offset, final int count, final WrappedParticle... particles) {
        new BukkitRunnable() {
            final Location location = caster.getEyeLocation();
            final Vector direction = location.getDirection().multiply(spellSpeed);
            boolean running = false;
            long tracker = 0;

            @Override
            public void run() {
                if (!running) {
                    runTaskTimer(instance, 0L, 1L);
                    running = true;
                }

                location.add(direction);
                try {
                    for (WrappedParticle particle : particles) {
                        Particle bParticle = particle.getParticle();
                        if (bParticle == Particle.REDSTONE) {
                            Particle.DustOptions options = particle.getOptions() != null ? particle.getOptions() : new Particle.DustOptions(Color.RED, 1);
                            Objects.requireNonNull(location.getWorld()).spawnParticle(bParticle, location, count, offset, offset, offset, spellSpeed, options);
                        } else {
                            Objects.requireNonNull(location.getWorld()).spawnParticle(bParticle, location, count, offset, offset, offset, spellSpeed);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!location.getBlock().getType().isTransparent()) {
                    onHit.block(location.getBlock());
                    cancel();
                    return;
                }

                List<LivingEntity> list = getNearbyEntities(location, 2, caster);
                if (list.size() != 0) {
                    onHit.entity(list.get(0));
                    cancel();
                    return;
                }
                tracker++;
            }
        }.run();
    }

    /**
     * Gets a list of LivingEntity's near a location
     */
    public List<LivingEntity> getNearbyEntities(Location location, double distance, Entity... ignoreEntity) {
        List<LivingEntity> list = new ArrayList<>();
        List<Entity> ig = Arrays.asList(ignoreEntity);
        for (Entity en : location.getChunk().getEntities()) {
            if (!(en instanceof LivingEntity) || ig.contains(en))
                continue;
            if (en.getLocation().distance(location) <= distance)
                list.add((LivingEntity) en);
        }
        return list;
    }


    public interface SpellHitEvent {

        /**
         * Called when a spell hits a block.
         */
        void block(Block block);

        /**
         * Called when a spell hits an entity.
         */
        void entity(LivingEntity entity);
    }

}
