package com.ken10.Entities.Planets;
import com.ken10.Entities.CelestialBodies;
import com.ken10.Entities.State;
import com.ken10.Other.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Planet case of celestial body.
 * Contains info about time state.
 */
public class PlanetModel extends CelestialBodies {
    public List<StateImage> history = new ArrayList<>();
    public PlanetModel(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }

    public void recordHistory(double time) {
        history.add(new StateImage(time, new State(position.copy(),velocity.copy())));
    }
    public List<StateImage> getHistory() {
        return history;
    }
    public static class StateImage{
        public double time;
        public State state;

        public StateImage(double time, State state){
            this.time = time;
            this.state = state;
        }
    }
    public void update(Vector acceleration, double dt) {
        velocity = velocity.add(acceleration.multiply(dt));
        position = position.add(velocity.multiply(dt));

    }
}