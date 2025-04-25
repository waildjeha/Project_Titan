package com.ken10.Dillons_Phase2.entities.Other;

/**
 * Object for Vectors and the properties.
 * Probably most used class.
 */
public class Vector {
    private double x;
    private double y;
    private double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

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

    public static double getDistance(Vector v1, Vector v2) {
        Vector newVector = new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        return newVector.magnitude();
    }

    public Vector cross(Vector v) {
        return new Vector(
            this.y * v.z - this.z * v.y,
            this.z * v.x - this.x * v.z,
            this.x * v.y - this.y * v.x
        );
    }

    public double dot(Vector v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector copy() {
        return new Vector(this.x, this.y, this.z);
    }
    public Vector normalize() {
        double mag = magnitude();
        return (mag == 0) ? new Vector(0, 0, 0) : multiply(1.0 / mag);
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
