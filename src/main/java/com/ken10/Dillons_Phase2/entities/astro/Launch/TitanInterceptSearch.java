package com.ken10.Dillons_Phase2.entities.astro.Launch;

import com.ken10.Dillons_Phase2.entities.astro.Emhemeris.EphemerisLoader;
import com.ken10.Dillons_Phase2.entities.NumericalSolvers.RK4Solver;
import com.ken10.Dillons_Phase2.entities.planet.*;
import com.ken10.Dillons_Phase2.entities.probe.Probe;
import com.ken10.Dillons_Phase2.entities.Other.Vector;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Pure-JDK search for a 60 km/s heliocentric velocity that minimises
 * miss-distance to Titan after a 312-day flight.
 *
 *    – biased random sampling cone  (±30°) around Earth-prograde
 *    – simple hill-climb refinement
 *    – no external libraries
 *
 * Compile & run exactly like your other source files.
 */
public final class TitanInterceptSearch {

    /* ───────────────────── user knobs ────────────────────────────── */
    private static final LocalDateTime T0        = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    private static final int           FLT_DAYS  = 312;
    private static final double        V_LAUNCH  = 60.0;     // km s-1
    private static final int           TAPESTEP_H= 2;        // 6-h planet tape
    private static final int           PROBSTEP_H= 1;        // 1-h probe step
    private static final int           RANDOM_IN_CONE = 20000;
    private static final int           RANDOM_GLOBAL  =  800;
    private static final int           HILL_PASSES    = 4;   // refinement depth
    /* increase RANDOM_IN_CONE to 10_000+ for better accuracy          */

    /* ───────────────────── infrastructure ─────────────────────────── */
    private final EphemerisLoader eph;
    private final Map<LocalDateTime, ArrayList<CelestialBodies>> tape;
    private final Random rng = new Random(123);

    private TitanInterceptSearch() {
        eph   = new EphemerisLoader(SolarSystem.CreatePlanets(), T0, T0.plusYears(1), TAPESTEP_H);
        eph.solve();// planet-only run
        double AU = 1.496e8;
        System.out.printf("Titan–Earth @T₀ : %.3f AU%n",
                Vector.getDistance(eph.initialState.get(BodyID.TITAN.index()).getPosition(),
                        eph.initialState.get(BodyID.EARTH.index()).getPosition())/AU
        );
        tape  = eph.history;
    }

    /* unit direction from (θ,φ) radians in inertial axes */
    private static Vector unit(double theta, double phi) {
        double ct = Math.cos(theta), st = Math.sin(theta);
        double cp = Math.cos(phi)  , sp = Math.sin(phi);
        return new Vector(ct*cp, ct*sp, st);
    }

    /* heliocentric miss distance at arrival for direction û(θ,φ) */
    private double miss(double theta, double phi) {

        Vector rE0 = eph.position(BodyID.EARTH, T0);
        Vector uPad= rE0.normalize();
        Vector r0  = rE0.add(uPad.multiply(6370));                // km

        Vector v0  = unit(theta,phi).multiply(V_LAUNCH);          // km/s
        Probe probe= new Probe("probe", r0, v0);

        ArrayList<CelestialBodies> state0 = new ArrayList<>(eph.initialState);
        state0.add(probe);

        RK4Solver solver = new RK4Solver(
                state0, T0, T0.plusDays(FLT_DAYS), PROBSTEP_H);
        solver.solve();

        LocalDateTime t1 = T0.plusDays(FLT_DAYS);
        Vector rP = solver.history.get(t1).stream()
                .filter(b -> b.getName().equals("probe"))
                .findFirst().orElseThrow().getPosition();
        CelestialBodies titan = tape.get(t1).get(BodyID.TITAN.index());
        //System.out.println("Confirmation: " + titan.getName());
        Vector rT = titan.getPosition();

        return Vector.getDistance(rP, rT);
    }

    /* ───────────────────── biased random directions ───────────────── */
    private Vector earthProgradeUnit() {
        Vector vE = eph.velocity(BodyID.EARTH, T0);
        return vE.normalize();
    }

    /** random unit vector inside a cone of half-angle α about axis ê */
    private Vector randomInCone(Vector axis, double alphaRad) {
        double cosAlpha = Math.cos(alphaRad);
        double z = 1 - rng.nextDouble() * (1 - cosAlpha); // uniform in cos
        double sin = Math.sqrt(1 - z*z);
        double phi = 2 * Math.PI * rng.nextDouble();
        double x = sin * Math.cos(phi);
        double y = sin * Math.sin(phi);

        /* build orthonormal basis {axis, u, v} */
        Vector a = axis;
        Vector u = (Math.abs(a.getX()) < 0.5)
                ? new Vector(1,0,0).cross(a).normalize()
                : new Vector(0,1,0).cross(a).normalize();
        Vector v = a.cross(u);

        return a.multiply(z).add(u.multiply(x)).add(v.multiply(y)).normalize();
    }

    /* ───────────────────── optimisation driver ────────────────────── */
    private void optimise() {

        Vector prograde = earthProgradeUnit();

        /* seed: straight prograde */
        double thetaBest = Math.asin(prograde.getZ());
        double phiBest   = Math.atan2(prograde.getY(), prograde.getX());
        double missBest  = miss(thetaBest, phiBest);
        System.out.printf("seed miss %.0f km%n", missBest);

        /* 1 ─ random shots inside ±30° cone */
        double cone = Math.toRadians(60);
        for (int i = 1; i <= RANDOM_IN_CONE; i++) {
            Vector u = randomInCone(prograde, cone);
            double t = Math.asin(u.getZ());
            double p = Math.atan2(u.getY(), u.getX());
            double m = miss(t, p);
            if (m < missBest) { missBest = m; thetaBest=t; phiBest=p; }
            if (i % 500 == 0) System.out.printf("  %d / %d   best %.0f km%n",
                    i, RANDOM_IN_CONE, missBest);
        }

        /* 2 ─ a few global random shots (entire sphere) */
        for (int i = 0; i < RANDOM_GLOBAL; i++) {
            double t = Math.asin(2*rng.nextDouble()-1);
            double p = 2*Math.PI*rng.nextDouble();
            double m = miss(t,p);
            if (m < missBest) { missBest = m; thetaBest=t; phiBest=p; }
        }

        /* 3 ─ hill-climb, shrinking step */
        double step = Math.toRadians(3);
        for (int pass = 0; pass < HILL_PASSES; pass++) {
            boolean improved;
            do {
                improved = false;
                for (int dt = -1; dt<=1; dt++)
                    for (int dp = -1; dp<=1; dp++) {
                        if (dt==0 && dp==0) continue;
                        double t = thetaBest + dt*step;
                        double p = phiBest   + dp*step;
                        double m = miss(t,p);
                        if (m < missBest) {
                            missBest=m; thetaBest=t; phiBest=p; improved=true;
                        }
                    }
            } while (improved);
            step *= 0.5;
        }

        /* 4 ─ result */
        Vector v0 = unit(thetaBest,phiBest).multiply(V_LAUNCH);
        System.out.println("---------------------------------------------");
        System.out.printf ("Miss distance : %.0f km%n", missBest);
        System.out.printf ("Angles θ,φ    : %.2f°, %.2f°%n",
                Math.toDegrees(thetaBest), Math.toDegrees(phiBest));
        System.out.println  ("Launch v0     : " + v0 + "  km/s");
    }

    /* ───────────────────── main ───────────────────────────────────── */
    public static void main(String[] args) {
        new TitanInterceptSearch().optimise();
    }
}
