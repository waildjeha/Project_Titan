package com.ken10.Other;
//Class to represent position and velocity as vectors in the 3d plain for Entities class.
//Includes operations on vectors for changing.

public class Vector {
    private double x;
    private double y;
    private double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
    }
    public Vector subtract(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z);
    }
    public Vector multiply(double c) {
        return new Vector(this.x * c, this.y * c, this.z * c);
    }
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
