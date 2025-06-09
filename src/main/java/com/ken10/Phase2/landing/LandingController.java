package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.Vector;


public class LandingController {
    private boolean landing = false;

    private boolean aligned = false;

    private double lastRotationVelocity = 0;

    public void execute(LandingModule module, int currentStep, double stepSize) {
        module.setThrust(new Vector2(0, 0));

        double rotationThrust;
        double mainThrust;

        Vector2 target = new Vector2(0,40);
        if (module.getY() < 42) target = new Vector2(0,20);
        if (module.getY() < 21) target = new Vector2(0,5);
        if (module.getY() < 5.2) target = new Vector2(0,0.1);
        //if (module.getY() < 0.11) target = new Vector2(0,-1);
        // if (module.getY() < 0.05) target = new Vector2(0,-1);
        if (module.getY() < 4 || landing) {
            landing = true;
            land(module);
            return;
        }

        double angle = directionOfAngleTarget(module, target) * Math.toDegrees(angleVelocityTarget(module, target));

        if (module.getY() < 0.05) angle = 0;
        if (aligned) angle = 0;

        rotationThrust = rotationThrustToReachAngle(angle, module);


        mainThrust = calculateThrust(module, angle);

        module.setThrust(new Vector2(mainThrust, rotationThrust));
    }

    private double calculateThrust(LandingModule module, double angle) {

        if (module.state.getVy() > 0) return 0;
        if (module.state.getY() < 16.4503) return slowDown(module);

        if (module.state.getY() > 150) return 0;

        double thrust = module.getTotalSpeed() * 2 * Math.sin(Math.toRadians(angle)/2);

        return Math.abs(thrust);

    }

    private void land(LandingModule module) {
        if (module.state.getVy() > 0) return;
        double angle = 0;
        double yVelocity = Math.abs(module.state.getVy());

        double rotationThrust = rotationThrustToReachAngle(angle, module);
        double mainThrust = LandingGravitationFunction.GRAVITATIONAL_ACCELERATION*0.5;
        if (module.getTotalSpeed() > 0.0003) {
            mainThrust = yVelocity/3.5 + LandingGravitationFunction.GRAVITATIONAL_ACCELERATION*0.95;
            if (module.getY() < 0.0002) {
                mainThrust = yVelocity + LandingGravitationFunction.GRAVITATIONAL_ACCELERATION;
            }
        }

        module.setThrust(new Vector2(mainThrust, rotationThrust));
    }



    private double slowDown(LandingModule module) {
        if (module.state.getVy() > 0) return 0;

        double yVelocity = Math.abs(module.state.getVy());
        if (yVelocity < 0.0003) return LandingGravitationFunction.GRAVITATIONAL_ACCELERATION * 0.95;

        if (module.getY() < 0.025) return yVelocity + LandingGravitationFunction.GRAVITATIONAL_ACCELERATION * 0.95;

        double result = yVelocity/2.5 + LandingGravitationFunction.GRAVITATIONAL_ACCELERATION * 0.95;

        return result;
    }


    private double rotationThrustToReachAngle(double desiredAngle, LandingModule module) {
        double rotationThrust = 0;
        double angleDifference = module.getRotationAngle() - desiredAngle;

        double rotationVelocity = module.state.getOmega();

        if (angleDifference < -0.5 && rotationVelocity < 1) rotationThrust = 1;
        else if (angleDifference < -90 && rotationVelocity < 2) rotationThrust = 1;
        else if (angleDifference > 0.5 && rotationVelocity > -1) rotationThrust = -1;
        else if (angleDifference > 90 && rotationVelocity > -2) rotationThrust = -1;

        lastRotationVelocity = rotationVelocity;
        return rotationThrust;
    }

    private double angleVelocityTarget(LandingModule module, Vector2 target) {
        Vector2 velocity = new Vector2(module.state.getVx(), module.state.getVy());
        Vector2 modulePosition = new Vector2(module.state.getX(), module.state.getY());
        target = target.subtract(modulePosition);

        double ADotBOverATimesB = velocity.dotProduct(target)
                / (velocity.getLength()*target.getLength());

        return Math.acos(ADotBOverATimesB);
    }

    private int directionOfAngleTarget(LandingModule module, Vector2 target) {
        double a = (module.getY() - target.getY()) / (module.getX() - target.getX());
        double ax_velocity = a * module.state.getVx();
        return (a * (module.state.getVy()-ax_velocity) < 0)? -1 : 1;
    }


    
}
