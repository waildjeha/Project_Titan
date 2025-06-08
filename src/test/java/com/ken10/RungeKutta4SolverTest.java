package com.ken10;

import com.ken10.Phase1.OdeFunction;
import com.ken10.Phase1.RungeKutta4Solver;
import org.junit.Test;

import static org.junit.Assert.*;

public class RungeKutta4SolverTest {

    private static final double TOLERANCE = 1e-6;

    // Test ODE function: dy/dt = -2y (analytical solution: y = y0 * e^(-2t))
    private OdeFunction exponentialDecay = new OdeFunction() {
        @Override
        public double[] evaluate(double t, double[] y) {
            return new double[]{-2.0 * y[0]};
        }
    };

    // Test ODE function: dy/dt = y (analytical solution: y = y0 * e^t)
    private OdeFunction exponentialGrowth = new OdeFunction() {
        @Override
        public double[] evaluate(double t, double[] y) {
            return new double[]{y[0]};
        }
    };

    // Test system: dx/dt = y, dy/dt = -x (harmonic oscillator)
    // Analytical solution: x = x0*cos(t) + y0*sin(t), y = y0*cos(t) - x0*sin(t)
    private OdeFunction harmonicOscillator = new OdeFunction() {
        @Override
        public double[] evaluate(double t, double[] state) {
            return new double[]{state[1], -state[0]};
        }
    };

    @Test
    public void testConstructor() {
        double[] initialState = {1.0};
        RungeKutta4Solver solver = new RungeKutta4Solver(
                exponentialDecay, initialState, 0.0, 1.0, 0.1);

        assertNotNull(solver);
        assertEquals(0.0, solver.getTime(), TOLERANCE);
        assertArrayEquals(new double[]{1.0}, solver.getState(), TOLERANCE);
    }

    @Test
    public void testSingleStep() {
        double[] initialState = {1.0};
        RungeKutta4Solver solver = new RungeKutta4Solver(
                exponentialDecay, initialState, 0.0, 1.0, 0.1);

        double initialTime = solver.getTime();
        double[] initialStateArray = solver.getState().clone();

        solver.step();

        // time should increase by stepSize
        assertEquals(initialTime + 0.1, solver.getTime(), TOLERANCE);

        // State should have changed
        assertNotEquals(initialStateArray[0], solver.getState()[0], TOLERANCE);

        // For dy/dt = -2y with y0=1, h=0.1 we expect y ≈ 0.8187
        double expected = Math.exp(-2.0 * 0.1); // Exakte Lösung bei t=0.1
        assertEquals(expected, solver.getState()[0], 1e-4); // RK4 ist sehr genau
    }

    @Test
    public void testExponentialDecayAccuracy() {
        double[] initialState = {1.0};
        double stepSize = 0.01;
        double endTime = 1.0;

        RungeKutta4Solver solver = new RungeKutta4Solver(
                exponentialDecay, initialState, 0.0, endTime, stepSize);

        // ... more steps
        while (solver.getTime() < endTime - stepSize/2) {
            solver.step();
        }

        // Compare with analytical solution: y = e^(-2t)
        double exactSolution = Math.exp(-2.0 * solver.getTime());
        double numericalSolution = solver.getState()[0];

        // RK4 should be very accurate for this ODE
        assertEquals(exactSolution, numericalSolution, 1e-6);
    }

    @Test
    public void testExponentialGrowthAccuracy() {
        double[] initialState = {2.0};
        double stepSize = 0.05;
        double endTime = 0.5;

        RungeKutta4Solver solver = new RungeKutta4Solver(
                exponentialGrowth, initialState, 0.0, endTime, stepSize);

        while (solver.getTime() < endTime - stepSize/2) {
            solver.step();
        }

        // Analytical solution: y = 2 * e^t
        double exactSolution = 2.0 * Math.exp(solver.getTime());
        double numericalSolution = solver.getState()[0];

        assertEquals(exactSolution, numericalSolution, 1e-5);
    }

