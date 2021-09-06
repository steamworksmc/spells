package me.steamworks.utils;

import me.steamworks.SpellPlugin;

import java.util.Map;

public class Utilities {

    public static int inBetween(int min, int max) {
        return SpellPlugin.getRandom().nextInt(max - min + 1) + min;
    }

    public static String getStringFromProbability(Map<String, Object> probabilities) {
        double probability = Math.random(),
                cumulativeProbability = 0.0;

        for (Map.Entry<String, Object> entry : probabilities.entrySet()) {
            cumulativeProbability += (Double) entry.getValue();

            if (probability <= cumulativeProbability)
                return entry.getKey();
        }
        return "Invalid Probability";
    }

}
