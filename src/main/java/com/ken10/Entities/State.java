package com.ken10.Entities;

import com.ken10.Other.Derivative;
import com.ken10.Other.Vector;

public class State {
    public Vector position;
    public Vector velocity;

    public State(Vector position, Vector velocity) {
        this.position = position;
        this.velocity = velocity;
    }
    public State add(State s) {
        return new State(position.add(s.position), velocity.add(s.velocity));
    }

    public State multiply(double c){
        return new State(this.position.multiply(c), this.velocity.multiply(c));
    }
    public State copy(){
        return new State(position.copy(), velocity.copy());
    }
}