    @Test
    public void testHarmonicOscillatorSystem() {
        // Initial conditions: x = 1, y = 0 (starts at maximum displacement)
        double[] initialState = {1.0, 0.0};
        double stepSize = 0.01;
        double period = 2 * Math.PI; // a complete period

        RungeKutta4Solver solver = new RungeKutta4Solver(
                harmonicOscillator, initialState, 0.0, period, stepSize);

        // Simulate a complete period
        while (solver.getTime() < period - stepSize/2) {
            solver.step();
        }

        //After a complete period, we should return to the initial state
        double[] finalState = solver.getState();
        assertEquals(1.0, finalState[0], 1e-3); // x sollte wieder 1 sein
        assertEquals(0.0, finalState[1], 1e-3); // y sollte wieder 0 sein
    }

    @Test
    public void testEnergyConservationInHarmonicOscillator() {
        double[] initialState = {1.0, 0.0};
        double stepSize = 0.01;

        RungeKutta4Solver solver = new RungeKutta4Solver(
                harmonicOscillator, initialState, 0.0, Math.PI, stepSize);

        // begin energy : E = 0.5 * (x² + y²)
        double initialEnergy = 0.5 * (initialState[0] * initialState[0] +
                initialState[1] * initialState[1]);

        // more steps
        for (int i = 0; i < 50; i++) {
            solver.step();
            double[] state = solver.getState();
            double currentEnergy = 0.5 * (state[0] * state[0] + state[1] * state[1]);

            // Energy should be conserved (with a small tolerance for numerical errors).
            assertEquals(initialEnergy, currentEnergy, 1e-6);
        }
    }

    @Test
    public void testMultipleSteps() {
        double[] initialState = {1.0};
        RungeKutta4Solver solver = new RungeKutta4Solver(
                exponentialDecay, initialState, 0.0, 1.0, 0.1);

        double previousTime = solver.getTime();
        double[] previousState = solver.getState().clone();

        // for more steps out
        for (int i = 0; i < 5; i++) {
            solver.step();

            // Time should increase monotonically.
            assertTrue(solver.getTime() > previousTime);

            // For exponential decay, the value should decrease
            assertTrue(solver.getState()[0] < previousState[0]);

            previousTime = solver.getTime();
            previousState = solver.getState().clone();
        }

        //After 5 steps we should be at t= 0.5
        assertEquals(0.5, solver.getTime(), TOLERANCE);
    }

    @Test
    public void testWithZeroStepSize() {
        double[] initialState = {1.0};

        // This should work but wothout causing changes
        RungeKutta4Solver solver = new RungeKutta4Solver(
                exponentialDecay, initialState, 0.0, 1.0, 0.0);

        double initialTime = solver.getTime();
        double[] initialStateArray = solver.getState().clone();

        solver.step();

        assertEquals(initialTime, solver.getTime(), TOLERANCE);
        assertArrayEquals(initialStateArray, solver.getState(), TOLERANCE);
    }

    @Test
    public void testWithNegativeStepSize() {
        // test backward integration method
        double[] initialState = {Math.exp(-2.0)}; // y(1) für die Lösung y = e^(-2t)
        RungeKutta4Solver solver = new RungeKutta4Solver(
                exponentialDecay, initialState, 1.0, 0.0, -0.1);

        solver.step();

        // time should decrease
        assertEquals(0.9, solver.getTime(), TOLERANCE);

        //Value should be increasing (backwards->exponential decay)
        assertTrue(solver.getState()[0] > initialState[0]);
    }

    @Test
    public void testLargeSystem() {
        // Test with a bigger system (3 variables)
        double[] initialState = {1.0, 2.0, 3.0};

        OdeFunction linearSystem = new OdeFunction() {
            @Override
            public double[] evaluate(double t, double[] y) {
                // Einfaches lineares System: dy_i/dt = -i * y_i
                return new double[]{-1.0 * y[0], -2.0 * y[1], -3.0 * y[2]};
            }
        };

        RungeKutta4Solver solver = new RungeKutta4Solver(
                linearSystem, initialState, 0.0, 1.0, 0.1);

        solver.step();

        // All values should decreaase
        double[] newState = solver.getState();
        for (int i = 0; i < 3; i++) {
            assertTrue(newState[i] < initialState[i]);
        }
    }

    // Helpermethod to compare arrays with tolerance
    private void assertArrayEquals(double[] expected, double[] actual, double tolerance) {
        assertEquals("Array lengths should be equal", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Arrays differ at index " + i, expected[i], actual[i], tolerance);
        }
    }


    private void assertNotEquals(double unexpected, double actual, double tolerance) {
        assertTrue("Values should not be equal", Math.abs(unexpected - actual) > tolerance);
    }
}