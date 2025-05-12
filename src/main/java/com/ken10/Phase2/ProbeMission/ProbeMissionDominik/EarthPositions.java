package com.ken10.Phase2.ProbeMission.ProbeMissionDominik;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.SolarSystem;
import com.ken10.Phase2.SolarSystemModel.Vector;

import java.util.ArrayList;
import java.util.List;

import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;

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
    System.out.println(position.toString());
    List<Vector> positions = new ArrayList<>();
    for(double degree = 0; degree < 360; degree += stepDegree) {

        double theta = Math.toRadians(degree);

        double cosT = Math.cos(theta);
        double sinT = Math.sin(theta);

        double x = position.getX() + R_EARTH * cosT;
//        System.out.println(x + "<- x-coordinate");
        double y = position.getY() + R_EARTH * sinT * cosEps;
//        System.out.println(y + "<- y-coordinate");
        double z = position.getZ() + R_EARTH * sinT * sinEps;
//        System.out.println(z + "<- z-coordinate");
        positions.add(new Vector(x, y, z));
    }
    this.positions = positions;
    }

    public static void main(String[] args) {
Vector position = new Vector(-1.4665809771040002E8, -2.8948457794751514E7, 2475.989626902912);
Vector earthPosition = SolarSystem.CreatePlanets().get(BodyID.EARTH.index()).getPosition();
System.out.println(getDistance(position, earthPosition));


    }

}
