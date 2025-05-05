package com.ken10.Phase2.GUI;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.*;
import com.ken10.Phase2.ProbeMission.*;
import com.ken10.Phase2.GUI.Line3D;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;

public class GUI extends Application {

    // Constants for simulation and visualization
    private static final double SIMULATION_TIME_STEP = 86400; // One day in seconds
    private static final double SCALE_FACTOR = 1e-7; // Scale down astronomical distances
    private static final double DEFAULT_PLANET_SIZE = 5.0; // Default size of planets in visualization
    private static final double SUN_SIZE = 15.0; // Size of sun in visualization
    // private static final double PATH_LENGTH = 500; // Number of points to keep in orbit path
    private static final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> timeStates = getStatesInTime();

    // JavaFX UI components
    private BorderPane root;
    private SubScene space;
    private Group celestialGroup;
    private Group pathGroup;
    private Slider speedSlider;
    private Label timeLabel;
    
    // Simulation components
    // private RK4Solver solver;
    private ArrayList<CelestialBodies> celestialBodies;
    private Map<String, Sphere> planetSpheres = new HashMap<>();
    private Map<String, Group> planetPaths = new HashMap<>();
    private Map<String, List<Vector>> pathHistory = new HashMap<>();
    private double simulationTime = 0;
    private double simulationSpeed = 10.0;
    
    // Camera control
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Scale scale = new Scale(1.0, 1.0, 1.0);
    private final Translate translate = new Translate(0, 0, 0);
    
