package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.SolarSystem;

import java.util.ArrayList;

public abstract class FeedbackController {

    private final StateVector goal;
    private ArrayList<CelestialBodies> celestialBodies;
    private CelestialBodies titan;

    public FeedbackController(){
        this.goal = new StateVector(0,0,0,0,0,0);
        this.celestialBodies= SolarSystem.createPlanets();
        this.titan=celestialBodies.get(9);


    }


    public ControlOutput controlFunction(StateVector stateVector,double time){
        //        TO DO
        //        the idea is that based on the error a new thrust parameters are calculated/ the error is just the vector cause my goal is zero
        //        return new ControlOutput(thrustX, thrustY, torque);
        //        adjust the values based on the thrust
        //        solve using rk4 - the plant function
        //        check if landed

        return null;

    }
    public StateVector plantFunction(StateVector currentState, ControlOutput control, double dt) {
        TitanLandingODE ode = new TitanLandingODE(titan,control);
        double[] states = currentState.toArray();
        StateVector vector=new StateVector(0,0,0,0,0,0);

        double[] k1 = ode.evaluate(0, states);

        double[] temp1 = new double[states.length];
        for (int i = 0; i < states.length; i++) {
            temp1[i] = states[i] + 0.5 * dt * k1[i];
        }
        double[] k2 = ode.evaluate(dt/2, temp1);

        double[] temp2 = new double[states.length];
        for (int i = 0; i < states.length; i++) {
            temp2[i] = states[i] + 0.5 * dt * k2[i];
        }
        double[] k3 = ode.evaluate(dt/2, temp2);

        double[] temp3 = new double[states.length];
        for (int i = 0; i < states.length; i++) {
            temp3[i] = states[i] + dt * k3[i];
        }
        double[] k4 = ode.evaluate(dt, temp3);

        double[] newStates = new double[states.length];
        for (int i = 0; i < states.length; i++) {
            newStates[i] = states[i] + (dt/6.0) * (k1[i] + 2*k2[i] + 2*k3[i] + k4[i]);
        }

        vector.setFromArray(newStates);

        return vector;
    }

// the sensor function this is how NASA called it is meant to check if the goal is reached
    public boolean sensorFunction(StateVector stateVector ){
        return stateVector.hasLandedSafely(goal);
    }

}