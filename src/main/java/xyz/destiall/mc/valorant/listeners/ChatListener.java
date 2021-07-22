package xyz.destiall.mc.valorant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Settings;
import xyz.destiall.mc.valorant.managers.MatchManager;

import java.util.HashSet;
import java.util.Set;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Participant participant = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (participant == null) return;
        e.setCancelled(true);
        final Set<Participant> recipients = new HashSet<>();
        String setting = "[TEAM]";
        if (participant.getChatSettings() == Settings.Chat.GLOBAL) {
            setting = "[ALL]";
            recipients.addAll(participant.getMatch().getPlayers().values());
        } else {
            recipients.addAll(participant.getTeam().getMembers());
        }
        for (Participant p : recipients) {
            ChatColor color = ChatColor.BLUE;
            if (p.getTeam() != participant.getTeam()) {
                color = ChatColor.RED;
            }
            p.sendMessage(color + setting + " " + participant.getPlayer().getName() + ": " + e.getMessage());
        }
    }
}
