package xyz.destiall.mc.valorant;

public class Valorant {
    private static Valorant instance;
    public Valorant() {
        instance = this;
    }

    public static Valorant getInstance() {
        return instance;
    }
}
