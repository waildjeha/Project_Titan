package com.ken10.Phase2.SolarSystemModel;

/**
 * Three-dimensional vector class for 3D spatial calculations.
 * Provides comprehensive vector operations including arithmetic,
 * magnitude calculations, and geometric operations.
 * Probably the most used class in the simulation.
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

    /**
     * Creates a new vector with the specified component added.
     * 
     * @param x the value to add to the the x component
     * @return a new Vector with the modified x component
    */
    public Vector addX(double x) { return new Vector(this.x + x, this.y, this.z); }

    /**
     * Creates a new vector with the specified component added.
     * 
     * @param y the value to add to the the y component
     * @return a new Vector with the modified y component
    */
    public Vector addY(double y) { return new Vector(this.x, this.y+y, this.z); }

    /**
     * Creates a new vector with the specified component added.
     * 
     * @param z the value to add to the the z component
     * @return a new Vector with the modified z component
    */
    public Vector addZ(double z) { return new Vector(this.x, this.y, this.z + z); }

    /**
     * Performs vector addition with another vector.
     * 
     * @param v the vector to add to this vector
     * @return a new Vector representing the sum
    */
    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    /**
     * Performs vector subtraction with another vector.
     * 
     * @param v the vector to subtract from this vector
     * @return a new Vector representing the difference
    */
    public Vector subtract(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    /**
     * Multiplies this vector by a scalar value.
     * 
     * @param c the scalar multiplier
     * @return a new Vector scaled by the multiplier
    */
    public Vector multiply(double c) {
        return new Vector(this.x * c, this.y * c, this.z * c);
    }

    /**
     * Calculates the magnitude (length) of this vector.
     * 
     * @return the Euclidean magnitude of the vector
    */
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Divides each component of this vector by a scalar value.
     * 
     * @param val the divisor value
     * @return a new Vector with each component divided by val
    */
    public Vector divide(double val) {return new Vector(this.x / val, this.y / val, this.z / val);}

    /**
     * Calculates the Euclidean distance between two position vectors.
     * 
     * @param v1 the first position vector
     * @param v2 the second position vector
     * @return the distance between the two points
    */
    public static double getDistance(Vector v1, Vector v2) {
        Vector newVector = new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        return newVector.magnitude();
    }

    /**
     * Calculates the distance from this vector to another vector.
     * 
     * @param v the target vector
     * @return the Euclidean distance between the vectors
    */
    public double getDistance(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z).magnitude();
    }

    /**
     * Computes the cross product of this vector with another vector.
     * 
     * @param v the vector to compute cross product with
     * @return a new Vector representing the cross product
    */
    public Vector cross(Vector v) {
        return new Vector(
            this.y * v.z - this.z * v.y,
            this.z * v.x - this.x * v.z,
            this.x * v.y - this.y * v.x
        );
    }

    /**
     * Computes the dot product of this vector with another vector.
     * 
     * @param v the vector to compute dot product with
     * @return the scalar dot product value
    */
    public double dot(Vector v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    /**
     * Creates a deep copy of this vector.
     * 
     * @return a new Vector with identical component values
    */
    public Vector copy() {
        return new Vector(this.x, this.y, this.z);
    }

    /**
     * Returns a unit vector in the same direction as this vector.
     * If the vector has zero magnitude, returns a zero vector.
     * 
     * @return a normalized vector with magnitude 1, or zero vector if magnitude is 0
    */
    public Vector normalize() {
        double mag = magnitude();
        return (mag == 0) ? new Vector(0, 0, 0) : multiply(1.0 / mag);
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