    @Override
    public void start(Stage primaryStage) {
        
        // Initialize the 3D scene
        initializeScene();
        
        // Create UI controls
        VBox controls = createControls();
        
        // Set up the main layout
        root = new BorderPane();
        root.setCenter(space);
        root.setBottom(controls);
        
        // Set up the main scene and stage
        Scene scene = new Scene(root, 1200, 800, true);
        primaryStage.setTitle("Titan Mission Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Add camera controls
        setupCameraControls();
        
        // Start the animation
        startAnimation();
    }

    public static Hashtable<LocalDateTime, ArrayList<CelestialBodies>> getStatesInTime(){
        EphemerisLoader eph = new EphemerisLoader(2);
        eph.solve();
        return eph.history;
    }
    
    private void initializeScene() {
        celestialGroup = new Group();
        pathGroup = new Group();
        
        // Create a group to hold both celestial bodies and their paths
        Group worldGroup = new Group();
        worldGroup.getChildren().addAll(pathGroup, celestialGroup);
        
        // Initialize the celestial bodies' visual representation
        initializeCelestialBodies();

        // Add the coordinate axes
        addCoordinateAxes();
        
        // Create the 3D subscene
        space = new SubScene(worldGroup, 1200, 700, true, null);
        space.setFill(Color.BLACK);
        
        // Center the view
        translate.setX(600);  // Half of width
        translate.setY(350);  // Half of height

        // Add transformations to the world group for camera control
        worldGroup.getTransforms().addAll(translate, rotateX, rotateY, scale);
    }

    private void addCoordinateAxes() {
        double axisLength = 100;
        
        // Create X axis (Red)
        Cylinder xAxisPositive = createAxis(axisLength, Color.RED);
        xAxisPositive.setRotationAxis(Rotate.Z_AXIS);
        xAxisPositive.setRotate(90);
        xAxisPositive.setTranslateX(axisLength/2);
        
        Cylinder xAxisNegative = createAxis(axisLength, Color.RED.darker());
        xAxisNegative.setRotationAxis(Rotate.Z_AXIS);
        xAxisNegative.setRotate(-90);
        xAxisNegative.setTranslateX(-axisLength/2);
        
        // Create Y axis (Green)
        Cylinder yAxisPositive = createAxis(axisLength, Color.GREEN);
        yAxisPositive.setTranslateY(-axisLength/2);
        
        Cylinder yAxisNegative = createAxis(axisLength, Color.GREEN.darker());
        yAxisNegative.setRotate(180);
        yAxisNegative.setTranslateY(axisLength/2);
        
        // Create Z axis (Blue)
        Cylinder zAxisPositive = createAxis(axisLength, Color.BLUE);
        zAxisPositive.setRotationAxis(Rotate.X_AXIS);
        zAxisPositive.setRotate(90);
        zAxisPositive.setTranslateZ(axisLength/2);
        
        Cylinder zAxisNegative = createAxis(axisLength, Color.BLUE.darker());
        zAxisNegative.setRotationAxis(Rotate.X_AXIS);
        zAxisNegative.setRotate(-90);
        zAxisNegative.setTranslateZ(-axisLength/2);
        
        // Add labels
        Label xLabel = createAxisLabel("X", Color.RED);
        xLabel.setTranslateX(axisLength + 5);
        
        Label yLabel = createAxisLabel("Y", Color.GREEN);
        yLabel.setTranslateY(-axisLength - 5);
        
        Label zLabel = createAxisLabel("Z", Color.BLUE);
        zLabel.setTranslateZ(axisLength + 5);
        
        // Create a small sphere at origin to mark (0,0,0)
        Sphere originMarker = new Sphere(1.5);
        PhongMaterial originMaterial = new PhongMaterial(Color.WHITE);
        originMarker.setMaterial(originMaterial);
        
        // Add all axes to a group
        Group axesGroup = new Group();
        axesGroup.getChildren().addAll(
            xAxisPositive, xAxisNegative, 
            yAxisPositive, yAxisNegative, 
            zAxisPositive, zAxisNegative, 
            xLabel, yLabel, zLabel, originMarker);
        
        // Add axes to the world
        celestialGroup.getChildren().add(axesGroup);
    }
    
    private Cylinder createAxis(double length, Color color) {
        Cylinder axis = new Cylinder(0.5, length);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        axis.setMaterial(material);
        return axis;
    }
    
    private Label createAxisLabel(String text, Color color) {
        Label label = new Label(text);
        label.setTextFill(color);
        return label;
    }
    
    private void initializeCelestialBodies() {
        // Process each celestial body
        for (CelestialBodies body : celestialBodies) {
            // Create a sphere to represent the celestial body
            double radius = body.getName().equalsIgnoreCase("sun") ? SUN_SIZE : DEFAULT_PLANET_SIZE;
            Sphere sphere = new Sphere(radius);
            
            // Set the material and color based on the body type
            PhongMaterial material = new PhongMaterial();
            Color color = getPlanetColor(body.getName());
            material.setDiffuseColor(color);
            sphere.setMaterial(material);
            
            // Position the sphere
            updateCelestialBodyPosition(sphere, body);
            
            // Add to the scene
            celestialGroup.getChildren().add(sphere);
            planetSpheres.put(body.getName(), sphere);
            
            // Initialize path history and visual representation
            pathHistory.put(body.getName(), new ArrayList<>());
            Group pathLines = new Group();
            pathGroup.getChildren().add(pathLines);
            planetPaths.put(body.getName(), pathLines);
        }
    }
    
    private Color getPlanetColor(String name) {
        switch (name.toLowerCase()) {
            case "sun": return Color.YELLOW;
            case "mercury": return Color.DARKGRAY;
            case "venus": return Color.SANDYBROWN;
            case "earth": return Color.BLUE;
            case "moon": return Color.LIGHTGRAY;
            case "mars": return Color.RED;
            case "jupiter": return Color.ORANGE;
            case "saturn": return Color.GOLDENROD;
            case "titan": return Color.ORANGE.darker();
            case "uranus": return Color.LIGHTBLUE;
            case "neptune": return Color.DARKBLUE;
            default: return Color.WHITE;
        }
    }
    
    private void updateCelestialBodyPosition(Sphere sphere, CelestialBodies body) {
        Vector position = body.getPosition();
        sphere.setTranslateX(position.getX() * SCALE_FACTOR);
        sphere.setTranslateY(position.getY() * SCALE_FACTOR);
        sphere.setTranslateZ(position.getZ() * SCALE_FACTOR);
    }
    private void updatePathVisualization(String bodyName, Vector position) {
        // Skip trajectory for the sun
        if (bodyName.equalsIgnoreCase("sun")) {
            return;
        }
    
        // Update path history
        List<Vector> history = pathHistory.get(bodyName);
        history.add(new Vector(position.getX(), position.getY(), position.getZ()));
        
        // Limit path length
        if (history.size() > PATH_LENGTH) {
            history.remove(0);
        }
        
        // Update path visualization
        Group pathLines = planetPaths.get(bodyName);
        pathLines.getChildren().clear();
        
        if (history.size() >= 2) {
            // Create a path using simple Line objects
            for (int i = 0; i < history.size() - 1; i++) {
                Vector start = history.get(i);
                Vector end = history.get(i + 1);
                
                double startX = start.getX() * SCALE_FACTOR;
                double startY = start.getY() * SCALE_FACTOR;
                double startZ = start.getZ() * SCALE_FACTOR;
                double endX = end.getX() * SCALE_FACTOR;
                double endY = end.getY() * SCALE_FACTOR;
                double endZ = end.getZ() * SCALE_FACTOR;
                
                // Use JavaFX Line3D (custom class we'll create)
                Line3D line = new Line3D(startX, startY, startZ, endX, endY, endZ, 
                                        getPlanetColor(bodyName).deriveColor(0, 1, 1, 0.7));
                pathLines.getChildren().add(line);
            }
        }
    }
    
    private VBox createControls() {
        // Create speed slider
        speedSlider = new Slider(0.1, 10.0, 1.0);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> simulationSpeed = newVal.doubleValue());
        
        // Create time label
        timeLabel = new Label("Simulation Time: 0 days");
        
        // Create layout for controls
        HBox sliderBox = new HBox(10, new Label("Simulation Speed:"), speedSlider);
        sliderBox.setPadding(new Insets(10));
        
        VBox controls = new VBox(10, sliderBox, timeLabel);
        controls.setPadding(new Insets(10));
        
        return controls;
    }
        
    
    private void setupCameraControls() {
        // Mouse press handler to capture initial mouse position
        space.setOnMousePressed(event -> {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });
        
        // Mouse drag handler for rotation
        space.setOnMouseDragged(event -> {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            
            // Calculate the rotation angle based on mouse movement
            rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX) * 0.2);
            rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY) * 0.2);
            
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });
        
        // Mouse scroll handler for zoom
        space.setOnScroll(event -> {
            double delta = event.getDeltaY() * 0.01;
            scale.setX(scale.getX() + delta);
            scale.setY(scale.getY() + delta);
            scale.setZ(scale.getZ() + delta);
        });
    }
    
    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                // Update simulation at a controlled rate (not every frame)
                if (lastUpdate == 0 || (now - lastUpdate) >= 16_000_000) { // ~60 fps
                    updateSimulation();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }
    
    private void updateSimulation() {
        // Step the simulation forward based on the simulation speed
        for (int i = 0; i < simulationSpeed; i++) {
            solver.step();
            simulationTime += SIMULATION_TIME_STEP;
        }
        
        // Update the visualization
        for (CelestialBodies body : celestialBodies) {
            // Update planet positions
            Sphere sphere = planetSpheres.get(body.getName());
            updateCelestialBodyPosition(sphere, body);
            
            // Update orbit paths
            updatePathVisualization(body.getName(), body.getPosition());
        }
        
        // Update the time label
        double daysElapsed = simulationTime / 86400; // Convert seconds to days
        timeLabel.setText(String.format("Simulation Time: %.1f days (%.2f years)", daysElapsed, daysElapsed / 365.25));
    }
    
    public static void main(String[] args) {
        getStatesInTime();
        // launch(args);
    }

}

