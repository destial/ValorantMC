package xyz.destiall.mc.valorant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.destiall.mc.valorant.api.player.Settings;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;

import java.util.HashSet;
import java.util.Set;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        e.getRecipients().removeIf(p -> MatchManager.getInstance().getParticipant(p) != null);
        VPlayer vPlayer = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (vPlayer == null) return;
        e.setCancelled(true);
        final Set<VPlayer> recipients = new HashSet<>();
        String setting = "[TEAM]";
        if (vPlayer.getChatSettings() == Settings.Chat.GLOBAL) {
            setting = "[ALL]";
            recipients.addAll(vPlayer.getMatch().getPlayers().values());
        } else {
            recipients.addAll(vPlayer.getTeam().getMembers());
        }
        for (VPlayer p : recipients) {
            ChatColor color = ChatColor.AQUA;
            if (p.getTeam() != vPlayer.getTeam()) {
                color = ChatColor.RED;
            }
            p.sendMessage(color + setting + " " + vPlayer.getPlayer().getName() + ": " + e.getMessage());
        }
    }
}
