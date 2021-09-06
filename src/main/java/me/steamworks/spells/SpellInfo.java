package me.steamworks.spells;


import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpellInfo {

    String name() default "";

    String description() default "";

    int range() default 25;

    int cooldown() default 60;

    boolean travelThroughWalls() default false;

    SpellType type() default SpellType.CHARM;

    Material icon() default Material.STICK;

}
