package me.steamworks.wand;

import lombok.Getter;
import lombok.Setter;
import me.steamworks.SpellPlugin;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WandLore {

    private final SpellPlugin INSTANCE = SpellPlugin.getInstance();

    private
    String spell = "None",
            wood = "Unknown",
            manufacturer = "Unknown",
            core = "Unknown",
            rigidity = "Unknown";

    private
    int rarity = 0,
            length = 13;

    public String getManufacturer() {
        return manufacturer.replace('_', ' ').trim();
    }

    public String getCore() {
        return core.replace('_', ' ').trim();
    }

    public String rarityAsString() {
        switch (rarity) {
            case 1:
                return "Legendary";
            case 2:
                return "Very Rare";
            case 3:
                return "Rare";
            case 4:
                return "Uncommon";
            case 5:
                return "Common";
            default:
                return "Unknown";
        }
    }

    public List<String> generate() {
        List<String> format = INSTANCE.getConfig().getStringList("wand.lore.format");
        List<String> lore = new ArrayList<>();

        for (String line : format) {
            line = line.replace("{spell}", this.getSpell());
            line = line.replace("{length}", String.valueOf(this.getLength()));
            line = line.replace("{core}", this.getCore());
            line = line.replace("{wood}", this.getWood());
            line = line.replace("{rarity}", this.rarityAsString());
            line = line.replace("{rigidity}", this.getRigidity());
            line = line.replace("{manufacturer}", this.getManufacturer());
            line = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', line);

            lore.add(line);
        }

        return lore;
    }
}
