package com.ken10.Phase2.Missions;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class GoldenSectionOrbit {

    private static final double PHI = 0.5 * (Math.sqrt(5.0) - 1.0);      // ≈0.618

    private final ArrayList<CelestialBodies> state0;   // deep-copied once
    private final LocalDateTime t0;
    private final Vector tangential;                   // unit vector û
    private double totalError = Double.POSITIVE_INFINITY;
    private Vector bestVector;

    /* ---------- constructor ---------- */
    public GoldenSectionOrbit(ArrayList<CelestialBodies> snapshot,
                              LocalDateTime epoch) {
        this.state0 = new ArrayList<>(snapshot.size());
        for (CelestialBodies c : snapshot) state0.add(c.deepCopy());
        this.t0 = epoch;

        Vector g = GravityCalc.computeAcceleration(snapshot,
                BodyID.SPACESHIP.index());
        this.tangential = g.findOrthogonalVector().normalize();           // û
    }

    /* ---------- search ---------- */
    public Vector findCircularVelocity() {
        double vLo = 0.0;      // km s-1
        double vHi = 4.0;      // km s-1  (> Titan escape)

        double v1 = vHi - PHI * (vHi - vLo);
        double v2 = vLo + PHI * (vHi - vLo);

        double f1 = errorAtSpeed(v1);
        double f2 = errorAtSpeed(v2);

        int iter = 0;
        while (vHi - vLo > 1e-4 && iter++ < 100) {    // 1 cm s-1 tolerance
            if (f1 < f2) {                 // keep [vLo , v2]
                vHi = v2;  v2 = v1;  f2 = f1;
                v1 = vHi - PHI * (vHi - vLo);
                f1 = errorAtSpeed(v1);
            } else {                        // keep [v1 , vHi]
                vLo = v1;  v1 = v2;  f1 = f2;
                v2 = vLo + PHI * (vHi - vLo);
                f2 = errorAtSpeed(v2);
            }
            System.out.printf("it %2d  vLo %.4f  vHi %.4f  bestErr %.1f%n",
                    iter, vLo, vHi, Math.min(f1, f2));
        }
        double vBest = 0.5 * (vLo + vHi);
        return tangential.multiply(vBest);            // km s-1 vector
    }

    /* ---------- helpers ---------- */
    private double errorAtSpeed(double v) {
        ArrayList<CelestialBodies> trial = new ArrayList<>(state0.size());
        for (CelestialBodies c : state0) trial.add(c.deepCopy());

        trial.get(BodyID.SPACESHIP.index())
                .setVelocity(tangential.multiply(v));

        return calculateError(trial);
    }

    private double calculateError(ArrayList<CelestialBodies> initialState) {
        double initDist = initialState.get(BodyID.TITAN.index())
                .getPosition()
                .getDistance(
                        initialState.get(
                                        BodyID.SPACESHIP.index())
                                .getPosition());

        double err = 0.0;
        EphemerisLoader eph = new EphemerisLoader(initialState,
                t0,
                t0.plusMinutes(10),
                1, true);
        eph.solve();

        for (LocalDateTime t : eph.history.keySet().stream().sorted().toList()) {
            double d = eph.position(BodyID.TITAN,     t)
                    .getDistance(
                            eph.position(BodyID.SPACESHIP, t));

            if (d < Titan.RADIUS)            return Double.MAX_VALUE;      // crash
            if (d > Titan.RADIUS + 300)      err += d - Titan.RADIUS - 300;
            if (d < Titan.RADIUS + 100)      err += Titan.RADIUS + 100 - d;
            err += Math.abs(initDist - d);                                  // drift
        }
        if (err < totalError) { totalError = err; bestVector = tangential; }
        return err;
    }

    /* ---------- MAIN ---------- */
    public static void main(String[] args) {

        VerifyOrbit v = new VerifyOrbit();
        Orbit       o = v.orbit;
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history = o.historyOrbit;

        for (LocalDateTime t : history.keySet()) {
            ArrayList<CelestialBodies> snapshot = history.get(t);

            Vector posShip = snapshot.get(BodyID.SPACESHIP.index()).getPosition();
            Vector posTitan = snapshot.get(BodyID.TITAN.index()).getPosition();
            double distance = posShip.getDistance(posTitan);

            if (distance < Titan.RADIUS + 220 && distance > Titan.RADIUS + 180) {
                GoldenSectionOrbit optimiser = new GoldenSectionOrbit(snapshot, t);
                Vector bestVel = optimiser.findCircularVelocity();

                System.out.println("\nOptimal tangential velocity (km s-1): "
                        + bestVel);
                break;
            }
        }
    }
}
