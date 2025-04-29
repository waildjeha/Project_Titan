package com.ken10.Phase1;

@FunctionalInterface
public interface OdeFunction {
    /**
     * Calculate derivatives for given time and state: dx/dt = f(x,t)
     * 
     * @param time  Current time
     * @param state Current state vector
     * @return Array of derivatives
     */
    double[] evaluate(double time, double[] state);
}