package com.ken10.Phase2.SolarSystemModel;

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

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }

    public Vector addX(double x) { return new Vector(this.x + x, this.y, this.z); }
    public Vector addY(double y) { return new Vector(this.x, this.y+y, this.z); }
    public Vector addZ(double z) { return new Vector(this.x, this.y, this.z + z); }

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

    public Vector divide(double val) {return new Vector(this.x / val, this.y / val, this.z / val);}

    public static double getDistance(Vector v1, Vector v2) {
        Vector newVector = new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        return newVector.magnitude();
    }

    public double getDistance(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z).magnitude();
    }

    public Vector cross(Vector v) {
        return new Vector(
            this.y * v.z - this.z * v.y,
            this.z * v.x - this.x * v.z,
            this.x * v.y - this.y * v.x
        );
    }

    public Vector findOrthogonalVector() {
        double x = getX();
        double y = getY();
        double z = getZ();

        // Handle zero vector case
        if (x == 0 && y == 0 && z == 0) {
            throw new IllegalArgumentException("Cannot find orthogonal vector for zero vector");
        }

        // Fixed reference vector (1,0,0)
        Vector reference = new Vector(1, 0, 0);

        // Compute cross product v × reference
        Vector orthogonal = new Vector(
                y * reference.getZ() - z * reference.getY(),
                z * reference.getX() - x * reference.getZ(),
                x * reference.getY() - y * reference.getX()
        );

        // If result is zero (v parallel to reference), use different reference (0,1,0)
        if (orthogonal.magnitude() < 1e-10) {
            reference = new Vector(0, 1, 0);
            orthogonal = new Vector(
                    y * reference.getZ() - z * reference.getY(),
                    z * reference.getX() - x * reference.getZ(),
                    x * reference.getY() - y * reference.getX()
            );
        }

        return orthogonal;
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

    public static void main(String[] args) {
        Vector vector = new Vector(1, 2, 3).normalize();
        Vector orthogonalVector = vector.findOrthogonalVector();
        System.out.println(vector);
        System.out.println(orthogonalVector);
        double dotProduct = vector.dot(orthogonalVector);
        System.out.println(dotProduct);
    }
}


