package com.ken10;
import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        EphemerisLoader eph = new EphemerisLoader(2);

        //we have an alternative constructor for EphemerisLoader if you want to have a different timeframe
        //of the history or have a different initialState.

        //Example:
        //new EphemerisLoader(ArrayList<CelestialBodies> initialState,
        //                      LocalDateTime startTime,
        //                      LocalDateTime endTime,
        //                      int StepSizeMins)


        // in order to save the history you need to call the method solve on the created object.
        // once it's called you can access any state at given time in O(1)
        eph.solve();

        LocalDateTime t0 = LocalDateTime.of(2025, 4, 1, 0, 0, 0);

        for (int i = 0; i <= 365 * 24; i++) {
            //here you print a state for every hour
            if (eph.history.get(t0.plusHours(i)) == null) continue;
            eph.printState(eph.history.get(t0.plusHours(i)), t0.plusHours(i));

            //you can get every history of every state that is divisible by stepSizeMins
        }

        /* This would print states for each day

        for (int i = 0; i <=365; i++){
           if(eph.history.get(t0.plusDays(i)) == null) continue;
           eph.printState(eph.history.get(t0.plusDays(i)), t0.plusDays(i));
      }
*/

            //if you want to get a specific body state/vector at a specific time state -
            // here is Titan for example at time t0
            CelestialBodies titan = eph.history.get(t0).get(BodyID.TITAN.index());

            titan.printBody();

            //here is a position vector of Titan at t0
            Vector titanPosition = titan.getPosition();

            System.out.println("Titan position Vector at: " +
                    t0 + ", "
                    + titanPosition.toString());
            //and so on...
        }
    }
