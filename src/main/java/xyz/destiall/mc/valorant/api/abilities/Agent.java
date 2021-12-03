package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.Color;
import org.bukkit.Material;

public enum Agent {
    JETT(Color.WHITE),
    REYNA(Color.PURPLE),
    SOVA(Color.NAVY),
    CYPHER(Color.SILVER),
    SAGE(Color.AQUA),
    PHOENIX(Color.ORANGE),
    KILLJOY(Color.YELLOW),
    SKYE(Color.LIME),
    VIPER(Color.GREEN),
    BRIMSTONE(Color.ORANGE),
    OMEN(Color.BLACK),
    CHAMBERLAIN(Color.YELLOW);

    public final Color COLOR;
    public final Material WOOL;
    Agent(Color color) {
        this.COLOR = color;
        if (color.toString().equals(Color.AQUA.toString())) {
            WOOL = Material.CYAN_WOOL;
        } else if (color.toString().equals(Color.PURPLE.toString())) {
            WOOL = Material.PURPLE_WOOL;
        } else if (color.toString().equals(Color.BLACK.toString())) {
            WOOL = Material.BLACK_WOOL;
        } else if (color.toString().equals(Color.ORANGE.toString())) {
            WOOL = Material.ORANGE_WOOL;
        } else if (color.toString().equals(Color.LIME.toString())) {
            WOOL = Material.LIME_WOOL;
        } else if (color.toString().equals(Color.BLUE.toString())) {
            WOOL = Material.BLUE_WOOL;
        } else if (color.toString().equals(Color.GREEN.toString())) {
            WOOL = Material.GREEN_WOOL;
        } else if (color.toString().equals(Color.GRAY.toString())) {
            WOOL = Material.GRAY_WOOL;
        } else if (color.toString().equals(Color.SILVER.toString())) {
            WOOL = Material.LIGHT_GRAY_WOOL;
        } else if (color.toString().equals(Color.RED.toString())) {
            WOOL = Material.RED_WOOL;
        } else if (color.toString().equals(Color.YELLOW.toString())) {
            WOOL = Material.YELLOW_WOOL;
        } else if (color.toString().equals(Color.MAROON.toString())) {
            WOOL = Material.RED_WOOL;
        } else if (color.toString().equals(Color.TEAL.toString())) {
            WOOL = Material.CYAN_WOOL;
        } else {
            WOOL = Material.WHITE_WOOL;
        }
    }
}
