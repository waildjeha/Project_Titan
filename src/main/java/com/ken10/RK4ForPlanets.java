package com.ken10;

public class RK4ForPlanets {
    double stepSize;
    double time;
    double startTime;
    double endTime;
    Object[][] state;

    public RK4ForPlanets(double stepSize, double time, double startTime, double endTime, Object[][] state) {
        this.stepSize = stepSize;
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
    }

    public void step() {
        double h = stepSize;
        double t = time;
        double[][] y = adapterArray(state);
        int n = state.length; //number of states of (planets/populations etc.) to evaluate
        int m = state[0].length;
        // First stage
        double[][] k1 = LotkaVolterraForPlanets.computeDerivatives(y);

        // Second stage
        double t2 = t + h / 2;
        double[][] y2 = new double[n][m-2];
        for (int i = 0; i < n; i++) {
            y2[i] = new double[m];
            for (int j = 0; j < m; j++) {
                y2[i][j] = y[i][j] + (h / 2) * k1[i][j];
            }
        }
        double[][] k2 = LotkaVolterraForPlanets.computeDerivatives(y2);

        // Third stage
        double t3 = t + h / 2;
        double[][] y3 = new double[n][m];
        for (int i = 0; i < n; i++) {
            y3[i] = new double[m];
            for (int j = 0; j < m; j++) {
                y3[i][j] = y2[i][j] + (h / 2) * k2[i][j];
            }
        }
        double[][] k3 = LotkaVolterraForPlanets.computeDerivatives(y3);

        // Fourth stage
        double t4 = t + h;
        double[][] y4 = new double[n][m];
        for (int i = 0; i < n; i++) {
            y4[i] = new double[m];
            for (int j = 0; j < m; j++) {
                y4[i][j] = y3[i][j] + (h / 2) * k3[i][j];
            }
        }
        double[][] k4 = LotkaVolterraForPlanets.computeDerivatives(y4);

        // Combine stages to update state
        for (int i = 0; i < n; i++) {
            var rowState = new double[m];
            var yRow = y[i];
            for (int j = 0; j < m; j++) {
                rowState[j] = yRow[j] + (h / 6) * (k1[i][j] + 2 * k2[i][j] + 2 * k3[i][j] + k4[i][j]);
            }
            for (int k = 1; k < m-1; k++) {
                state[i][k] = rowState[k-1];
            }
        }
        time += stepSize;
    }

    private double[][] adapterArray(Object[][] state) {
        double[][] adapterArray = new double[state.length][state[0].length-2];
        for (int i = 0; i < state.length; i++) {
            for (int j = 1; j < state[0].length-1; j++) {
                adapterArray[i][j] = Double.parseDouble(state[i][j].toString());
            }
        }
        return adapterArray;
    }
}
