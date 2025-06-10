# Project 1.2: Manned Mission to Titan Simulation

This project simulates a manned mission to Titan, one of Saturn's moons, and back to Earth. It features:

* **Ordinary Differential Equation (ODE) Solver:** For accurate trajectory calculations.
* **Graphical User Interface (GUI):** Visualizes the trajectories of planets and the spaceship.
* **Orbital Mechanics Simulation:** Accurately simulates bringing the spaceship into orbit.

## System Requirements

This project is designed to run on most systems, as it doesn't have intensive performance requirements. However, you must have a Java Development Kit (JDK) installed.

## Getting Started

Follow these steps to run the simulation:

**1. Install Maven:**

* Ensure you have Maven installed and configured in your system's PATH environment variable.
* Download Maven from the official [Apache Maven website](https://maven.apache.org/).
* Follow the installation instructions for your operating system.

**2. Clone the Repository:**

* Open your preferred command-line terminal (e.g., Terminal on macOS/Linux, Command Prompt or PowerShell on Windows).
* Use the following command to clone the repository:

    ```bash
    git clone https://github.com/waildjeha/Project_Titan
    ```

**3. Navigate to the Cloned Folder:**

* Use the `cd` command to navigate to the cloned project directory:

    ```bash
    cd Project_titan
    ```

    * Alternatively, you can open the cloned folder directly in your preferred Integrated Development Environment (IDE), such as IntelliJ IDEA or Eclipse.

**4. Build and Run the Project:**

* **Clean the Project:**
    * To clean any previously built artifacts, run the following Maven command:

        ```bash
        mvn clean
        ```

* **Run the Simulation:**

    * To run the GUI from the command line, use the following Maven command:

            mvn javafx:run

* **Find optimal Velocity to reach any planet from a position:**
* Example usage pseudocode:
* LaunchData launchData = new LaunchData(BodyID destination, 
* BodyID launchPlanet, 
* Vector initialPosition, 
* LocalDateTime launchTime, 
* LocalDateTime endTime, 
* ArrayList<CelestialBodies> initialState);
  RK4Probe simulation;
  OptimalVelocityFinder optimalVelocityFinder = new OptimalVelocityFinder(launchData);
  optimalVelocityFinder.solve();
  simulation = optimalVelocityFinder.getFoundData();
* To visualize found data just print the "simulation"
