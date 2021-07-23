package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.player.Participant;

public class AgentPicker implements Listener, Module {
    private final Match match;
    private final Inventory inventory;
    public AgentPicker(Match match) {
        this.match = match;
        inventory = Bukkit.createInventory(null, 36, "Pick your Agent");
        setup();
    }

    public void setup() {
        int i = 2;
        for (Agent agent : Agent.values()) {
            ItemStack stack = new ItemStack(agent.WOOL);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(agent.name());
            stack.setItemMeta(meta);
            inventory.setItem(i, stack);
            if (i > 36) {
                i = 3;
                continue;
            }
            i += 9;
        }
        i = 7;
        for (Agent agent : Agent.values()) {
            ItemStack stack = new ItemStack(agent.WOOL);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(agent.name());
            stack.setItemMeta(meta);
            inventory.setItem(i, stack);
            if (i > 36) {
                i = 8;
                continue;
            }
            i += 9;
        }
    }

    public Match getMatch() {
        return match;
    }

    public void close() {
        for (Participant p : match.getPlayers().values()) {
            p.getPlayer().closeInventory();
        }
    }

    @EventHandler
    public void onQuitInventory(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.getPlayer().openInventory(inventory);
        }
    }
}
