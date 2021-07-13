package xyz.destiall.mc.valorant.api.abilities;

import java.time.Duration;

public interface Molly {
    Duration getMollyDuration();
    void updateMolly();
    int getMollyRange();
    Duration getMollyLastingDuration();
}
