package com.ken10.Entities;

import com.ken10.Other.Vector;

public class LaunchState implements RocketState {
    @Override
    public void handle(Rocket rocket) {
        rocket.velocity = new Vector(0,0,0);
    }
}
