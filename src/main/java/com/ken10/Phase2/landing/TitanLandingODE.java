package com.ken10.Phase2.landing;

import com.ken10.Phase1.OdeFunction;
import com.ken10.Phase2.SolarSystemModel.GravityCalc;
import com.ken10.Phase2.SolarSystemModel.PlanetModel;
import com.ken10.Phase2.SolarSystemModel.Titan;
import com.ken10.Phase2.landing.ControlOutput;


public class TitanLandingODE implements OdeFunction {

    private final Titan titan;                
    private final ControlOutput control;      
    public TitanLandingODE(Titan titan, ControlOutput control) {
        this.titan = titan;
        this.control = control;
    }

    @Override
    public double[] evaluate(double time, double[] state) {
        double x = state[0];
        double y = state[1];
        double theta = state[2];
        double vx = state[3];
        double vy = state[4];
        double omega = state[5];

        double altitude = Math.max(y - titan.getRadius(), 0); // km

        double g = GravityCalc.calculateGravity(titan, altitude);

        double thrustX = control.getThrustX();
        double thrustY = control.getThrustY();
        double torque = control.getTorque();

        // Derivatives
        double[] derivatives = new double[6];
        derivatives[0] = vx;                       // dx/dt = vx
        derivatives[1] = vy;                       // dy/dt = vy
        derivatives[2] = omega;                    // dθ/dt = ω
        derivatives[3] = thrustX;                  // dvx/dt
        derivatives[4] = thrustY - g;              // dvy/dt
        derivatives[5] = torque;                   // dω/dt

        return derivatives;
    }
}
