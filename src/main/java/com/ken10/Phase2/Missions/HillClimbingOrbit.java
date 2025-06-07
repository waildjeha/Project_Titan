package com.ken10.Phase2.Missions;

import com.ken10.Phase2.OptimizationAlgorithms.HillClimbing;
import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class HillClimbingOrbit {
    private Vector bestVector;
    private double totalError;
    private ArrayList<CelestialBodies> initialState;
    private LocalDateTime startTime;

    private final double step;
    private final double minStep;
    private final double enlargeFactor;
    private final double shrinkFactor;
    public static final double MU_TITAN_KM = 8.978e12 / 1.0e9;

    public HillClimbingOrbit(ArrayList<CelestialBodies> initialState, LocalDateTime startTime) {
        this.initialState = initialState;
        this.bestVector = circularVelocity(initialState);
        this.step    = 0.01;     // 10  m/s
        this.minStep = 1e-8;     // 1   mm/s
        this.enlargeFactor = 1.2;
        this.shrinkFactor = 0.5;
        this.totalError = Double.MAX_VALUE;
        this.startTime = startTime;
    }

    private Vector loadOrthogonalVector() {
        Vector acceleration = GravityCalc.computeAcceleration(this.initialState, BodyID.SPACESHIP.index());
        Vector orthogonal = acceleration.findOrthogonalVector();
        return orthogonal.normalize().multiply(acceleration.magnitude());
    }

    public Vector getBestVector() {
        return solve();
    }

    private Vector circularVelocity(ArrayList<CelestialBodies> state) {

        Vector titanPos = state.get(BodyID.TITAN.index()).getPosition();   // km
        Vector probePos = state.get(BodyID.SPACESHIP.index()).getPosition();// km

        Vector radius   = probePos.subtract(titanPos);   // km
        double  r       = radius.magnitude();            // km

        double vCirc = Math.sqrt(MU_TITAN_KM / r);   // km s-¹

        Vector tangential = radius.findOrthogonalVector()
                .normalize()
                .multiply(vCirc);     // km s-¹
        return tangential;
    }



    private Vector solve() {
        double step = this.step;
        double totalError = Double.MAX_VALUE;
        int    generations = 0;
        int    successStreak = 0;              // NEW

        while (totalError > 100 &&
                generations < 1_000_000 &&
                step        > minStep) {

            boolean improved = false;
            Vector  dir = bestVector.normalize();
            double  v   = bestVector.magnitude();

            // try two neighbours
            for (double dv : new double[]{ +step, -step }) {
                if (v + dv < 0) continue;      // speed cannot be negative
                Vector neighbour = dir.multiply(v + dv);

                ArrayList<CelestialBodies> trial = new ArrayList<>(initialState.size());
                for (CelestialBodies c : initialState) trial.add(c.deepCopy());
                trial.getLast().setVelocity(neighbour);

                double err = calculateError(trial);
                if (err < totalError) {
                    improved    = true;
                    totalError  = err;
                    bestVector  = neighbour;
                }
            }

            /* ------ adaptive step size ------ */
            if (improved) {
                successStreak++;
                if (successStreak >= 3) {      // enlarge only after 3 straight wins
                    step *= enlargeFactor;
                    successStreak = 0;
                }
            } else {
                successStreak = 0;
                step *= shrinkFactor;          // ALWAYS shrink on failure
            }
            /* -------------------------------- */

            System.out.printf("gen %4d  step %.5f km/s  err %.1f%n",
                    generations, step, totalError);

            generations++;
        }
        return bestVector;
    }


    private double calculateError(ArrayList<CelestialBodies> initialState) {
        double initialDistanceFromTitan = initialState.get(BodyID.TITAN.index()).getPosition().getDistance(initialState.get(BodyID.SPACESHIP.index()).getPosition());
        double totalError = Double.MAX_VALUE;
        EphemerisLoader eph = new EphemerisLoader(initialState, this.startTime, this.startTime.plusMinutes(10), 1, true);
        eph.solve();
        List<LocalDateTime> timeStates = eph.history.keySet().stream().sorted().toList();
        for (LocalDateTime t : timeStates) {
            double distance = eph.position(BodyID.TITAN, t).getDistance(eph.position(BodyID.SPACESHIP, t));
            if(distance < Titan.RADIUS){
                return Double.MAX_VALUE;
            }
            if(distance > Titan.RADIUS+300){
                totalError += distance-Titan.RADIUS-300;
            }
            if (distance < Titan.RADIUS+100) {
                totalError += distance-Titan.RADIUS-100;
            }
            totalError += Math.abs(initialDistanceFromTitan - eph.position(BodyID.SPACESHIP, t).getDistance(eph.position(BodyID.TITAN, t)));
        }
        if (totalError < this.totalError) {this.totalError = totalError; this.bestVector = initialState.get(BodyID.SPACESHIP.index()).getVelocity();}
        return totalError;
    }

    public static void main(String[] args) {
        VerifyOrbit v = new VerifyOrbit();
        Orbit o = v.orbit;
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history = o.historyOrbit;
        LocalDateTime startTime;
        for(LocalDateTime t : history.keySet()) {
            List<CelestialBodies> list = history.get(t);
            Vector posSpaceship = list.get(BodyID.SPACESHIP.index()).getPosition();
            Vector posTitan = list.get(BodyID.TITAN.index()).getPosition();
            double distance = posSpaceship.getDistance(posTitan);
            if (distance < Titan.RADIUS + 220 && distance > Titan.RADIUS + 180) {
                HillClimbingOrbit optimization = new HillClimbingOrbit(history.get(t), t);
                optimization.solve();
                break;
            }
        }



    }



}
