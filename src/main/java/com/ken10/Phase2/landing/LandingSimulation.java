package com.ken10.Phase2.landing;


public class LandingSimulation
{
    private final static double g = 1.352 * 1e-3;
    private final static double maxAcceleration = 10 * g;
    private final static double maxTorque = 1;

    private final static double positionXTolerance = 1e-4;
    private final static double positionYTolerance = 0;

    private final static double velocityXTolerance = 1e-4;
    private final static double velocityYTolerance = 1e-4;

    private final static double rotationTolerance = 0.02;
    private final static double torqueTolerance = 0.01;

    private final Vector2 startPosition;
    private final Vector2 startVelocity;
    private final double startRotation;

    private Vector2 currentPosition;
    private Vector2 currentVelocity;

    private double acceleration;

    private double currentRotation;
    private double currentTorque;

    private double targetRotation;
    private double windFactor=0.5;

    public LandingSimulation(Vector2 startPosition, Vector2 startVelocity, double startRotation)
    {
        this.startPosition = startPosition;
        this.startVelocity = startVelocity;
        this.startRotation = startRotation;

        this.currentPosition = startPosition;
        this.currentVelocity = startVelocity;
        this.currentRotation = startRotation;
    }

    public void run()
    {
        System.out.printf("Starting run at: Position: %s Velocity: %s Rotation: %s%n",
                currentPosition.toString(),
                currentVelocity.toString(),
                currentRotation);

        while (!isLanded())
        {
            currentVelocity = Vector2.ZERO;
            currentTorque = 0;

            if (!isXReached())
            {
                double xDifference = currentPosition.getX();
                targetRotation = xDifference >= 0 ? -90 : 90;

                if (currentRotation == targetRotation)
                {
                    acceleration = Math.min(xDifference, maxAcceleration);
                }
                else
                {
                    acceleration = 0;
                }
            }
            else if (!isYReached())
            {
                double yDifference = currentPosition.getY();
                targetRotation = yDifference >= 0 ? 180 : 0;

                if (currentRotation == targetRotation)
                {
                    acceleration = Math.min(maxAcceleration, Math.max(0, -yDifference));
                }
                else
                {
                    acceleration = 0;
                }
            }
            else if (!isRotationReached())
            {
                targetRotation = 0;
                acceleration = 0;
            }

            double rotationDifference = targetRotation - currentRotation;
            currentTorque = clamp(rotationDifference, -maxTorque, maxTorque);

            move();
            rotate();

            System.out.printf("Landing Iteration: Position: %s Velocity: %s Acceleration: %s Rotation: %s Target Rotation: %s Torque: %s%n",
                    currentPosition.toString(),
                    currentVelocity.toString(),
                    acceleration,
                    currentRotation,
                    targetRotation,
                    currentTorque);
        }

        System.out.printf("Finished run at: Position: %s Velocity: %s Rotation: %s%n",
                currentPosition.toString(),
                currentVelocity.toString(),
                currentRotation);
    }

    private void move()
    {
        double radians = Math.toRadians(currentRotation);
        double x = acceleration * Math.sin(radians);
        double y = acceleration * Math.cos(radians) - g;
        currentVelocity = new Vector2(x, y);

        currentVelocity=Wind.applyWindVector(currentVelocity,windFactor);
        currentPosition = currentPosition.add(currentVelocity);

        if (Math.abs(currentPosition.getY()) < 1e-3 + 0.001) {
            currentPosition = new Vector2(currentPosition.getX(), 0);
            currentVelocity = new Vector2(currentVelocity.getX(), 0);
        }

    }

    private void rotate()
    {
        currentRotation += currentTorque;
    }

    private boolean isLanded()
    {
        return isXReached()
                && isYReached()
                && Math.abs(currentVelocity.getX()) <= velocityXTolerance
                && Math.abs(currentVelocity.getY()) <= velocityYTolerance
                && isRotationReached()
                && Math.abs(currentTorque) <= torqueTolerance;
    }

    private boolean isRotationAligned() { return currentRotation == targetRotation; }

    private boolean isXReached() { return Math.abs(currentPosition.getX()) <= positionXTolerance; }
    private boolean isYReached() {return Math.abs(currentPosition.getY()) <= positionYTolerance; }
    private boolean isRotationReached() { return Math.abs(currentRotation) <= rotationTolerance; }

    private double clamp(double value, double min, double max)
    {
        return Math.max(min, Math.min(value, max));
    }

    public static void main(String[] args)
    {
        Vector2 startPosition = new Vector2(10, 1000);
        Vector2 startVelocity = Vector2.ZERO;
        double startRotation = 0;
        LandingSimulation landingSimulation = new LandingSimulation(startPosition, startVelocity, startRotation);
        landingSimulation.run();
    }
}
