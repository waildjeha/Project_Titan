package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.Probe;

import java.time.LocalDateTime;

public interface FitnessFunctions {

    public double getError();

    public LocalDateTime getClosestDistTime();

    public Probe getInitialProbe();

    public int getStepSize();

    public void solve();
}
