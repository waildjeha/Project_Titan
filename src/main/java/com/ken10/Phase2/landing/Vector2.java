package com.ken10.Phase2.landing;

public class Vector2 {

    public static final Vector2 ZERO = new Vector2(0,0) ;
    private double x;
    private double y;

    public Vector2(double x, double y) {
        this.x=x;
        this.y=y;

    }


    double getX(){
        return x;
    }

    double getY(){
        return y;
    }


    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }



    @Override
    public String toString() {
        return String.format("x: %.4f y: %.4f", x, y);
    }
}
