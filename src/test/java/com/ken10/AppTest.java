package com.ken10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ken10.ODEs.GravityCalc;
import com.ken10.Other.Vector;
import com.ken10.entities.CelestialBodies;
import com.ken10.entities.SolarSystem;
import com.ken10.entities.planet.PlanetModel;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {

        assertTrue(true );
    }

    @Test
    public void testSingleBodyZeroAcceleration() {
        ArrayList<CelestialBodies> bodies = new ArrayList<>();
        bodies.add(new PlanetModel("Earth",
                new Vector(0, 0, 0),
                new Vector(0, 0, 0), 5.972e24));
        double delta= 1e-6;
//        delta is the margin of error
        Vector acceleration = GravityCalc.computeAcceleration(bodies, 0);
        assertEquals(0, acceleration.getX(), delta);
        assertEquals(0, acceleration.getY(), delta);
        assertEquals(0, acceleration.getZ(), delta);
    }

    @Test
    public void testEvaluateAccelerationOfMercury() {
        ArrayList<CelestialBodies> bodies = SolarSystem.CreatePlanets();
        Vector acceleration = GravityCalc.computeAcceleration(bodies, 1);

       double expectedX = 2.77e-5;
       double expectedY = 1.58e-5;
       double expectedZ = -1.27e-6;
       double delta=1e-6;

        assertEquals(expectedX, acceleration.getX(),delta);
        assertEquals(expectedY, acceleration.getY(), delta);
        assertEquals(expectedZ, acceleration.getZ(), delta);
    }


}
