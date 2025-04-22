package com.ken10.Dillons_Phase2.entities.astro.Launch;

import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.astro.Emhemeris.EphemerisLoader;
import com.ken10.Dillons_Phase2.entities.astro.Lambert.LambertResult;
import com.ken10.Dillons_Phase2.entities.astro.Lambert.LambertSolver;
import com.ken10.Dillons_Phase2.entities.planet.BodyID;

import java.time.LocalDateTime;

public class LaunchWindowScanner {
    private static final double MU_SUN   = 1.32712440018E11;   // km³/s²
    private static final double MU_EARTH = 3.986004418E5;      // km³/s²
    private static final double R_EARTH  = 6370;           // km
    private static final double V_INF_CAP= 60.0;               // km/s

    private final EphemerisLoader eph;
    private final LambertSolver solver = new LambertSolver();
    public LaunchWindowScanner(EphemerisLoader eph) {this.eph = eph;}


    public static final class Solution {

        private final java.time.LocalDateTime launchUTC;
        private final java.time.LocalDateTime arrivalUTC;
        private final Vector v0_ECI;   // Earth‑centred inertial launch vector

        public Solution(java.time.LocalDateTime launchUTC,
                        java.time.LocalDateTime arrivalUTC,
                        Vector v0_ECI){
            this.launchUTC  = launchUTC;
            this.arrivalUTC = arrivalUTC;
            this.v0_ECI     = v0_ECI;
        }
        public java.time.LocalDateTime getLaunchUTC (){ return launchUTC;  }
        public java.time.LocalDateTime getArrivalUTC(){ return arrivalUTC; }
        public Vector                 getV0_ECI    (){ return v0_ECI;     }
    }


    public Solution findBest(){
        LocalDateTime launchTime = LocalDateTime.of(2025,4,1,0,0, 0);
        //TODO for better optimization we can use BST
        //TODO we'd have to edit also the EphemerisLoader for it to make sense
        for(int tof=200; tof<=365; tof+=1){         // TOF sweep
            LocalDateTime arrive = launchTime.plusDays(tof);

            Vector rE = eph.position (BodyID.EARTH,  launchTime.plusDays(1));
            Vector vE = eph.velocity (BodyID.EARTH,  launchTime.plusDays(1));
            Vector rT = eph.position (BodyID.TITAN,  arrive);

            try{
                LambertResult lam = solver.solve(
                        rE, rT, launchTime.plusDays(tof), MU_SUN, true);

                Vector vInf = lam.vDepart().subtract(vE);
                double vInfMag = vInf.magnitude();
                if(vInfMag > V_INF_CAP) continue;          // fails cap

                // surface launch speed
                double v0mag = Math.sqrt(vInfMag*vInfMag + 2*MU_EARTH/R_EARTH);
                Vector v0eci = vInf.normalize().multiply(v0mag);

                return new Solution(launchTime, arrive, v0eci);

            }catch(RuntimeException e){System.err.println("Solver failed."); /* solver failed – skip */ }
        }
        return null;   // nothing in the window
    }
}
