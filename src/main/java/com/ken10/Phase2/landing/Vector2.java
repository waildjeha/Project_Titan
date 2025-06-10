package com.ken10.Phase2.landing;


/**
 * Represents a 2D vector with basic operations such as addition, subtraction, multiplication,
 * dot product, normalization, and magnitude calculation.
 */
public class Vector2 {
    private final double x;
    private final double y;


    /**
     * Default constructor initializes the vector to (0, 0).
     */
    public Vector2() {
        this(0, 0);
    }


    /**
     * Constructs a Vector2 with specified x and y coordinates.
     *
     * @param x Horizontal coordinate
     * @param y Vertical coordinate
     */
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Vector2 with specified x, y, and z coordinates.
     * Note: The z coordinate is ignored in this 2D vector implementation.
     *
     * @param x Horizontal coordinate
     * @param y Vertical coordinate
     * @param z Ignored z coordinate
     */
    public Vector2(double x, double y, double z) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Vector2 from an array of doubles.
     * The first two elements of the array are used for x and y coordinates.
     *
     * @param values Array containing at least two elements for x and y
     */
    public Vector2(double[] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("Array must contain at least 2 elements.");
        }
        this.x = values[0];
        this.y = values[1];
    }
    


    /**
     * Returns the x coordinate of the vector.
     *
     * @return x coordinate
     */
    public double getX() { return x; }

    /**
     * Returns the y coordinate of the vector.
     *
     * @return y coordinate
     */
    public double getY() { return y; }


    /**
     * Performs vector addition with another Vector2.
     * @param other The other vector to add.
     * @return A new Vector2 representing the sum.
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    /**
     * Performs vector subtraction with another Vector2.
     * @param other The other vector to subtract.
     * @return A new Vector2 representing the difference.
     */
    public Vector2 subtract(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    /**
     * Multiplies this vector by a scalar value.
     * @param scalar The scalar value to multiply with.
     * @return A new Vector2 representing the scaled vector.
     */
    public Vector2 multiply(double scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    /**
     * Calculates the dot product of this vector with another Vector2.
     * @param c The other vector to calculate the dot product with.
     * @return The dot product as a double.
     */
    public double dotProduct(Vector2 c) {
        double result = 0;
        for (int i = 0; i < 2; i++) {
            result += this.getValue(i) * c.getValue(i);
        }
        return result;
    }

    /**
     * Calculates the dot product of this vector with another Vector2.
     * @param i The other vector to calculate the dot product with.
     * @return The dot product as a double.
     */
    private double getValue(int i) {
        switch (i) {
            case 0: return x;
            case 1: return y;
            default: throw new IndexOutOfBoundsException("Invalid index");
        }
    }

    /**
     * Calculates the magnitude (length) of the vector.
     * @return The magnitude as a double.
     */
    public double magnitude() {
        return Math.sqrt(x*x + y*y);
    }

    /**
     * Normalizes the vector to have a magnitude of 1.
     * If the magnitude is zero, returns a zero vector.
     * @return A new Vector2 representing the normalized vector.
     */
    public Vector2 normalize() {
        double mag = magnitude();
        return mag > 0 ? new Vector2(x/mag, y/mag) : new Vector2(0,0);
    }


    /**
     * Returns a string representation of the vector.
     * @return A string in the format "(x, y)".
     */
    @Override
    public String toString() {
        return String.format("(%.3fm, %.3fm)", x, y);
    }

    public static final Vector2 VELOCITY = new Vector2(0,0);

    /**
     * Calculates the length of the vector.
     * This is equivalent to the magnitude of the vector.
     * @return The length as a double.
     */
    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the dimensions of the vector.
     * For a 2D vector, this is always 2.
     * @return The dimensions as an integer.
     */
    public int getSize() {
        return 2; // Since this is a 2D vector
    }
}