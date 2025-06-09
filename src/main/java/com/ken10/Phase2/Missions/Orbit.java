package com.ken10.Phase2.Missions;


import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Orbit extends ToTitan{

    protected Orbit (){
    solve();
    }

    private void solve(){
        var t0 = getLastDateTime();
        System.out.println("Probe is just ahead of orbit, time " + t0);
        var t1 = getLastDateTime().plusMinutes(historyEphemeris.getStepSize());
        EphemerisLoader eph = new EphemerisLoader(getLastState(),t0,t1,1 ,true);
        eph.solve();
        double distance = eph.position(BodyID.SPACESHIP, t0).getDistance(eph.position(BodyID.TITAN, t0));
        double altitude = distance;
        LocalDateTime t_orbit_goal = t0;
        ArrayList<CelestialBodies> state = new ArrayList<>();
        for(var time = t0.plusSeconds(1); (time.isBefore(t1)||time.isEqual(t1)); time = time.plusSeconds(1)){
            var tmpState = eph.history.get(time);
            Vector posSpaceship = eph.position(BodyID.SPACESHIP, time);
            Vector posTitan = eph.position(BodyID.TITAN, time);
            distance = posSpaceship.getDistance(posTitan);
            if(distance<(200+R_TITAN)) break;
            historyEphemeris.putHistory(time, tmpState);
            state = tmpState;
            altitude = distance;
            t_orbit_goal = time;
        }
        System.out.println("Probe reached desired altitude of " + (distance-R_TITAN) + "km above Titan surface, time " + t_orbit_goal);
        Vector desiredVelocity = findCircularOrbit(state, altitude-R_TITAN, t_orbit_goal);
        runSimulation(state, desiredVelocity, t_orbit_goal); //populate history
        evaluateFuel(state, desiredVelocity);
//        System.out.println("Desired velocity: " + desiredVelocity);
    }

    private Vector findCircularOrbit(ArrayList<CelestialBodies> state, double altitude, LocalDateTime t) {
        final double r_orbit = R_TITAN + altitude;  // Target orbit radius (km)

        var probe = state.get(BodyID.SPACESHIP.index());
        var titan = state.get(BodyID.TITAN.index());

        // Get state vectors (in Sun frame)
        Vector probePosSun = probe.getPosition();
        Vector probeVelSun = probe.getVelocity();
        Vector titanPosSun = titan.getPosition();
        Vector titanVelSun = titan.getVelocity();

        // Convert to Titan-relative frame
        Vector probePosRel = probePosSun.subtract(titanPosSun);
        Vector probeVelRel = probeVelSun.subtract(titanVelSun);

        // Calculate required orbital velocity (relative to Titan)
        double v_orbit_mag = Math.sqrt(G * M_TITAN / r_orbit);

        // Calculate correct tangential direction (FIXED)
        Vector radialDir = probePosRel.normalize();
        Vector orbitalNormal = radialDir.cross(probeVelRel).normalize();  // Orbital plane normal
        Vector tangentialDir = orbitalNormal.cross(radialDir).normalize(); // True tangential direction
        Vector v_orbit_rel = tangentialDir.multiply(v_orbit_mag);
        System.out.println("Velocity relative to Titan " + v_orbit_rel.magnitude());

        // Compute velocity change needed (Titan-relative)
        Vector deltaV_rel = v_orbit_rel.subtract(probeVelRel);
        Vector bestGuess = probeVelSun.add(deltaV_rel);

        // Convert Δv to Sun frame and update probe velocity
        return bestGuess;
    }


    private void runSimulation(ArrayList<CelestialBodies> state, Vector vNewInertial, LocalDateTime time) {
    ArrayList<CelestialBodies> newState = new ArrayList<>(state);
    Probe probe = (Probe) newState.remove(BodyID.SPACESHIP.index());
    probe.setVelocity(vNewInertial);
    newState.add(probe);
    EphemerisLoader ephemerisLoader = new EphemerisLoader(newState, time, time.plusSeconds(2*9695), 1, true);
    ephemerisLoader.solve();
    System.out.println("Probe orbited Titan twice from " + time + " till " + time.plusSeconds(2*9695));
    for(var key : ephemerisLoader.history.keySet()){
        historyEphemeris.putHistory(key, ephemerisLoader.history.get(key));
//        Vector posTitan = ephemerisLoader.position(BodyID.TITAN, key);
//        Vector posSpaceship = ephemerisLoader.position(BodyID.SPACESHIP, key);
//        double distance = posSpaceship.getDistance(posTitan);
//        System.out.println("Distance: " + distance + " at time " + key);
//        if (distance>R_TITAN+300){error = Math.max(error, Math.abs(distance-R_TITAN-300));}
//        else if ((distance<(R_TITAN+100)&&(distance>R_TITAN))){error = Math.max(error, Math.abs(R_TITAN+100-distance));}
//        else if (distance<R_TITAN){return Double.MAX_VALUE;}
    }
//    return error;
    }

    private void evaluateFuel(ArrayList<CelestialBodies> state, Vector desiredVelocity) {
        Vector initialVel = state.get(BodyID.SPACESHIP.index()).getVelocity();
        Vector finalVel = initialVel.subtract(desiredVelocity);
        double thrustMagnitude = initialVel.subtract(finalVel).magnitude();
        totalFuelConsumption += thrustMagnitude*Probe.MASS;
    }

    public static void main(String[] args) {
        Orbit orbit = new Orbit();
    }

}
