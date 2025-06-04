package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.Vector;

import java.util.Random;

/**
 * Represents wind on Titan as a 3D vector (zonal, meridional, vertical).
 * <p>This method models the wind speed profile according to observational data
 * from the Huygens probe and Cassini mission as reported in:
 * <i>Journal of Geophysical Research: Planets – Chapter 4.1: Winds</i>.</p>
 *
 */
public class Wind {

   private static Random random =new Random();
    private static final double DEFAULT_NOISE_FACTOR = 0.1;

    /**
     * Creates a wind vector at a given altitude (km) from Titan's surface.
     * @param distanceToTitan altitude in kilometers
     * @return wind vector (x=zonal, y=meridional, z=vertical)
     */


    public static Vector getWindVector(double distanceToTitan){
        Vector windVector=createWindVector(distanceToTitan);
     return addRandomNoise(windVector);
    }


    public static Vector createWindVector(double distanceToTitan) {
        double zonal = calculateZonalWind(distanceToTitan);
        double meridional = calculateMeridionalWind(distanceToTitan);
        double vertical = calculateVerticalWind(distanceToTitan);
        return new Vector(zonal, meridional, vertical);
    }

    /**
     * Calculates zonal (east-west) wind speed based on altitude.
     * @param distanceToTitan altitude in kilometers
     * @return zonal wind speed (m/s)
     */
    public static double calculateZonalWind(double distanceToTitan) {
        double wind;
        if (distanceToTitan < 10) {
            double slope = calculateSlope(0, 0, 10, 1);
            wind = slope * distanceToTitan;
        } else if (distanceToTitan < 60) {
            double slope = calculateSlope(10, 1, 60, 40);
            wind = 1 + (distanceToTitan - 10) * slope;
        } else if (distanceToTitan < 75) {
            double slope = calculateSlope(60, 40, 75, 5);
            wind = 40 + (distanceToTitan - 60) * slope;
        } else if (distanceToTitan < 200) {
            double slope = calculateSlope(75, 5, 200, 200);
            wind = 5 + (distanceToTitan - 75) * slope;
        } else if (distanceToTitan < 450) {
            double slope = calculateSlope(200, 200, 450, 60);
            wind = 200 + (distanceToTitan - 200) * slope;
        } else {
            double slope = calculateSlope(450, 60, 600, 60);
            wind = 60 + (distanceToTitan - 450) * slope;
        }
        return wind;
    }

    /**
     * Calculates the slope between two points.
     * @param x1 first x
     * @param y1 first y
     * @param x2 second x
     * @param y2 second y
     * @return slope
     */
    public static double calculateSlope(double x1, double y1, double x2, double y2) {
        return (y2 - y1) / (x2 - x1);
    }

    /**
     * Calculates the meridional (north-south) wind speed based on the altitude (distance to Titan's surface).
     *</p>
     * The meridional wind is generally weak, usually less than 1 m/s,
     * with speeds peaking between 0.4 and 0.9 m/s near the surface.
     *
     * @param distanceToTitan Altitude above Titan's surface in kilometers.
     * @return The meridional wind speed in meters per second (m/s).
     */
    public static double calculateMeridionalWind(double distanceToTitan) {
        if (distanceToTitan < 1) {
            return -0.9 * Math.sin(Math.PI * distanceToTitan);
        } else if (distanceToTitan < 20) {
            return 0.4 * Math.sin(Math.PI * (distanceToTitan - 1) / 19);
        } else {
            return 0.1;
        }
    }

    /**
     * Calculates vertical (up-down) wind speed based on altitude.
     * @param distanceToTitan altitude in kilometers
     * @return vertical wind speed (m/s)
     *
     */
    public static double calculateVerticalWind(double distanceToTitan) {
//       every 50 km it completes a full sine wave cycle
        double sineWaveCycle=50;
        return 0.01 * Math.sin(2 * Math.PI * distanceToTitan / sineWaveCycle);
    }

    private static double applyNoise(double value, double noiseFactor) {
        return value * (1 + (random.nextDouble() * 2 - 1) * noiseFactor);
    }

    public static Vector addRandomNoise(Vector windVector) {
        return addRandomNoise(windVector, DEFAULT_NOISE_FACTOR);
    }

    public static Vector addRandomNoise(Vector windVector, double noiseFactor) {
        return new Vector(
                applyNoise(windVector.getX(), noiseFactor),
                applyNoise(windVector.getY(), noiseFactor),
                applyNoise(windVector.getZ(), noiseFactor)
        );
    }


        public static void main(String[] args) {
//        test cases to see if it matches with the data
            double[] distanceToTitan = {0, 0.5, 1, 10, 20, 50,75, 100,200, 300,450, 500, 600};

            System.out.println("Altitude (km) | Zonal (m/s) | Meridional (m/s) | Vertical (m/s) | Wind Vector (with noise)");
            System.out.println("-----------------------------------------------------------------------------------------");

            for (double altitude : distanceToTitan) {
                Vector windVector = Wind.getWindVector(altitude);
                double zonal = Wind.calculateZonalWind(altitude);
                double meridional = Wind.calculateMeridionalWind(altitude);
                double vertical = Wind.calculateVerticalWind(altitude);

                System.out.printf("%12.1f | %11.3f | %15.3f | %13.5f | (%.3f, %.3f, %.5f)%n",
                        altitude, zonal, meridional, vertical,
                        windVector.getX(), windVector.getY(), windVector.getZ());
            }
        }
    }


