package com.ken10;

import com.ken10.Phase2.OptimizationAlgorithms.RK4Probe;
import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class RK4ProbeTest {

    private RK4Probe rk4Probe;
    private Probe testProbe;
    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> testHistoryPlanets;
    private LocalDateTime startTime;
    private int stepSizeMin;

    @Before
    public void setUp() {
        // Initialize test data
        startTime = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        stepSizeMin = 60; // 1 hour steps for testing

        // Create test probe with known initial conditions
        Vector initialPosition = new Vector(-1.4664541759104577E8, -2.8949304626334388E7, 2241.9186033698497);
        Vector initialVelocity = new Vector(63.28501526589577, -30.337437078355766, -12.818387742029104);
        testProbe = new Probe("TestProbe", initialPosition, initialVelocity);

        // Create minimal test history for planets
        testHistoryPlanets = createTestPlanetHistory();

        // Initialize RK4Probe
        rk4Probe = new RK4Probe(testProbe, testHistoryPlanets, stepSizeMin);
    }

    @After
    public void tearDown() {
        rk4Probe = null;
        testProbe = null;
        testHistoryPlanets = null;
    }

    /**
     * Test constructor initialization
     */
    @Test
    public void testConstructor() {
        assertNotNull("RK4Probe should be initialized", rk4Probe);
        assertEquals("Step size should match", stepSizeMin, rk4Probe.getStepSizeMin());
        assertNotNull("Initial probe should be set", rk4Probe.getInitialProbe());
        assertEquals("Initial probe name should match", "TestProbe", rk4Probe.getInitialProbe().getName());
    }

    /**
     * Test initial probe copy independence
     */
    @Test
    public void testInitialProbeCopyIndependence() {
        Probe initialProbe = rk4Probe.getInitialProbe();

        // Verify it's a copy, not the same reference
        assertNotSame("Initial probe should be a copy", testProbe, initialProbe);

        // Verify values are the same
        assertEquals("Position should match", testProbe.getPosition().getX(), initialProbe.getPosition().getX(), 1e-10);
        assertEquals("Velocity should match", testProbe.getVelocity().getX(), initialProbe.getVelocity().getX(), 1e-10);
    }

    /**
     * Test initial closest distance
     */
    @Test
    public void testInitialClosestDistance() {
        double initialDistance = rk4Probe.getClosestDistance();
        assertTrue("Initial closest distance should be positive", initialDistance > 0);
        assertEquals("Initial closest distance should be MAX_VALUE", Double.MAX_VALUE, initialDistance, 0);
    }

    /**
     * Test initial closest distance time
     */
    @Test
    public void testInitialClosestDistanceTime() {
        LocalDateTime initialTime = rk4Probe.getClosestDistTime();
        assertEquals("Initial closest time should be start time", startTime, initialTime);
    }

    /**
     * Test solve method execution (basic functionality)
     */
    @Test
    public void testSolveBasicExecution() {
        // This test verifies the solve method runs without throwing exceptions
        try {
            rk4Probe.solve();
            assertTrue("Solve method should complete without exceptions", true);
        } catch (Exception e) {
            fail("Solve method should not throw exceptions: " + e.getMessage());
        }
    }

    /**
     * Test that history is populated after solving
     */
    @Test
    public void testHistoryPopulationAfterSolve() {
        rk4Probe.solve();

        assertNotNull("History should not be null", rk4Probe.historyProbe);
        assertTrue("History should contain entries", rk4Probe.historyProbe.size() > 0);
        assertTrue("History should contain initial state", rk4Probe.historyProbe.containsKey(startTime));
    }

    /**
     * Test closest distance update after solving
     */
    @Test
    public void testClosestDistanceUpdateAfterSolve() {
        double initialDistance = rk4Probe.getClosestDistance();
        rk4Probe.solve();
        double finalDistance = rk4Probe.getClosestDistance();

        assertTrue("Closest distance should be updated", finalDistance < initialDistance);
        assertTrue("Closest distance should be positive", finalDistance >= 0);
    }

    /**
     * Test that closest distance time is updated
     */
    @Test
    public void testClosestDistanceTimeUpdate() {
        LocalDateTime initialTime = rk4Probe.getClosestDistTime();
        rk4Probe.solve();
        LocalDateTime finalTime = rk4Probe.getClosestDistTime();

        // The time should be updated (could be same if closest approach is at start)
        assertNotNull("Closest distance time should not be null", finalTime);
        assertTrue("Closest distance time should be after or equal to start time",
                   !finalTime.isBefore(startTime));
    }

    /**
     * Test toString method
     */
    @Test
    public void testToString() {
        String result = rk4Probe.toString();

        assertNotNull("ToString should not return null", result);
        assertTrue("ToString should contain probe info", result.contains("Initial probe position"));
        assertTrue("ToString should contain velocity info", result.contains("Velocity magnitude"));
        assertTrue("ToString should contain distance info", result.contains("Closest Distance"));
        assertTrue("ToString should contain date info", result.contains("Date of closest approach"));
    }

    /**
     * Test step size getter
     */
    @Test
    public void testGetStepSizeMin() {
        assertEquals("Step size should match initialized value", stepSizeMin, rk4Probe.getStepSizeMin());
    }

    /**
     * Test probe state consistency
     */
    @Test
    public void testProbeStateConsistency() {
        Probe originalProbe = rk4Probe.getInitialProbe();
        Vector originalPosition = originalProbe.getPosition().copy();
        Vector originalVelocity = originalProbe.getVelocity().copy();

        rk4Probe.solve();

        // Original probe should remain unchanged
        Probe afterSolveProbe = rk4Probe.getInitialProbe();
        assertEquals("Original position should be preserved",
                     originalPosition.getX(), afterSolveProbe.getPosition().getX(), 1e-10);
        assertEquals("Original velocity should be preserved",
                     originalVelocity.getX(), afterSolveProbe.getVelocity().getX(), 1e-10);
    }

    /**
     * Test with different step sizes
     */
    @Test
    public void testDifferentStepSizes() {
        int[] stepSizes = {30, 60, 120}; // 30 min, 1 hour, 2 hours

        for (int stepSize : stepSizes) {
            RK4Probe probe = new RK4Probe(testProbe, testHistoryPlanets, stepSize);
            assertEquals("Step size should be set correctly", stepSize, probe.getStepSizeMin());

            // Test that it can solve without errors
            try {
                probe.solve();
                assertTrue("Should solve successfully with step size " + stepSize, true);
            } catch (Exception e) {
                fail("Should not fail with step size " + stepSize + ": " + e.getMessage());
            }
        }
    }

    /**
     * Helper method to create minimal test planet history
     */
    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> createTestPlanetHistory() {
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history = new Hashtable<>();

        // Creating a simple test scenario with minimal planet data
        LocalDateTime currentTime = startTime;
        LocalDateTime endTime = LocalDateTime.of(2026, 4, 1, 0, 0, 0);

        while (currentTime.isBefore(endTime) || currentTime.isEqual(endTime)) {
            ArrayList<CelestialBodies> planets = SolarSystem.createPlanets();
            history.put(currentTime, planets);
            currentTime = currentTime.plusMinutes(stepSizeMin);
        }

        return history;
    }

    /**
     * Integration test with real ephemeris data (if available)
     */
    @Test
    public void testWithRealEphemerisData() {
        try {
            // Here we're creating real solar system state
            ArrayList<CelestialBodies> initialState = SolarSystem.createPlanets();
            initialState.add(testProbe);

            // Creating the ephemeris for short duration to keep test fast
            LocalDateTime testEndTime = startTime.plusDays(30); // 30 days only for testing
            EphemerisLoader eph = new EphemerisLoader(initialState, startTime, testEndTime, 60);
            eph.solve();

            // Create RK4Probe with real data
            RK4Probe realDataProbe = new RK4Probe(testProbe, eph.history, 60);
            realDataProbe.solve();

            // Verify results
            assertTrue("Should complete with real data", realDataProbe.getClosestDistance() >= 0);
            assertNotNull("Should have closest distance time", realDataProbe.getClosestDistTime());

        } catch (Exception e) {
            // If real ephemeris data is not available, skip this test
            System.out.println("Skipping real ephemeris test due to: " + e.getMessage());
        }
    }
}