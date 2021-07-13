package xyz.destiall.mc.valorant.utils;

import org.bukkit.Location;
import org.bukkit.Particle;

public class Particles {
    public static void smoke(Location location, Type type) {
        for (double i = 0; i <= Math.PI; i += Math.PI / 20) {
            double radius = Math.sin(i);
            double y = Math.cos(i);
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 20) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                location.add(x, y, z);
                location.subtract(x, y, z);
            }
        }
    }
    public enum Type {
        JETT(Particle.DUST_COLOR_TRANSITION);
        Particle particle;
        Type(Particle particle) {
            this.particle = particle;
        }
    }
}
