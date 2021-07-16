package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.Color;
import org.bukkit.Material;

public enum Agent {
    JETT(Color.WHITE),
    REYNA(Color.PURPLE),
    SOVA(Color.NAVY),
    CYPHER(Color.BLUE),
    SAGE(Color.AQUA),
    PHOENIX(Color.ORANGE),
    KILLJOY(Color.YELLOW),
    SKYE(Color.LIME),
    VIPER(Color.GREEN),
    BRIMSTONE(Color.ORANGE),
    OMEN(Color.BLACK);

    public final Color color;
    public final Material wool;
    Agent(Color color) {
        this.color = color;
        if (color.toString().equals(Color.AQUA.toString())) {
            wool = Material.CYAN_WOOL;
        } else if (color.toString().equals(Color.PURPLE.toString())) {
            wool = Material.PURPLE_WOOL;
        } else if (color.toString().equals(Color.BLACK.toString())) {
            wool = Material.BLACK_WOOL;
        } else if (color.toString().equals(Color.ORANGE.toString())) {
            wool = Material.ORANGE_WOOL;
        } else if (color.toString().equals(Color.LIME.toString())) {
            wool = Material.LIME_WOOL;
        } else if (color.toString().equals(Color.BLUE.toString())) {
            wool = Material.BLUE_WOOL;
        } else if (color.toString().equals(Color.GREEN.toString())) {
            wool = Material.GREEN_WOOL;
        } else {
            wool = Material.WHITE_WOOL;
        }
    }
}
