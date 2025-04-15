package com.ken10.Other;

/**
 * Not used, keeping it for now just in case tho.
 */

public class Derivative {
    public Vector positionDerivative;
    public Vector velocityDerivative;

    public Derivative(Vector positionDerivative, Vector velocityDerivative) {
        this.positionDerivative = positionDerivative;
        this.velocityDerivative = velocityDerivative;
    }
    public Derivative add(Derivative derivative) {
        return new Derivative(positionDerivative.add(derivative.positionDerivative),
                velocityDerivative.add(derivative.velocityDerivative));
    }
    public Derivative multiply(double c) {
        return new Derivative(positionDerivative.multiply(c),velocityDerivative.multiply(c));
    }

}
