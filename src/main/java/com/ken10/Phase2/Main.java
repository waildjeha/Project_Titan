package com.ken10.Phase2;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        EphemerisLoader eph = new EphemerisLoader(2);
        eph.solve();
        LocalDateTime t0 = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        for (int i = 0; i < 365; i++) {
            eph.printState(eph.history.get(t0.plusDays(i)), t0.plusDays(i));
        }
    }
}
