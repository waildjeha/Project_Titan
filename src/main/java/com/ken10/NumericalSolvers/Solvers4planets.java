/*
package com.ken10.NumericalSolvers;

import com.ken10.Entities.GravityCalc;
import com.ken10.Entities.Planets.PlanetModel;
import com.ken10.Entities.State;
import com.ken10.Other.*;

import java.util.List;


public class Solvers4planets {
    public static void RK4(PlanetModel planet, List<PlanetModel> planets, double dt) {
        Vector orgPosition = planet.getPosition();
        Vector orgVelocity = planet.getVelocity();
        State originalState = new State(orgPosition, orgVelocity);

        Derivative k1 = GravityCalc.computeDerivative(planet,planets);
        State firstState = new State(k1.positionDerivative, k1.velocityDerivative);

        State tempState = originalState.add(firstState.multiply(0.5 * dt));
        planet.setPosition(tempState.position);
        planet.setVelocity(tempState.velocity);

        Derivative k2 = GravityCalc.computeDerivative(planet,planets);
        State secondState = new State(k2.positionDerivative, k2.velocityDerivative);

        State tempState2 = secondState.add(secondState.multiply(0.5 * dt));
        planet.setPosition(tempState2.position);
        planet.setVelocity(tempState2.velocity);

        Derivative k3 = GravityCalc.computeDerivative(planet,planets);
        State thirdState = new State(k3.positionDerivative, k3.velocityDerivative);

        State tempState3 = thirdState.add(thirdState.multiply(dt));
        planet.setPosition(tempState3.position);
        planet.setVelocity(tempState3.velocity);

        Derivative k4 = GravityCalc.computeDerivative(planet,planets);
        State fourthState = new State(k4.positionDerivative, k4.velocityDerivative);

        State total = firstState.add(secondState.multiply(2))
                .add(thirdState.multiply(2))
                .add(fourthState)
                .multiply(1.0/6.0);

        planet.setPosition(orgPosition.add(total.position.multiply(dt)));
        planet.setVelocity(orgVelocity.add(total.velocity.multiply(dt)));

    }
}
*/
