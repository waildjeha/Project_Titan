package com.ken10.Dillons_Phase2.entities.astro.Lambert;

import com.ken10.Dillons_Phase2.entities.Other.Vector;

public class LambertEquations {

    public static double cosNu(Vector v1, Vector v2) {
        double r1distance = v1.magnitude();    // |r1|
        double r2distance = v2.magnitude();    // |r2|
        double cosNu = v1.dot(v2) / (r1distance * r2distance);
        cosNu = Math.max(-1.0, Math.min(1.0, cosNu));
        return cosNu;
    }

    public static double sinNu(Vector v1, Vector v2) {
        return Math.sqrt(1 - Math.pow(cosNu(v1, v2), 2));
    }

    public static double parameterA(Vector v1, Vector v2) {
        return sinNu(v1, v2) * Math.sqrt(v1.magnitude() * v2.magnitude() / (1.0 - cosNu(v1, v2)));
    }

    public static class Stumpff {
        public static double C(double z) {
            if (Math.abs(z) < 1E-8)    // series around 0 avoids /0
                return 0.5 - z / 24.0 + z * z / 720.0;
            if (z > 0) return (1 - Math.cos(Math.sqrt(z))) / z;
            else return (Math.cosh(Math.sqrt(-z)) - 1) / -z;
        }

        public static double S(double z) {
            if (Math.abs(z) < 1E-8)
                return 1.0 / 6.0 - z / 120.0 + z * z / 5040.0;
            if (z > 0) {
                double s = Math.sqrt(z);
                return (s - Math.sin(s)) / (s * s * s);
            } else {
                double s = Math.sqrt(-z);
                return (Math.sinh(s) - s) / (s * s * s);
            }
        }
    }
}
