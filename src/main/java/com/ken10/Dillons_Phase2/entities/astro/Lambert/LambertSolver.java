package com.ken10.Dillons_Phase2.entities.astro.Lambert;

import com.ken10.Dillons_Phase2.entities.Other.Vector;
import java.time.LocalDateTime;
import static com.ken10.Dillons_Phase2.entities.astro.Lambert.LambertEquations.*;

public class LambertSolver {
    public static final double ERROR_TOLERANCE = 1e-8;
    public static final double MAX_ITERATIONS = 50;


    /**
     * Solve Lambert’s problem.
     *
     * @param r1       initial position vector (km)
     * @param r2       final   position vector (km)
     * @param dTime       time‑of‑flight, seconds  (positive ⇒ forward propagation)
     * @param mu       gravitational parameter of central body (km³/s²)
     *                 if Earth that is gConstant*earthMass
     *                 if Titan gConstatnt * 0.138 * titanMass
     * @param prograde true  ⇒ use smaller transfer angle  (Δν ≤ π)
     *                 false ⇒ use larger  transfer angle  (Δν > π)
     *
     * @return LambertResult carrying (vDepart, vArrive)
     * @throws RuntimeException if the Newton iteration does not converge
     *                          or if r1/r2 are co‑linear (no unique orbit).
     */
    public LambertResult solve(Vector r1, Vector r2,
                               LocalDateTime dTime, double mu,
                               boolean prograde){
        double deltaTime = dTime.getSecond();
        double r1distance = r1.magnitude();
        double r2distance = r2.magnitude();
        // angle between r1 and r2
        double cosNu = LambertEquations.cosNu(r1,r2);
        double sinNu = LambertEquations.sinNu(r1,r2);

        if(!prograde) sinNu = -sinNu;   // flip sign for retrograde path

        double A = parameterA(r1,r2);

        if(Math.abs(A) < 1e-12)
            throw new RuntimeException("Lambert: r1 & r2 are nearly collinear");

        //------------------ Newton iteration on z --------------------------
        double z     =  0.0;   // universal‑variable free parameter
        double ratio =  1.0;
        int    iter  =  0;

        while(Math.abs(ratio) > ERROR_TOLERANCE && iter++ < MAX_ITERATIONS){

            double C = Stumpff.C(z);
            double S = Stumpff.S(z);

            // Eq. 5‑18 in Vallado: y(z)
            double y = r1distance + r2distance + A * (z*S - 1.0)/Math.sqrt(C);
            if(y < 0){                 // x would be imaginary – push z upward
                z += 0.1;              // small positive increment
                continue;
            }

            double x     = Math.sqrt( y / C );                    // Eq. 5‑17
            double dtEst = ( x*x*x*S + A*Math.sqrt(y) )/Math.sqrt(mu);

            // derivative d(dt)/dz  (Lancaster & Blanchard, eq. 14)
            double dtdz  = ( x*x*x*(0.5/C*(1.0 - z*S)/S)
                    + A/8.0*(3.0*z*S - 1.0)/C ) / Math.sqrt(mu);

            ratio = (dtEst - deltaTime) / dtdz;
            z    -= ratio;          // Newton update
        }
        if(iter >= MAX_ITERATIONS) {
            throw new RuntimeException("Lambert: iteration did not converge");
        }
        //------------------ Lagrange f‑g coefficients ----------------------
        double C = Stumpff.C(z);
        double S = Stumpff.S(z);
        double y = r1distance + r2distance + A*(z*S - 1.0)/Math.sqrt(C);

        double f    = 1.0 - y / r1distance;
        double g    = A   * Math.sqrt( y / mu );
        double gDot = 1.0 - y / r2distance;

        //------------------ build output vectors --------------------------
        Vector vDepart = r2.subtract(r1.multiply(f) ).multiply( 1.0 / g );      // km/s
        Vector vArrive = r2.multiply(gDot).subtract( r1 ).multiply( 1.0 / g );

        // Stuff both outputs into a single, immutable record and hand back
        return new LambertResult(vDepart, vArrive);
    }
}
