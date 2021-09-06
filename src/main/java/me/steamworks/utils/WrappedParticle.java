package me.steamworks.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;

@AllArgsConstructor
@Getter
@Setter
public class WrappedParticle {

    private Particle particle;
    private Particle.DustOptions options;

    public WrappedParticle(Particle particle) {
        this(particle, null);
    }

}
