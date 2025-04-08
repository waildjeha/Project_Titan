package com.ken10.phase2;

import java.util.ArrayList;

import static com.ken10.phase2.InitialDataset.bodyArrayList;
import static com.ken10.phase2.PlanetarySystem.evaluateDerivatives;
import static com.ken10.phase2.PlanetarySystem.hardCodedDataset;

public class RK4ForPlanets extends Solver{
    //PlanetarySystem planetarySystem;
    public RK4ForPlanets(ArrayList<Body> planetarySystem, double startTime, double endTime, double stepSize)
    {
        super(planetarySystem, startTime, endTime, stepSize);
    }

    public void step() {
        double h = stepSize;
        double t = time;
        ArrayList<Body> y = planetarySystem;
        int n = y.size(); //number of states of (planets/populations etc.) to evaluate
        // First stage
        ArrayList<Body> k1 = evaluateDerivatives(time, y);

        // Second stage
        double t2 = t + h / 2;
        ArrayList<Body> y2 = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Body b2 = new Body(i);
            b2.setX(y.get(i).getX() + (h / 2) * k1.get(i).getX());
            b2.setY(y.get(i).getY() + (h / 2) * k1.get(i).getY());
            b2.setZ(y.get(i).getZ() + (h / 2) * k1.get(i).getZ());
            b2.setVx(y.get(i).getVx() + (h / 2) * k1.get(i).getVx());
            b2.setVy(y.get(i).getVy() + (h / 2) * k1.get(i).getVy());
            b2.setVz(y.get(i).getVz() + (h / 2) * k1.get(i).getVz());
            y2.add(b2);
        }
        var k2 = evaluateDerivatives(t2, y2);

        // Third stage
        double t3 = t + h / 2;
        ArrayList<Body> y3 = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Body b3 = new Body(i);
            b3.setX(y.get(i).getX() + (h / 2) * k2.get(i).getX());
            b3.setY(y.get(i).getY() + (h / 2) * k2.get(i).getY());
            b3.setZ(y.get(i).getZ() + (h / 2) * k2.get(i).getZ());
            b3.setVx(y.get(i).getVx() + (h / 2) * k2.get(i).getVx());
            b3.setVy(y.get(i).getVy() + (h / 2) * k2.get(i).getVy());
            b3.setVz(y.get(i).getVz() + (h / 2) * k2.get(i).getVz());
            y3.add(b3); // <-- Use y3 here, not y2
        }
        var k3 = evaluateDerivatives(t3, y3);

// Fourth stage
        double t4 = t + h;
        ArrayList<Body> y4 = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Body b4 = new Body(i);
            b4.setX(y.get(i).getX() + h * k3.get(i).getX());  // Notice: h * k3, not (h/2)
            b4.setY(y.get(i).getY() + h * k3.get(i).getY());
            b4.setZ(y.get(i).getZ() + h * k3.get(i).getZ());
            b4.setVx(y.get(i).getVx() + h * k3.get(i).getVx());
            b4.setVy(y.get(i).getVy() + h * k3.get(i).getVy());
            b4.setVz(y.get(i).getVz() + h * k3.get(i).getVz());
            y4.add(b4); // <-- Use y4 here
        }
        var k4 = evaluateDerivatives(t4, y4);


        // Combine stages to update state
        for (int i = 0; i < n; i++) {
            var bodyState = new Body(y.get(i));

            bodyState.setX(bodyState.getX()
                    + (h/6) * (k1.get(i).getX()  + 2*k2.get(i).getX()  + 2*k3.get(i).getX()  + k4.get(i).getX()));
            bodyState.setY(bodyState.getY()
                    + (h/6) * (k1.get(i).getY()  + 2*k2.get(i).getY()  + 2*k3.get(i).getY()  + k4.get(i).getY()));
            bodyState.setZ(bodyState.getZ()
                    + (h/6) * (k1.get(i).getZ()  + 2*k2.get(i).getZ()  + 2*k3.get(i).getZ()  + k4.get(i).getZ()));
            bodyState.setVx(bodyState.getVx()
                    + (h/6) * (k1.get(i).getVx() + 2*k2.get(i).getVx() + 2*k3.get(i).getVx() + k4.get(i).getVx()));
            bodyState.setVy(bodyState.getVy()
                    + (h/6) * (k1.get(i).getVy() + 2*k2.get(i).getVy() + 2*k3.get(i).getVy() + k4.get(i).getVy()));
            bodyState.setVz(bodyState.getVz()
                    + (h/6) * (k1.get(i).getVz() + 2*k2.get(i).getVz() + 2*k3.get(i).getVz() + k4.get(i).getVz()));
            planetarySystem.set(i,bodyState);
            }
            planetarySystem.set(0, new Body("Sun", 0, 0, 0, 0, 0, 0, 1.99E+30));


        time += stepSize;
    }

    public static void main(String[] args) {
        ArrayList<Body> planetarySystem = bodyArrayList;
        RK4ForPlanets rk4 = new RK4ForPlanets(planetarySystem, 0, 5, 0.1);
        rk4.solve();

//        rk4.solve();
    }
}
