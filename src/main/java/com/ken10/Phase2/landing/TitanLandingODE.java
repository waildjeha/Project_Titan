package com.ken10.Phase2.landing;

import com.ken10.Phase1.OdeFunction;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.GravityCalc;
import com.ken10.Phase2.SolarSystemModel.PlanetModel;
import com.ken10.Phase2.SolarSystemModel.Titan;
import com.ken10.Phase2.landing.ControlOutput;


public class TitanLandingODE implements OdeFunction {

    private final CelestialBodies titan;
    private final ControlOutput control;

    public TitanLandingODE(CelestialBodies titan, ControlOutput control) {
        this.titan = titan;
        this.control = control;
    }

    public double[] evaluate(double time, double[] state) {
        double x = state[0];
        double vx = state[1];
        double y = state[2];
        double vy = state[3];
        double theta = state[4];
        double omega = state[5];

        double mass = 1000.0;
        double I = mass * 100.0;

        // Get control inputs
        double thrustX = control.getThrustX();
        double thrustY = control.getThrustY();
        double torque = control.getTorque();

        double altitude = Math.max(y, 0);
        double r = titan.getRadius();
        double g = 1.352 * Math.pow(r/(r + altitude), 2);

        // Derivatives
        double[] derivatives = new double[6];
        derivatives[0] = vx;                       // dx/dt = vx
        derivatives[1] = thrustX / mass;           // dvx/dt = Fx/m
        derivatives[2] = vy;                       // dy/dt = vy
        derivatives[3] = (thrustY / mass) - g;     // dvy/dt = Fy/m - g
        derivatives[4] = omega;                    // dθ/dt = ω
        derivatives[5] = torque / I;               // dω/dt = τ/I

        return derivatives;
    }

}