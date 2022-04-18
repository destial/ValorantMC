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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class AgentPicker implements Listener, Module {
    private final Match match;
    private final Inventory inventory;
    private final HashSet<UUID> viewers;
    private final HashMap<UUID, Integer> choices;
    private final HashSet<UUID> lockedIn;

    public AgentPicker(Match match) {
        this.match = match;
        inventory = Bukkit.createInventory(null, 36, "Pick your Agent");
        viewers = new HashSet<>();
        choices = new HashMap<>();
        lockedIn = new HashSet<>();
        setup();
    }

    @SuppressWarnings("all")
    public void setup() {
        int i = 0;
        for (Agent agent : Agent.values()) {
            if (i >= 36) {
                i = 1;
                continue;
            }
            ItemStack stack = new ItemStack(agent.WOOL);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + agent.name());
            stack.setItemMeta(meta);
            inventory.setItem(i, stack);
            i += 9;
        }

        i = 7;
        for (Agent agent : Agent.values()) {
            if (i >= 36) {
                i = 8;
                continue;
            }
            ItemStack stack = new ItemStack(agent.WOOL);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + agent.name());
            stack.setItemMeta(meta);
            inventory.setItem(i, stack);
            i += 9;
        }

        ItemStack lock = new ItemStack(Material.REDSTONE);
        ItemMeta meta = lock.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Lock In");
        lock.setItemMeta(meta);
        inventory.setItem(31, lock);

        ItemStack start = new ItemStack(Material.STONE_BUTTON);
        ItemMeta startMeta = start.getItemMeta();
        startMeta.setDisplayName(ChatColor.RED + "Start");
        start.setItemMeta(startMeta);
        inventory.setItem(32, start);

        ItemStack dodge = new ItemStack(Material.OAK_DOOR);
        ItemMeta dodgeMeta = dodge.getItemMeta();
        dodgeMeta.setDisplayName(ChatColor.RED + "Dodge");
        dodge.setItemMeta(dodgeMeta);
        inventory.setItem(30, dodge);
    }

    public void show(VPlayer player) {
        if (viewers.contains(player.getUUID())) return;
        player.getPlayer().openInventory(inventory);
        viewers.add(player.getUUID());
    }

    public Match getMatch() {
        return match;
    }

    @EventHandler
    public void onQuitInventory(InventoryCloseEvent e) {
        if (!match.getState().equals(Match.MatchState.PLAYING)) {
            if (viewers.contains(e.getPlayer().getUniqueId())) {
                Scheduler.delay(() -> e.getPlayer().openInventory(inventory), 1L);
            }
        }
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        if (viewers.contains(uuid)) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (item == null) return;
            VPlayer p = match.getPlayer(uuid);
            if (!isWool(item.getType())) {
                if (item.getType().equals(Material.REDSTONE)) {
                    if (lockedIn.contains(uuid)) return;
                    if (p.getAgent() == null) {
                        p.sendMessage("&cYou have not selected an agent!");
                        return;
                    }
                    lockedIn.add(uuid);
                    p.sendMessage("&cYou have locked in: " + p.getAgent().name());
                    p.chooseAgent(p.getAgent());
                } else if (item.getType().equals(Material.STONE_BUTTON)) {
                    Collection<VPlayer> list = match.getPlayers().values();
                    for (VPlayer player : list) {
                        player.chooseAgent(player.getAgent());
                    }
                    match.start(true);
                } else if (item.getType().equals(Material.OAK_DOOR)) {
                    if (choices.get(p.getUUID()) != null && p.getAgent() != null) {
                        Agent prevAgent = p.getAgent();
                        Integer slot = choices.get(uuid);
                        ItemStack prevItem = inventory.getItem(slot);
                        prevItem.setType(prevAgent.WOOL);
                        ItemMeta meta = prevItem.getItemMeta();
                        meta.setDisplayName(ChatColor.RESET + prevAgent.name());
                        prevItem.setItemMeta(meta);
                    }
                    viewers.remove(p.getUUID());
                    choices.remove(p.getUUID());
                    lockedIn.remove(p.getUUID());
                    p.leave();
                }
                return;
            }
            if (lockedIn.contains(uuid)) {
                p.sendMessage("&cYou have already locked in: " + p.getAgent().name());
                return;
            }
            int woolSlot = e.getSlot();
            if (woolSlot == 0 || woolSlot == 1 || woolSlot == 9 || woolSlot == 10 || woolSlot == 18 || woolSlot == 27 || woolSlot == 28) {
                if (p.getTeam().getSide().equals(Team.Side.DEFENDER)) {
                    p.sendMessage("&cWrong side to pick!");
                    return;
                }
            }
            if (woolSlot == 7 || woolSlot == 8 || woolSlot == 16 || woolSlot == 17 || woolSlot == 25 || woolSlot == 26 || woolSlot == 34 || woolSlot == 35) {
                if (p.getTeam().getSide().equals(Team.Side.ATTACKER)) {
                    p.sendMessage("&cWrong side to pick!");
                    return;
                }
            }
            String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            p.sendMessage("&bYou picked: " + display);
            if (choices.get(p.getUUID()) != null && p.getAgent() != null) {
                Agent prevAgent = p.getAgent();
                Integer slot = choices.get(uuid);
                ItemStack prevItem = inventory.getItem(slot);
                prevItem.setType(prevAgent.WOOL);
                ItemMeta meta = prevItem.getItemMeta();
                meta.setDisplayName(ChatColor.RESET + prevAgent.name());
                prevItem.setItemMeta(meta);
            }
            p.setAgent(Agent.valueOf(display));
            choices.put(uuid, e.getSlot());
            item.setType(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "TAKEN BY " + p.getPlayer().getName());
            item.setItemMeta(meta);
        }
    }

    private boolean isWool(Material wool) {
        return wool.name().contains("WOOL");
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
