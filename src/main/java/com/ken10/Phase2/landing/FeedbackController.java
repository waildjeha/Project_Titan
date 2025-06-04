package com.ken10.Phase2.landing;

public class FeedbackController {

    private final StateVector goal;

    public FeedbackController(){
        this.goal = new StateVector(0,0,0,0,0,0);

    }

    public ControlOutput controlFunction(StateVector stateVector){
//        TO DO
//        the idea is that based on the error a new thrust parameters are calculated
//        return new ControlOutput(thrustX, thrustY, torque);
//        adjust the values based on the thrust
//        solve using rk4 - the plant function
//        check if landed

return null;

    }
    public StateVector plantFunction(StateVector currentState, ControlOutput control, double dt) {
        // Apply Newton’s and RK4 here to update position/velocity/angle

        return null;
    }


// the sensor function this is how NASA called it is meant to check if the goal is reached
    public boolean sensorFunction(StateVector stateVector ){
        return stateVector.hasLandedSafely(goal);
    }

}