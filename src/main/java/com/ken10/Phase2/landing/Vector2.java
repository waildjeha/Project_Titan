package com.ken10.Phase2.landing;

public class Vector2 {
    private final double x;
    private final double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    public Vector2 multiply(double scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    public double magnitude() {
        return Math.sqrt(x*x + y*y);
    }

    public Vector2 normalize() {
        double mag = magnitude();
        return mag > 0 ? new Vector2(x/mag, y/mag) : new Vector2(0,0);
    }

    @Override
    public String toString() {
        return String.format("(%.3fm, %.3fm)", x, y);
    }

    public static final Vector2 ZERO = new Vector2(0,0);
}