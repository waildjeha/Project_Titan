package com.ken10.Phase2.ProbeMission.ProbeMissionDominik;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.SolarSystem;
import com.ken10.Phase2.SolarSystemModel.Vector;

import java.util.ArrayList;
import java.util.List;

public class EarthPositions {
    private static final double R_EARTH = 6_370;
    private static final double TILT_RAD = Math.toRadians(23.44);
    private double stepDegree;
    private List<Vector> positions;


    public EarthPositions(double stepDegree) {
        this.stepDegree = stepDegree;
        calculatePositions();
    }
    public List<Vector> getPositions() {
        return positions;
    }
    public void setStepDegree(double stepDegree) {
        this.stepDegree = stepDegree;
        calculatePositions();
    }

    private void calculatePositions() {
    double cosEps = Math.cos(TILT_RAD);
    double sinEps = Math.sin(TILT_RAD);
    Vector position = SolarSystem.CreatePlanets().get(BodyID.EARTH.index()).getPosition();
    List<Vector> positions = new ArrayList<>();
    for(double degree = 0; degree < 360; degree += stepDegree) {

        double theta = Math.toRadians(degree);

        double cosT = Math.cos(theta);
        double sinT = Math.sin(theta);

        double x = position.getX() + R_EARTH * cosT;
        double y = position.getY() + R_EARTH * sinT * cosEps;
        double z = position.getZ() + R_EARTH * sinT * sinEps;
        positions.add(new Vector(x, y, z));
    }
    this.positions = positions;
    }

}
