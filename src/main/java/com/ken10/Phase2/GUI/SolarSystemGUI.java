package com.ken10.Phase2.GUI;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.*;
import javafx.scene.Group;
import javafx.scene.Node;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
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

import java.io.InputStream;
import java.net.URL;
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
    private static final double DEFAULT_PLANET_SIZE = 15; // Default size of planets in visualization (slightly larger)
    private static final double SUN_SIZE = 25.0; // Size of sun in visualization (slightly smaller)
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
    private double simulationSpeed = 1000;
    
    // Camera control
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS); // Initial tilt
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Scale scale = new Scale(1.0, 1.0, 1.0);
    private final Translate translate = new Translate(0, 0, 0);
    private String trackedBody = null; // The body to focus on with the camera.
    private final Translate cameraOffset = new Translate(0, 0, 0); // Set the camera offset.

    @Override
    public void start(Stage primaryStage) {
        // Load ephemeris data
        loadEphemerisData();

        initializeScene();

        VBox controls = createControls();
        VBox zoomMenu = createZoomMenu();

        BorderPane root = new BorderPane();
        root.setCenter(space);
        root.setLeft(zoomMenu);
        root.setBottom(controls);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Solar System Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();


        setupCameraControls();

        startAnimation();
    }

    private void loadEphemerisData() {
        System.out.println("Loading ephemeris data...");
        
        // Initialize ephemeris loader with 1-minute steps
        EphemerisLoader eph = new EphemerisLoader(1, 1);
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

    private VBox createZoomMenu() {
        Button zoomEarth = new Button("Zoom Earth");
        zoomEarth.setOnAction(e -> zoomOnBody("earth"));

        Button zoomSaturn = new Button("Zoom Saturn");
        zoomSaturn.setOnAction(e -> zoomOnBody("saturn"));
        Button zoomTitan = new Button("Zoom Titan");
        zoomTitan.setOnAction(e -> zoomOnBody("titan"));

        Button zoomProbe = new Button("Zoom Probe");
        zoomProbe.setOnAction(e -> zoomOnBody("probe"));

        Button resetButton = new Button("Reset View");
        resetButton.setOnAction(e -> resetCameraView());

        VBox menu = new VBox(10, zoomEarth, zoomSaturn, zoomTitan, zoomProbe, resetButton);
        menu.setPadding(new Insets(15));
        menu.setAlignment(Pos.TOP_LEFT);
        return menu;
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
        worldGroup.getTransforms().addAll(translate, rotateX, rotateY, scale, cameraOffset);
    }

    private void resetCameraView() {
        trackedBody = null;
        rotateX.setAngle(20);
        rotateY.setAngle(0);
        scale.setX(1.0);
        scale.setY(1.0);
        scale.setZ(1.0);
        translate.setX(600);
        translate.setY(250);
        translate.setZ(-200);
        cameraOffset.setX(0);  // Reset camera offset
        cameraOffset.setY(0);
        cameraOffset.setZ(0);

        System.out.println("Camera reset to default view.");
    }

    private void zoomOnBody(String name) {
        Sphere target = planetSpheres.get(name.toLowerCase());
        if (target == null) {
            System.out.println("No body named " + name + " found.");
            return;
        }
        
        trackedBody = name.toLowerCase();
        
        // Set scale for zoom
        scale.setX(3.5);
        scale.setY(3.5); 
        scale.setZ(3.5);
        
        // Initial camera positioning will be handled in updateVisualization
        System.out.println("Zoomed on " + name);
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
            
            // Determine radius for this specific body.
            double radius = body.getSize();
            
            // Create a sphere that represents the body.
            Sphere sphere = new Sphere(radius);


//            if (name.equals("probe")) {
//                root.getChildren().remove(sphere);
//
//                Group rocketModel = loadRocketModel();
//                if (rocketModel != null) {
//                    rocketModel.setTranslateX(sphere.getTranslateX());
//                    rocketModel.setTranslateY(sphere.getTranslateY());
//                    rocketModel.setTranslateZ(sphere.getTranslateZ());
//
//
//                    root.getChildren().add(rocketModel);
//                } else {
//                    System.out.println("Failed to load rocket model, keeping probe sphere");
//                    root.getChildren().add(sphere); // fallback
//                }
//            } else {
            PhongMaterial material = getTexturedMaterial(name);
            sphere.setMaterial(material);
            updateCelestialBodyPosition(sphere, body);
            
            // Add to the scene
            celestialGroup.getChildren().add(sphere);
            planetSpheres.put(name, sphere);
            
            // Initialize path history and visual representation
            pathHistory.put(name, new ArrayList<>());
            Group pathLines = new Group();
            pathGroup.getChildren().add(pathLines);
            planetPaths.put(name, pathLines);
            
            System.out.println("Added: " + name + " at position " + body.getPosition() + "scaling: " + body.getRelativeScalingFactor());
        }
    }

    private PhongMaterial getTexturedMaterial(String name) {
        try {
            String texturePath = "textures/" + name.toLowerCase() + ".jpeg";
            URL url = getClass().getClassLoader().getResource(texturePath);
            if (url == null) {
                System.out.println("Resource not found: " + texturePath);
                throw new RuntimeException("Resource missing: " + texturePath);
            }

            else {
                System.out.println("Resource found: " + url);
            }
            Image texture = new Image(url.toExternalForm());
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseMap(texture);
            return material;
        } catch (Exception e) {
            System.out.println("Texture not found for " + name + ", using default color.");
            PhongMaterial fallback = new PhongMaterial(getPlanetColor(name));
            return fallback;
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
            case "probe": return Color.BROWN;
            default: return Color.WHITE;
        }
    }

    private Group loadRocketModel() {
        try {
            String rocketModelPath = "model/rocket.obj";
            URL rocketUrl = getClass().getClassLoader().getResource(rocketModelPath);
            if (rocketUrl == null) {
                System.out.println("Rocket model not found: " + rocketModelPath);
                return null;
            }
            ObjModelImporter importer = new ObjModelImporter();
            importer.read(rocketUrl);
            MeshView[] meshViews = importer.getImport();
            Group rocketGroup = new Group(meshViews);
            importer.close();
            return rocketGroup;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void updateCelestialBodyPosition(Sphere sphere, CelestialBodies body) {
        Vector position = body.getPosition();
        double relativeScalingFactor = body.getRelativeScalingFactor();
        sphere.setTranslateX(position.getX() * relativeScalingFactor * SCALE_FACTOR);
        sphere.setTranslateY(position.getY() * relativeScalingFactor * SCALE_FACTOR);
        sphere.setTranslateZ(position.getZ() * relativeScalingFactor * SCALE_FACTOR);
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
        Slider speedSlider = new Slider(500, 1500, 1000);
        speedSlider.setPrefWidth(200);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            simulationSpeed = newVal.doubleValue());
        
        // Create time label
        timeLabel = new Label("Simulation Time: " + formatDateTime(currentTime));
        
        // Create restart button
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> restartSimulation());

        // Create layout for controls
        HBox sliderBox = new HBox(10, new Label("Time:"), timeSlider, playPauseButton, restartButton);
        sliderBox.setPadding(new Insets(10));
        
        HBox speedBox = new HBox(10, new Label("Speed:"), speedSlider);
        speedBox.setPadding(new Insets(10));
        
        VBox controls = new VBox(10, sliderBox, speedBox, timeLabel);
        controls.setPadding(new Insets(10));
        
        return controls;
    }

    private void restartSimulation() {
        currentTimeIndex = 0;
        currentTime = startTime;
        isPlaying = true;
        playPauseButton.setText("Pause");
        timeSlider.setValue(0);
        updateVisualization();
        
        // Clear all path histories for a fresh start
        for (String bodyName : pathHistory.keySet()) {
            pathHistory.get(bodyName).clear();
            planetPaths.get(bodyName).getChildren().clear();
        }
        
        System.out.println("Simulation restarted from beginning");
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
                        
                        // Check if we've reached the end
                        if (currentTimeIndex >= timeKeys.size()) {
                            currentTimeIndex = timeKeys.size() - 1; // Stay at the last frame
                            isPlaying = false; // Pause the simulation
                            playPauseButton.setText("Play");
                            System.out.println("Simulation reached end time - paused");
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

        // Handle camera tracking (add this after the position update loop)
        if (trackedBody != null) {
            Sphere trackedSphere = planetSpheres.get(trackedBody);
            if (trackedSphere != null) {
                double x = trackedSphere.getTranslateX();
                double y = trackedSphere.getTranslateY();
                double z = trackedSphere.getTranslateZ();
                
                // Update camera offset to center the tracked body
                cameraOffset.setX(-x);
                cameraOffset.setY(-y);
                cameraOffset.setZ(-z);
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