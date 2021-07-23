package xyz.destiall.mc.valorant.api.player;

public class Settings {
    private Chat chat;
    public Settings() {
        chat = Chat.TEAM;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public enum Chat {
        TEAM,
        GLOBAL,
    }
}
