package com.ken10.Phase2.GUI;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SolarSystemGUI extends Application {

    // Constants for visualization
    private static final double SCALE_FACTOR = 10e-7; // Scale down astronomical distances (adjusted)
    private static final double DEFAULT_PLANET_SIZE = 1.5; // Default size of planets in visualization (slightly larger)
    private static final double SUN_SIZE = 6.0; // Size of sun in visualization (slightly smaller)
    private static final int PATH_LENGTH = 1000; // Number of points to keep in orbit path
    
    // Pre-loaded ephemeris data
    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> timeStates;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime currentTime;
    private List<LocalDateTime> timeKeys;
    private int currentTimeIndex = 0;
    
    // JavaFX UI components
    private BorderPane root;
    private SubScene space;
    private Group celestialGroup;
    private Group pathGroup;
    private Label timeLabel;
    private Button playPauseButton;
    private Slider timeSlider;
    private boolean isPlaying = true; // Start playing by default
    
    // Visualization components
    private ArrayList<CelestialBodies> celestialBodies;
    private Map<String, Sphere> planetSpheres = new HashMap<>();
    private Map<String, Group> planetPaths = new HashMap<>();
    private Map<String, List<Vector>> pathHistory = new HashMap<>();
    private double simulationSpeed = 1.0;
    
    // Camera control
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS); // Initial tilt
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Scale scale = new Scale(1.0, 1.0, 1.0);
    private final Translate translate = new Translate(0, 0, 0);
    
    @Override
    public void start(Stage primaryStage) {
        // Load ephemeris data
        loadEphemerisData();
        
        // Initialize the 3D scene
        initializeScene();
        
        // Create UI controls
        VBox controls = createControls();
        
        // Set up the main layout
        root = new BorderPane();
        root.setCenter(space);
        root.setBottom(controls);
        
        // Set up the main scene and stage
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Solar System Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Add camera controls
        setupCameraControls();
        
        // Start the animation
        startAnimation();
    }

    private void loadEphemerisData() {
        System.out.println("Loading ephemeris data...");
        
        // Initialize ephemeris loader with 60-minute steps (faster loading)
        EphemerisLoader eph = new EphemerisLoader(60);
        eph.solve();
        timeStates = eph.history;
        
        System.out.println("Loaded " + timeStates.size() + " time states");
        
        // Extract start and end times from the data
        timeKeys = new ArrayList<>(timeStates.keySet());
        Collections.sort(timeKeys);
        
        startTime = timeKeys.get(0);
        endTime = timeKeys.get(timeKeys.size() - 1);
        currentTime = startTime;
        
        // Get the initial state of celestial bodies
        celestialBodies = timeStates.get(startTime);
        
        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);
        System.out.println("Loaded " + celestialBodies.size() + " celestial bodies");
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
        space = new SubScene(worldGroup, 1200, 500, true, null);
        space.setFill(Color.BLACK);
        
        // Center the view
        translate.setX(600);  // Half of width
        translate.setY(250);  // Half of height
        translate.setZ(-200); // Initial Z offset for better view

        // Add transformations to the world group for camera control
        worldGroup.getTransforms().addAll(translate, rotateX, rotateY, scale);
    }

    private void addCoordinateAxes() {
        double axisLength = 50;
        
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
        Sphere originMarker = new Sphere(1.0);
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
            String name = body.getName().toLowerCase();
            
            // Skip bodies we don't want to visualize (if any)
            
            // Determine radius based on body type and importance
            double radius;
            if (name.equals("sun")) {
                radius = SUN_SIZE;
            } else if (name.equals("jupiter") || name.equals("saturn")) {
                radius = DEFAULT_PLANET_SIZE * 2.5;
            } else if (name.equals("earth") || name.equals("venus")) {
                radius = DEFAULT_PLANET_SIZE * 1.5;
            } else if (name.equals("titan") || name.equals("moon")) {
                radius = DEFAULT_PLANET_SIZE * 0.7;  
            } else {
                radius = DEFAULT_PLANET_SIZE;
            }
            
            Sphere sphere = new Sphere(radius);
            
            // Set the material and color based on the body type
            PhongMaterial material = new PhongMaterial();
            Color color = getPlanetColor(name);
            material.setDiffuseColor(color);
            sphere.setMaterial(material);
            
            // Position the sphere
            updateCelestialBodyPosition(sphere, body);
            
            // Add to the scene
            celestialGroup.getChildren().add(sphere);
            planetSpheres.put(name, sphere);
            
            // Initialize path history and visual representation
            pathHistory.put(name, new ArrayList<>());
            Group pathLines = new Group();
            pathGroup.getChildren().add(pathLines);
            planetPaths.put(name, pathLines);
            
            System.out.println("Added: " + name + " at position " + body.getPosition());
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
    
    // Simple implementation of Line3D using a thin cylinder
    private class Line3D extends Group {
        public Line3D(double startX, double startY, double startZ, 
                     double endX, double endY, double endZ, Color color) {
            
            // Calculate the length of the line
            double dx = endX - startX;
            double dy = endY - startY;
            double dz = endZ - startZ;
            double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
            // Create a cylinder with the appropriate dimensions
            Cylinder line = new Cylinder(0.1, length);
            
            // Set the material and color
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(color);
            line.setMaterial(material);
            
            // Calculate rotation angles
            Point3D yAxis = new Point3D(0, 1, 0);
            Point3D diff = new Point3D(dx, dy, dz);
            Point3D axisOfRotation = yAxis.crossProduct(diff);
            double angle = Math.acos(yAxis.dotProduct(diff) / length) * 180 / Math.PI;
            
            // Apply transformations
            Translate moveToStart = new Translate(startX, startY, startZ);
            Rotate rotation = new Rotate(angle, axisOfRotation);
            
            // Position at the center of the line
            Translate moveToCenter = new Translate(0, length/2, 0);
            
            // Apply transformations
            this.getTransforms().addAll(moveToStart, rotation, moveToCenter);
            
            // Add the cylinder
            this.getChildren().add(line);
        }
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
        
        // Update path visualization - only rebuild every few frames for better performance
        if (currentTimeIndex % 5 == 0) {
            Group pathLines = planetPaths.get(bodyName);
            pathLines.getChildren().clear();
            
            if (history.size() >= 2) {
                // Create a path using Line3D objects
                for (int i = 0; i < history.size() - 1; i += 2) { // Skip some points for performance
                    Vector start = history.get(i);
                    Vector end = history.get(i + 1);
                    
                    double startX = start.getX() * SCALE_FACTOR;
                    double startY = start.getY() * SCALE_FACTOR;
                    double startZ = start.getZ() * SCALE_FACTOR;
                    double endX = end.getX() * SCALE_FACTOR;
                    double endY = end.getY() * SCALE_FACTOR;
                    double endZ = end.getZ() * SCALE_FACTOR;
                    
                    // Use our simplified Line3D
                    Line3D line = new Line3D(startX, startY, startZ, endX, endY, endZ, 
                                        getPlanetColor(bodyName).deriveColor(0, 1, 1, 0.5));
                    pathLines.getChildren().add(line);
                }
            }
        }
    }
    
    private VBox createControls() {
        // Create time slider
        timeSlider = new Slider(0, 100, 0);
        timeSlider.setPrefWidth(600);
        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isPlaying) {
                // Calculate the time index based on slider position
                int index = (int) (newVal.doubleValue() / 100 * (timeKeys.size() - 1));
                index = Math.max(0, Math.min(index, timeKeys.size() - 1));
                currentTimeIndex = index;
                updateVisualization();
            }
        });
        
        // Create play/pause button
        playPauseButton = new Button("Pause");  // Start in playing state
        playPauseButton.setOnAction(e -> {
            isPlaying = !isPlaying;
            playPauseButton.setText(isPlaying ? "Pause" : "Play");
        });
        
        // Create speed slider
        Slider speedSlider = new Slider(0.1, 5.0, 1.0);
        speedSlider.setPrefWidth(200);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            simulationSpeed = newVal.doubleValue());
        
        // Create time label
        timeLabel = new Label("Simulation Time: " + formatDateTime(currentTime));
        
        // Create layout for controls
        HBox sliderBox = new HBox(10, new Label("Time:"), timeSlider, playPauseButton);
        sliderBox.setPadding(new Insets(10));
        
        HBox speedBox = new HBox(10, new Label("Speed:"), speedSlider);
        speedBox.setPadding(new Insets(10));
        
        VBox controls = new VBox(10, sliderBox, speedBox, timeLabel);
        controls.setPadding(new Insets(10));
        
        return controls;
    }
    
    private String formatDateTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return time.format(formatter);
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
            double newScale = scale.getX() + delta;
            
            // Limit zoom range
            if (newScale > 0.1 && newScale < 10.0) {
                scale.setX(newScale);
                scale.setY(newScale);
                scale.setZ(newScale);
            }
        });
    }
    
    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                // Update simulation at a controlled rate (not every frame)
                if (lastUpdate == 0 || (now - lastUpdate) >= 16_000_000) { // ~60 fps
                    if (isPlaying) {
                        // Advance time index based on simulation speed
                        currentTimeIndex += (int)Math.max(1, simulationSpeed);
                        
                        // Loop back to start if we reach the end
                        if (currentTimeIndex >= timeKeys.size()) {
                            currentTimeIndex = 0;
                        }
                        
                        // Update slider position (without triggering its listener)
                        double sliderValue = (double) currentTimeIndex / (timeKeys.size() - 1) * 100;
                        timeSlider.setValue(sliderValue);
                        
                        // Update visualization
                        updateVisualization();
                    }
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }
    
    private void updateVisualization() {
        // Get the current state from pre-loaded data
        if (currentTimeIndex < 0 || currentTimeIndex >= timeKeys.size()) {
            return;
        }
        
        currentTime = timeKeys.get(currentTimeIndex);
        ArrayList<CelestialBodies> currentState = timeStates.get(currentTime);
        
        if (currentState == null) {
            System.out.println("No data for time: " + currentTime);
            return;
        }
        
        // Update the visualization for each celestial body
        for (CelestialBodies body : currentState) {
            String bodyName = body.getName().toLowerCase();
            
            // Get the sphere for this body
            Sphere sphere = planetSpheres.get(bodyName);
            if (sphere != null) {
                // Update planet positions
                updateCelestialBodyPosition(sphere, body);
                
                // Update orbit paths
                updatePathVisualization(bodyName, body.getPosition());
            }
        }
        
        // Update the time label
        long daysBetween = ChronoUnit.DAYS.between(startTime, currentTime);
        double yearsFraction = daysBetween / 365.25;
        timeLabel.setText(String.format("Simulation Time: %s (%.2f days, %.2f years)", 
                formatDateTime(currentTime), (double)daysBetween, yearsFraction));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}