package com.example.demo;

public class Planet {
    private String planetName;
    private double x;
    private double y;
    private double z;

    private double vx;
    private double vy;
    private double vz;

    private double m;

    public Planet(String planetName, double x, double y, double z, double vx, double vy, double vz, double m) {
        this.planetName = planetName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.m = m;
    }

    @Override
    public String toString() {
        return planetName+ " {x=" + x + ", y=" + y + ", z=" + z + 
               ", vx=" + vx + ", vy=" + vy + ", vz=" + vz + 
               ", m=" + m + "}";
    }
}
