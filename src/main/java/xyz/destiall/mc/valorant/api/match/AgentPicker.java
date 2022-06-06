package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class AgentPicker implements Listener, Module {
    private final Match match;
    private final Inventory inventory;
    private final HashSet<UUID> viewers;
    private final HashMap<UUID, Integer> choices;
    private final HashSet<UUID> lockedIn;
    private final HashMap<Team.Side, List<Integer>> slots;

    public AgentPicker(Match match) {
        this.match = match;
        inventory = Bukkit.createInventory(null, 54, "Pick your Agent");
        viewers = new HashSet<>();
        choices = new HashMap<>();
        lockedIn = new HashSet<>();
        slots = new HashMap<>();
        slots.put(Team.Side.DEFENDER, new ArrayList<>());
        slots.put(Team.Side.ATTACKER, new ArrayList<>());
        setup();
    }

    public void setup() {
        int original = 0;
        int i = original;
        Agent[] agents = new Agent[] { Agent.JETT, Agent.REYNA, Agent.PHOENIX, Agent.OMEN, Agent.CYPHER }; //Agent.values();
        for (Agent agent : agents) {
            if (i >= inventory.getSize()) {
                i = ++original;
            }
            ItemStack stack = agent.getHead();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + agent.getName());
            stack.setItemMeta(meta);
            inventory.setItem(i, stack);
            slots.get(Team.Side.DEFENDER).add(i);
            i += 9;
        }
        original = 8;
        i = original;
        for (Agent agent : agents) {
            if (i >= inventory.getSize()) {
                i = --original;
            }
            ItemStack stack = agent.getHead();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + agent.getName());
            stack.setItemMeta(meta);
            inventory.setItem(i, stack);
            slots.get(Team.Side.ATTACKER).add(i);
            i += 9;
        }

        ItemStack lock = new ItemStack(Material.REDSTONE);
        ItemMeta meta = lock.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Lock In");
        lock.setItemMeta(meta);
        inventory.setItem(inventory.getSize() - 4, lock);

        ItemStack start = new ItemStack(Material.STONE_BUTTON);
        ItemMeta startMeta = start.getItemMeta();
        startMeta.setDisplayName(ChatColor.RED + "Start");
        start.setItemMeta(startMeta);
        inventory.setItem(inventory.getSize() - 5, start);

        ItemStack dodge = new ItemStack(Material.OAK_DOOR);
        ItemMeta dodgeMeta = dodge.getItemMeta();
        dodgeMeta.setDisplayName(ChatColor.RED + "Dodge");
        dodge.setItemMeta(dodgeMeta);
        inventory.setItem(inventory.getSize() - 6, dodge);
    }

    public void show(VPlayer player) {
        player.getPlayer().openInventory(inventory);
        viewers.add(player.getUUID());
    }

    public Match getMatch() {
        return match;
    }

    @EventHandler
    public void onQuitInventory(InventoryCloseEvent e) {
        if (match.getState() != Match.MatchState.PLAYING) {
            if (viewers.contains(e.getPlayer().getUniqueId())) {
                Scheduler.delay(() -> e.getPlayer().openInventory(inventory), 1L);
            }
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        if (viewers.contains(uuid)) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (item == null) return;
            VPlayer p = match.getPlayer(uuid);
            if (item.getType() == Material.REDSTONE) {
                if (lockedIn.contains(uuid)) return;
                if (p.getAgent() == null) {
                    p.sendMessage("&cYou have not selected an agent!");
                    return;
                }
                lockedIn.add(uuid);
                p.sendMessage("&cYou have locked in: " + p.getAgent().name());
                p.chooseAgent(p.getAgent());
                return;
            } else if (item.getType() == Material.STONE_BUTTON) {
                if (p.getPlayer().hasPermission("valorant.admin")) {
                    Collection<VPlayer> list = match.getPlayers().values();
                    for (VPlayer player : list) {
                        player.chooseAgent(player.getAgent());
                    }
                    match.start(true);
                } else {
                    p.sendMessage("&cYou do not have permission to force start!");
                }
                return;
            } else if (item.getType() == Material.OAK_DOOR) {
                if (choices.get(uuid) != null && p.getAgent() != null) {
                    Agent prevAgent = p.getAgent();
                    Integer slot = choices.get(uuid);
                    ItemStack prevItem = prevAgent.getHead();
                    ItemMeta meta = prevItem.getItemMeta();
                    meta.setDisplayName(ChatColor.RESET + prevAgent.getName());
                    prevItem.setItemMeta(meta);
                    inventory.setItem(slot, prevItem);
                }
                viewers.remove(p.getUUID());
                choices.remove(p.getUUID());
                lockedIn.remove(p.getUUID());
                p.leave(false);
                return;
            } else if (item.getType() == Material.BARRIER) {
                return;
            }

            if (lockedIn.contains(uuid)) {
                p.sendMessage("&cYou have already locked in: " + p.getAgent().name());
                return;
            }
            int woolSlot = e.getSlot();
            if (p.getTeam().getSide() == Team.Side.DEFENDER) {
                if (slots.get(Team.Side.ATTACKER).contains(woolSlot)) {
                    p.sendMessage("&cWrong side to pick!");
                    return;
                }
            }
            if (p.getTeam().getSide() == Team.Side.ATTACKER) {
                if (slots.get(Team.Side.DEFENDER).contains(woolSlot)) {
                    p.sendMessage("&cWrong side to pick!");
                    return;
                }
            }
            String display = item.getItemMeta().getDisplayName();
            p.sendMessage("&bYou picked: " + display);
            if (choices.get(p.getUUID()) != null && p.getAgent() != null) {
                Agent prevAgent = p.getAgent();
                Integer slot = choices.get(uuid);
                ItemStack prevItem = prevAgent.getHead();
                ItemMeta meta = prevItem.getItemMeta();
                meta.setDisplayName(ChatColor.RESET + prevAgent.getName());
                prevItem.setItemMeta(meta);
                inventory.setItem(slot, prevItem);
            }
            p.setAgent(Agent.valueOf(ChatColor.stripColor(display.toUpperCase())));
            choices.put(uuid, e.getSlot());
            item.setType(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + p.getAgent().getName() + ChatColor.RED + " (" + p.getPlayer().getName() + ")");
            item.setItemMeta(meta);
        }
    }

    @Override
    public void destroy() {
        viewers.clear();
        choices.clear();
        lockedIn.clear();
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer p : list) {
            p.getPlayer().closeInventory();
        }
        inventory.clear();
    }
}
