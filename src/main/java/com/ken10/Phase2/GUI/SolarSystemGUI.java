package com.ken10.Phase2.GUI;

// =====================================
// PROJECT-SPECIFIC IMPORTS
// =====================================
// Solar system model classes containing celestial body data and physics
import com.ken10.Phase2.SolarSystemModel.*;
// State calculation classes for orbital mechanics and ephemeris data
import com.ken10.Phase2.StatesCalculations.*;

// =====================================
// JAVAFX CORE APPLICATION IMPORTS
// =====================================
// Main application framework
import javafx.application.Application;
import javafx.stage.Stage;

// =====================================
// JAVAFX SCENE AND LAYOUT IMPORTS
// =====================================
// Scene management and 3D rendering
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.Group;
import javafx.scene.Node;

// Layout containers and positioning
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;

// =====================================
// JAVAFX UI CONTROL IMPORTS
// =====================================
// Interactive user interface components
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

// =====================================
// JAVAFX 3D GRAPHICS IMPORTS
// =====================================
// 3D geometric shapes and primitives
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Box;

// Material and color system for 3D objects
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.image.Image;

// =====================================
// JAVAFX TRANSFORMATION IMPORTS
// =====================================
// 3D transformations for positioning and animation
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.transform.Transform;

// =====================================
// JAVAFX ANIMATION IMPORTS
// =====================================
// Animation framework for real-time updates
import javafx.animation.AnimationTimer;

// =====================================
// JAVA STANDARD LIBRARY IMPORTS
// =====================================
// Network and resource handling
import java.net.URL;

// Date and time manipulation for simulation timeline
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

// Collection framework for data management
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Main GUI application for visualizing a 3D solar system simulation with celestial bodies and space missions.
 * This JavaFX application provides an interactive 3D visualization of planetary motion, spacecraft trajectories,
 * and allows users to control time, zoom into specific bodies, and switch between different mission scenarios.
 *
 * Features include:
 * - 3D visualization of planets, moons, and spacecraft
 * - Time-based animation with play/pause controls
 * - Camera controls for rotation, zoom, and tracking
 * - Mission switching between probe and engine missions
 * - Orbital path visualization
 */
public class SolarSystemGUI extends Application {

    // =====================================
    // CONSTANTS
    // =====================================
    private static final double SCALE_FACTOR = 10e-7; // Scale down astronomical distances (adjusted)
    private static final int PATH_LENGTH = 1000; // Number of points to keep in orbit path

    // =====================================
    // DATA FIELDS
    // =====================================
    // Pre-loaded ephemeris data
    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> timeStates;
    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> probeTimeStates;
    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> engineMissionTimeStates;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime currentTime;
    private List<LocalDateTime> timeKeys;
    private int currentTimeIndex = 0;

    // =====================================
    // UI COMPONENTS
    // =====================================
    private BorderPane root;
    private SubScene space;
    private Group celestialGroup;
    private Group pathGroup;
    private Label timeLabel;
    private Button playPauseButton;
    private Slider timeSlider;
    private boolean isPlaying = false; // Only starts playing when the user clicks a button for that.

    // =====================================
    // VISUALIZATION COMPONENTS
    // =====================================
    private ArrayList<CelestialBodies> celestialBodies;
    private Map<String, Sphere> planetSpheres = new HashMap<>();
    private Map<String, Group> planetPaths = new HashMap<>();
    private Map<String, Node> celestialNodes = new HashMap<>(); // Store both spheres and rockets
    private Map<String, List<Vector>> pathHistory = new HashMap<>();
    private double simulationSpeed = 200;

    // =====================================
    // CAMERA CONTROL FIELDS
    // =====================================
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS); // Initial tilt
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Scale scale = new Scale(1.0, 1.0, 1.0);
    private final Translate translate = new Translate(0, 0, 0);
    private String trackedBody = null; // The body to focus on with the camera.
    private final Translate cameraOffset = new Translate(0, 0, 0); // Set the camera offset.

    // =====================================
    // ZOOM CONTROL FIELDS
    // =====================================
    private String currentZoomContext = null; // Track what we're zoomed on
    private Map<String, Double> originalSizes = new HashMap<>();
    private Map<String, ZoomConfig> zoomConfigurations = new HashMap<>();

// =====================================
// MAIN APPLICATION ENTRY POINT
// =====================================
    /**
     * Main entry point for the JavaFX application. Initializes the GUI, loads ephemeris data,
     * sets up the 3D scene, creates control panels, and starts the animation.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set
     * @throws Exception if there are any issues during application startup
     */
    @Override
    public void start(Stage primaryStage) {
        // Load data from the ephemerisloader.
        // All the data is loaded before the animation starts, to make the animation itself faster.
        loadEphemerisData();

        // Initialize zoom configurations for zooming for the bodies on which you can zoom in.
        initializeZoomConfigurations();

        // Initialize the scene.
        initializeScene();

        // Create the control panels for the simulation.
        VBox controls = createControls();
        HBox missionChoice = createMissionButtons();
        VBox zoomMenu = createZoomMenu();

        // Create the root borderpane and add the different panels on top of it.
        root = new BorderPane();
        root.setCenter(space);
        root.setTop(missionChoice);
        root.setLeft(zoomMenu);
        root.setBottom(controls);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setTitle("Solar System Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize and set up the camera controls.
        setupCameraControls();

        // Start the animation.
        startAnimation();
    }

    /**
     * Application main method that launches the JavaFX application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

// =====================================
// DATA INITIALIZATION METHODS
// =====================================
    /**
     * Loads data for both probe and engine missions from the EphemerisLoader.
     * Initializes probe object and solves differential equations for both mission types.
     * Sets up time-related data structures and extracts start/end times.
     * This method is called during application initialization to pre-load all simulation data.
     */
    private void loadEphemerisData() {
        System.out.println("Loading ephemeris data...");

        // Initialize the Probe.
        Probe probe = new Probe("probe", new Vector(-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497),
                new Vector(62.37766685559131, -32.92234163864975, -15.852587264581345),
                1.0, 11.0);


        // Initialize ephemeris loader with 2-minute steps for the probe mission
        EphemerisLoader ephProbe = new EphemerisLoader(2, probe, 1);
        ephProbe.solve();
        probeTimeStates = ephProbe.history;

        // Initialize ephemeris loader with 2-minute steps for the engine mission
        EphemerisLoader ephEngine = new EphemerisLoader(2, probe, 2);
        ephEngine.solve();
        engineMissionTimeStates = ephEngine.history;

        // Assume that the simulation by default starts with the Engine Mission.
        timeStates = engineMissionTimeStates;
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

    /**
     * Initializes zoom configurations for different celestial bodies.
     * Sets up scaling factors and relative sizes for Earth-Moon system, probe tracking,
     * Saturn-Titan system, and individual body zoom modes.
     * Each configuration defines how bodies should appear when zoomed into specific contexts.
     */
    private void initializeZoomConfigurations() {
        // Earth-Moon system configuration
        Map<String, Double> earthMoonSizes = new HashMap<>();
        earthMoonSizes.put("earth", 0.02);
        earthMoonSizes.put("moon", 0.005);
        earthMoonSizes.put("probe", 0.002);
        earthMoonSizes.put("sun", 1.0);

        zoomConfigurations.put("earth", new ZoomConfig(600.0, earthMoonSizes));

        // Probe zoom configuration
        Map<String, Double> probeSizes = new HashMap<>();
        probeSizes.put("probe", 0.02); // Make probe more visible
        probeSizes.put("earth", 0.2);
        probeSizes.put("moon", 0.05);
        probeSizes.put("saturn", 0.7);
        probeSizes.put("titan", 0.2);

        zoomConfigurations.put("probe", new ZoomConfig(100.0, probeSizes));

        // Saturn-Titan system zoom configuration
        Map<String, Double> saturnTitanSizes = new HashMap<>();
        saturnTitanSizes.put("saturn", 0.7);
        saturnTitanSizes.put("titan", 0.08);
        saturnTitanSizes.put("probe", 0.02);

        zoomConfigurations.put("saturn", new ZoomConfig(170.0, saturnTitanSizes));

        // Titan system zoom configuration
        Map<String, Double> titanSizes = new HashMap<>();
        titanSizes.put("saturn", 0.2);
        titanSizes.put("titan", 0.5);
        titanSizes.put("probe", 0.05);

        zoomConfigurations.put("titan", new ZoomConfig(300.0, titanSizes));
    }

// =====================================
// SCENE AND UI SETUP METHODS
// =====================================
    /**
     * Initializes the 3D scene including celestial bodies, coordinate axes, and camera setup.
     * Creates the main groups for celestial bodies and orbital paths, sets up the SubScene,
     * and configures initial camera transformations.
     */
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
        translate.setX(450);  // Half of width
        translate.setY(250);  // Half of height
        translate.setZ(-200); // Initial Z offset for better view

        // Add transformations to the world group for camera control
        worldGroup.getTransforms().addAll(translate, rotateX, rotateY, scale, cameraOffset);
    }

    /**
     * Creates the vertical zoom menu with buttons for focusing on different celestial bodies.
     * Includes buttons for Earth, Saturn, Titan, probe, and a reset view option.
     *
     * @return VBox containing styled zoom control buttons
     */
    private VBox createZoomMenu() {
        // Button to zoom on the Earth
        Button zoomEarth = new Button("Zoom Earth");
        styleButton(zoomEarth);
        zoomEarth.setOnAction(e -> zoomOnBody("earth"));

        // Button to zoom on Saturn
        Button zoomSaturn = new Button("Zoom Saturn");
        styleButton(zoomSaturn);
        zoomSaturn.setOnAction(e -> zoomOnBody("saturn"));

        // Button to zoom on Titan
        Button zoomTitan = new Button("Zoom Titan");
        styleButton(zoomTitan);
        zoomTitan.setOnAction(e -> zoomOnBody("titan"));

        // Button to zoom on the probe
        Button zoomProbe = new Button("Zoom Probe");
        styleButton(zoomProbe);
        zoomProbe.setOnAction(e -> zoomOnBody("probe"));

        // Button to reset the view on the Solar System
        Button resetButton = new Button("Reset View");
        styleButton(resetButton);
        resetButton.setOnAction(e -> resetCameraView());

        // Add the buttons to a menu
        VBox menu = new VBox(10, zoomEarth, zoomSaturn, zoomTitan, zoomProbe, resetButton);
        menu.setPadding(new Insets(15));
        menu.setAlignment(Pos.TOP_LEFT);
        return menu;
    }

    /**
     * Creates the horizontal mission selection menu at the top of the interface.
     * Allows users to switch between probe mission and engine mission scenarios.
     *
     * @return HBox containing mission selection buttons
     */
    private HBox createMissionButtons() {
        // Button for the probe mission
        Button probeMission = new Button("Probe Mission");
        styleButton(probeMission);
        probeMission.setOnAction(e -> startSpecifiedMission("probe"));

        // Button for the engine mission
        Button engineMission = new Button("Engine Mission");
        styleButton(engineMission);
        engineMission.setOnAction(e -> startSpecifiedMission("engine"));

        // Add the buttons to a menu
        HBox menu = new HBox(10, probeMission, engineMission); // Horizontal layout
        menu.setPadding(new Insets(15));
        menu.setAlignment(Pos.CENTER); // Center at the top

        return menu;
    }

    /**
     * Creates the bottom control panel with time controls, speed adjustment, and simulation information.
     * Includes play/pause functionality, time slider, speed control, restart button, and time display.
     *
     * @return VBox containing all simulation control elements
     */
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
        playPauseButton = new Button("Play");  // Start in paused state
        playPauseButton.setOnAction(e -> {
            isPlaying = !isPlaying;
            playPauseButton.setText(isPlaying ? "Pause" : "Play");
        });

        // Create speed slider
        Slider speedSlider = new Slider(10, 1000, 200);
        speedSlider.setPrefWidth(200);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                simulationSpeed = newVal.doubleValue());

        timeLabel = new Label("Simulation Time: " + formatDateTime(currentTime));

        // Create button to restart the simulation
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> restartSimulation());

        // Create a label to display the simulation time
        timeLabel = new Label("Simulation Time: " + formatDateTime(currentTime));
        timeLabel.setStyle("-fx-text-fill: white;");

        Label timeText = new Label("Time:");
        timeText.setStyle("-fx-text-fill: white;");

        Label speedText = new Label("Speed:");
        speedText.setStyle("-fx-text-fill: white;");

        // Create a box for the time slider, playbutton, pausebutton and the time text
        HBox sliderBox = new HBox(10, timeText, timeSlider, playPauseButton, restartButton);
        sliderBox.setPadding(new Insets(10));

        // Create a box for the speedslider and speedtext
        HBox speedBox = new HBox(10, speedText, speedSlider);
        speedBox.setPadding(new Insets(10));

        // Create a box for the sliderbox, speedbox and for the timelabel
        VBox controls = new VBox(10, sliderBox, speedBox, timeLabel);
        controls.setPadding(new Insets(10));

        return controls;
    }

    /**
     * Applies consistent styling to buttons throughout the application.
     * Sets transparent background with white borders and hover effects.
     *
     * @param button The button to apply styling to
     */
    private void styleButton(Button button) {
        String white = "#DCDCDC";

        button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-background-radius: 10; " +
                        "-fx-text-fill: " + white + "; " +
                        "-fx-pref-width: 100px; " +
                        "-fx-pref-height: 30px; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-color: " + white + ";"
        );

        button.setOnMousePressed(event -> button.setStyle(
                "-fx-background-color: " + white + "; " +
                        "-fx-background-radius: 10; " +
                        "-fx-text-fill: white; " +
                        "-fx-pref-width: 100px; " +
                        "-fx-pref-height: 30px; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-color: " + white + ";"
        ));

        button.setOnMouseReleased(event -> button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-background-radius: 10; " +
                        "-fx-text-fill: " + white + "; " +
                        "-fx-pref-width: 100px; " +
                        "-fx-pref-height: 30px; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-color: " + white + ";"
        ));
    }

// =====================================
// COORDINATE AXES METHODS
// =====================================
    /**
     * Adds 3D coordinate axes (X, Y, Z) to the scene for spatial reference.
     * Creates colored cylinders for positive and negative directions of each axis,
     * adds text labels, and places an origin marker at (0,0,0).
     */
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

    /**
     * Creates a cylindrical axis with specified length and color.
     * Helper method for coordinate axes creation.
     *
     * @param length The length of the axis cylinder
     * @param color The color of the axis
     * @return Cylinder object representing the axis
     */
    private Cylinder createAxis(double length, Color color) {
        Cylinder axis = new Cylinder(0.5, length);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        axis.setMaterial(material);
        return axis;
    }

    /**
     * Creates a text label for coordinate axes.
     *
     * @param text The text to display (X, Y or Z)
     * @param color The color of the text
     * @return Label object for the axis
     */
    private Label createAxisLabel(String text, Color color) {
        Label label = new Label(text);
        label.setTextFill(color);
        return label;
    }

// =====================================
// CELESTIAL BODY MANAGEMENT METHODS
// =====================================
    /**
     * Initializes 3D representations of all celestial bodies from the loaded ephemeris data.
     * Creates spheres for planets/moons and rocket models for probes.
     * Sets up initial positions, materials, textures, and orbital path tracking.
     */
    private void initializeCelestialBodies() {
        // Process each celestial body
        for (CelestialBodies body : celestialBodies) {
            String name = body.getName().toLowerCase();

            Node celestialNode; // Can be either Sphere or Group (rocket)

            if (name.equals("probe")) {
                // Create rocket for probe
                celestialNode = createRocket(body.getSize());

                // Store the original size for the probe
                // Use a consistent base size that represents the "scale factor 1.0"
                originalSizes.put(name, 1.0); // This represents the base scale

                // Position the rocket
                Vector position = body.getPosition();
                double relativeScalingFactor = body.getRelativeScalingFactor();
                celestialNode.setTranslateX(position.getX() * relativeScalingFactor * SCALE_FACTOR);
                celestialNode.setTranslateY(position.getY() * relativeScalingFactor * SCALE_FACTOR);
                celestialNode.setTranslateZ(position.getZ() * relativeScalingFactor * SCALE_FACTOR);

            } else {
                // Create sphere for other bodies
                double radius = body.getSize();
                Sphere sphere = new Sphere(radius);

                // Store original size for planets
                originalSizes.put(name, radius);

                // Set the material and appearance of the sphere
                PhongMaterial material = getTexturedMaterial(name);
                sphere.setMaterial(material);

                // Position the sphere
                updateCelestialBodyPosition(sphere, body);

                celestialNode = sphere;
                planetSpheres.put(name, sphere); // Keep spheres in the old map for compatibility
            }

            // Add to the scene
            celestialGroup.getChildren().add(celestialNode);
            celestialNodes.put(name, celestialNode); // Store in the new map

            // Initialize path history and visual representation (existing code)
            pathHistory.put(name, new ArrayList<>());
            Group pathLines = new Group();
            pathGroup.getChildren().add(pathLines);
            planetPaths.put(name, pathLines);

            System.out.println("Added: " + name + " at position " + body.getPosition());
        }
    }

    /**
     * Updates the 3D position of a celestial body sphere based on its current state.
     * Applies scaling factors and coordinate transformations.
     *
     * @param sphere The sphere object to update
     * @param body The celestial body data containing position information
     */
    private void updateCelestialBodyPosition(Sphere sphere, CelestialBodies body) {
        Vector position = body.getPosition();
        double relativeScalingFactor = body.getRelativeScalingFactor();
        sphere.setTranslateX(position.getX() * relativeScalingFactor * SCALE_FACTOR);
        sphere.setTranslateY(position.getY() * relativeScalingFactor * SCALE_FACTOR);
        sphere.setTranslateZ(position.getZ() * relativeScalingFactor * SCALE_FACTOR);
    }

    /**
     * Creates a textured material for celestial bodies using image files.
     * Attempts to load texture from resources, falls back to solid colors if texture not found.
     *
     * @param name The name of the celestial body (used to find texture file)
     * @return PhongMaterial with texture or fallback color
     */
    private PhongMaterial getTexturedMaterial(String name) {
        try {
            if(name.equals("probe")){
                PhongMaterial material = new PhongMaterial(Color.WHITE);
                return material;
            }
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

    /**
     * Provides fallback colors for planets when textures are not available.
     *
     * @param name The name of the celestial body
     * @return Color object representing the default color for the body
     */
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

// =====================================
// ROCKET METHODS
// =====================================
    /**
     * Creates a detailed 3D rocket model for representing space probes.
     * Constructs a rocket with main body, nose cone, engine section, fins, and engine flame.
     * All components scale proportionally with the size parameter.
     *
     * @param size The base size for scaling all rocket components
     * @return Group containing the complete rocket model
     */
    private Group createRocket(double size) {
        Group rocket = new Group();

        // All dimensions should scale with the size parameter
        double bodyRadius = size / 8;
        double bodyHeight = size;

        // Main rocket body
        Cylinder mainBody = new Cylinder(bodyRadius, bodyHeight);
        mainBody.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
        mainBody.setTranslateY(0); // center at origin

        // Nose cone - scales with body radius and height
        Sphere noseCone = new Sphere(bodyRadius);
        noseCone.setScaleY(2.5);
        noseCone.setScaleX(1.0);
        noseCone.setScaleZ(1.0);
        noseCone.setMaterial(new PhongMaterial(Color.DARKRED));
        noseCone.setTranslateY(-bodyHeight / 2); // placed above main body

        // Engine section - scales with body dimensions
        double engineHeight = bodyHeight / 4; // Scale engine height with body
        Cylinder engineSection = new Cylinder(bodyRadius * 1.2, engineHeight);
        engineSection.setMaterial(new PhongMaterial(Color.DARKGRAY));
        engineSection.setTranslateY(bodyHeight / 2 + engineHeight / 2); // below main body

        // Engine nozzle - scales with body radius
        Sphere engineNozzle = new Sphere(bodyRadius * 1.2);
        engineNozzle.setScaleY(0.8);
        engineNozzle.setScaleX(0.6);
        engineNozzle.setScaleZ(0.6);
        engineNozzle.setMaterial(new PhongMaterial(Color.BLACK));
        engineNozzle.setTranslateY(bodyHeight / 2 + engineHeight + bodyRadius * 0.8); // below engine section

        // Racing stripes - scale with body dimensions
        double stripeHeight = bodyHeight / 6; // Scale stripe height
        Cylinder stripe1 = new Cylinder(bodyRadius * 1.05, stripeHeight);
        stripe1.setMaterial(new PhongMaterial(Color.DARKBLUE));
        stripe1.setTranslateY(-bodyHeight / 4); // Position relative to body height

        Cylinder stripe2 = new Cylinder(bodyRadius * 1.05, stripeHeight);
        stripe2.setMaterial(new PhongMaterial(Color.DARKBLUE));
        stripe2.setTranslateY(bodyHeight / 4); // Position relative to body height

        // Add fins - pass scaled dimensions
        for (int i = 0; i < 3; i++) {
            Group fin = createStreamlinedFin(bodyHeight, bodyRadius);
            fin.getTransforms().add(new Rotate(i * 120, Rotate.Y_AXIS));
            rocket.getChildren().add(fin);
        }

        // Engine flame - scale with body dimensions
        Group flame = createEngineFlame(bodyHeight, bodyRadius);
        flame.setTranslateY(bodyHeight / 2 + engineHeight + bodyRadius * 1.6); // below nozzle

        // Assemble everything
        rocket.getChildren().addAll(
                mainBody,
                noseCone,
                engineSection,
                engineNozzle,
                stripe1,
                stripe2,
                flame
        );

        return rocket;
    }

    /**
     * Creates streamlined fins for the rocket model.
     * Generates proportionally scaled fins that attach to the rocket body.
     *
     * @param bodyHeight The height of the rocket body for proportional scaling
     * @param bodyRadius The radius of the rocket body for proportional scaling
     * @return Group containing the fin geometry
     */
    private Group createStreamlinedFin(double bodyHeight, double bodyRadius) {
        Group fin = new Group();

        // All fin dimensions scale with body dimensions
        double finHeight = bodyHeight * 0.4; // Scale fin height with body height
        double finWidth = bodyRadius / 5;     // Scale fin width with body radius
        double finDepth = bodyHeight / 4;     // Scale fin depth with body height
        double finOffset = bodyRadius * 1.2;  // Scale fin offset with body radius

        Box finBody = new Box(finWidth, finHeight, finDepth);
        PhongMaterial material = new PhongMaterial(Color.DARKRED);
        finBody.setMaterial(material);
        finBody.setTranslateX(0);
        finBody.setTranslateY(bodyHeight/1.7); // Position relative to body height
        finBody.setTranslateZ(finOffset); // Scale offset outward

        fin.getChildren().add(finBody);
        return fin;
    }

    /**
     * Creates an animated engine flame effect for the rocket.
     * Generates a multi-layered flame with different colors and sizes.
     *
     * @param bodyHeight The height of the rocket body for proportional scaling
     * @param bodyRadius The radius of the rocket body for proportional scaling
     * @return Group containing the flame effect geometry
     */
    private Group createEngineFlame(double bodyHeight, double bodyRadius) {
        Group flame = new Group();

        // All flame dimensions scale with body dimensions
        double flameRadius = bodyRadius * 0.8;      // Scale flame radius
        double flameHeight = bodyHeight * 0.3;      // Scale flame height with body

        // Outer flame core
        Sphere flameCore = new Sphere(flameRadius);
        flameCore.setScaleY(flameHeight / flameRadius); // Scale to desired height
        flameCore.setMaterial(new PhongMaterial(Color.ORANGE));
        flameCore.setTranslateY(0);

        // Inner flame - smaller and shorter
        double innerFlameRadius = flameRadius * 0.67;
        double innerFlameHeight = flameHeight * 0.67;
        Sphere innerFlame = new Sphere(innerFlameRadius);
        innerFlame.setScaleY(innerFlameHeight / innerFlameRadius);
        innerFlame.setMaterial(new PhongMaterial(Color.YELLOW));
        innerFlame.setTranslateY(-flameHeight * 0.17); // Offset slightly

        // Hot core - smallest and shortest
        double hotCoreRadius = flameRadius * 0.33;
        double hotCoreHeight = flameHeight * 0.44;
        Sphere hotCore = new Sphere(hotCoreRadius);
        hotCore.setScaleY(hotCoreHeight / hotCoreRadius);
        hotCore.setMaterial(new PhongMaterial(Color.WHITE));
        hotCore.setTranslateY(-flameHeight * 0.33); // Offset more

        flame.getChildren().addAll(flameCore, innerFlame, hotCore);
        return flame;
    }

    /**
     * Updates the rocket's orientation to point in the direction of its velocity vector.
     * Preserves existing scale transformations while applying rotation to align with motion.
     *
     * @param rocket The rocket Group to orient
     * @param velocity The velocity vector determining the rocket's direction
     */
    private void updateRocketOrientation(Group rocket, Vector velocity) {
        if (velocity.magnitude() > 1e-10) { // Check for non-zero velocity
            // Preserve existing scale transforms while clearing only rotate transforms
            List<Scale> existingScales = new ArrayList<>();
            for (Transform transform : rocket.getTransforms()) {
                if (transform instanceof Scale) {
                    existingScales.add((Scale) transform);
                }
            }

            // Clear all transforms
            rocket.getTransforms().clear();

            // Re-add the preserved Scale transforms first
            rocket.getTransforms().addAll(existingScales);

            // Normalize velocity vector to get direction
            Vector direction = velocity.normalize();

            // The rocket's default orientation is along the Y-axis (pointing up)
            // We need to rotate it to point in the direction of velocity

            // Calculate rotation to align Y-axis with velocity direction (negated for correct orientation)
            Point3D yAxis = new Point3D(0, 1, 0); // Default rocket orientation
            Point3D velocityDirection = new Point3D(-direction.getX(), -direction.getY(), -direction.getZ());

            // Calculate the axis of rotation (cross product)
            Point3D rotationAxis = yAxis.crossProduct(velocityDirection);

            // Calculate the angle of rotation (dot product)
            double dotProduct = yAxis.dotProduct(velocityDirection);
            double angle = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dotProduct))));

            // Only apply rotation if we have a valid rotation axis
            if (rotationAxis.magnitude() > 1e-10) {
                Rotate rotation = new Rotate(angle, rotationAxis);
                rocket.getTransforms().add(rotation); // Add after the scale transforms
            } else if (dotProduct < 0) {
                // Special case: velocity is opposite to Y-axis, rotate 180 degrees around X-axis
                Rotate rotation = new Rotate(180, Rotate.X_AXIS);
                rocket.getTransforms().add(rotation); // Add after the scale transforms
            }
            // If dotProduct > 0.99, velocity is already aligned with Y-axis, no rotation needed

            System.out.println("Rocket orientation updated. Total transforms: " + rocket.getTransforms().size());
        }
    }

// =====================================
// ZOOM AND CAMERA CONTROL METHODS
// =====================================
    /**
     * Focuses the camera on a specific celestial body with appropriate zoom and scaling.
     * Applies predefined zoom configurations and updates camera tracking.
     *
     * @param name The name of the celestial body to focus on
     */
    private void zoomOnBody(String name) {
        String bodyName = name.toLowerCase();
        Node target = celestialNodes.get(bodyName);
        if (target == null) {
            System.out.println("No body named " + name + " found.");
            return;
        }

        // Reset camera transformations first
        rotateX.setAngle(20);
        rotateY.setAngle(0);
        translate.setX(450);
        translate.setY(250);
        translate.setZ(-200);
        cameraOffset.setX(0);
        cameraOffset.setY(0);
        cameraOffset.setZ(0);

        // Set trackedbody to be the specified body
        trackedBody = bodyName;
        currentZoomContext = bodyName;

        // Store original sizes if not already stored
        if (originalSizes.isEmpty()) {
            for (CelestialBodies body : celestialBodies) {
                originalSizes.put(body.getName().toLowerCase(), body.getSize());
            }
        }

        // Apply zoom configuration if it exists
        ZoomConfig config = zoomConfigurations.get(bodyName);
        if (config != null) {
            applyZoomConfiguration(config);

            // Set scale
            scale.setX(config.scaleX);
            scale.setY(config.scaleY);
            scale.setZ(config.scaleZ);
        } else {
            // Default zoom behavior for bodies without specific configuration
            resetToOriginalSizes();
            scale.setX(3.5);
            scale.setY(3.5);
            scale.setZ(3.5);
        }

        // Immediately update camera tracking to position on the selected body
        updateCameraTracking();

        for (Group pathGroup : planetPaths.values()) {
            pathGroup.setVisible(false);
        }

        System.out.println("Zoomed on " + name + " with context: " + currentZoomContext);
    }

    /**
     * Applies a specific zoom configuration to adjust body sizes and camera settings.
     * Modifies sphere radii and probe scaling according to the zoom context.
     *
     * @param config The ZoomConfig object containing scaling and size information
     */
    private void applyZoomConfiguration(ZoomConfig config) {
        for (CelestialBodies body : celestialBodies) {
            String bodyName = body.getName().toLowerCase();

            // Handle spheres (planets, moons, etc.)
            Sphere sphere = planetSpheres.get(bodyName);
            if (sphere != null) {
                // Set size
                Double configSize = config.bodySizes.get(bodyName);
                if (configSize != null) {
                    sphere.setRadius(configSize);
                } else {
                    // Use a small default size for unconfigured bodies
                    sphere.setRadius(0.5);
                }
            }

            // Handle probe (rocket)
            if (bodyName.equals("probe")) {
                Node probeNode = celestialNodes.get("probe");
                if (probeNode != null && probeNode instanceof Group) {
                    Group probeGroup = (Group) probeNode;

                    // Handle size scaling
                    Double configSize = config.bodySizes.get("probe");
                    if (configSize != null) {
                        // Remove all existing scale transforms first
                        probeGroup.getTransforms().removeIf(t -> t instanceof Scale);

                        // Create a new scale transform and add it FIRST in the transform list
                        // This ensures it's applied before any rotation transforms
                        Scale probeScale = new Scale(configSize, configSize, configSize);
                        probeGroup.getTransforms().add(0, probeScale); // Add at index 0 (first)

                        System.out.println("Applied scale " + configSize + " to probe");
                    }
                }
            }
        }
    }

    /**
     * Resets all celestial bodies to their original sizes.
     * Removes any zoom-specific scaling transformations.
     */
    private void resetToOriginalSizes() {
        for (CelestialBodies body : celestialBodies) {
            String bodyName = body.getName().toLowerCase();

            // Reset sphere sizes
            Sphere sphere = planetSpheres.get(bodyName);
            if (sphere != null && originalSizes.containsKey(bodyName)) {
                sphere.setRadius(originalSizes.get(bodyName));
                sphere.setOpacity(1.0); // Full opacity
            }

            // Reset probe size
            if (bodyName.equals("probe")) {
                Node probeNode = celestialNodes.get("probe");
                if (probeNode != null && probeNode instanceof Group) {
                    Group probeGroup = (Group) probeNode;
                    probeGroup.setOpacity(1.0);

                    // Remove all scale transforms to reset to original size
                    probeGroup.getTransforms().removeIf(t -> t instanceof Scale);

                    System.out.println("Reset probe to original size");
                }
            }
        }
    }

    /**
     * Resets the camera to the default view showing the entire solar system.
     * Clears tracking, resets transformations, and restores original body sizes.
     */
    private void resetCameraView() {
        trackedBody = null;
        currentZoomContext = null;

        // Reset to original sizes and full visibility
        resetToOriginalSizes();

        rotateX.setAngle(20);
        rotateY.setAngle(0);
        scale.setX(1.0);
        scale.setY(1.0);
        scale.setZ(1.0);
        translate.setX(450);
        translate.setY(250);
        translate.setZ(-200);
        cameraOffset.setX(0);
        cameraOffset.setY(0);
        cameraOffset.setZ(0);

        for (Group pathGroup : planetPaths.values()) {
            pathGroup.setVisible(true);
        }

        System.out.println("Camera reset to default view.");
    }

    /**
     * Sets up mouse and scroll event handlers for interactive camera controls.
     * Enables mouse dragging for rotation and scroll wheel for zooming.
     */
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

    /**
     * Updates camera position to track a specific celestial body.
     * Centers the view on the tracked body by adjusting camera offset transforms.
     */
    private void updateCameraTracking() {
        if (trackedBody != null) {
            Node trackedNode = celestialNodes.get(trackedBody);
            if (trackedNode != null) {
                double x = trackedNode.getTranslateX();
                double y = trackedNode.getTranslateY();
                double z = trackedNode.getTranslateZ();

                // Update camera offset to center the tracked body
                cameraOffset.setX(-x);
                cameraOffset.setY(-y);
                cameraOffset.setZ(-z);
            }
        }
    }

// =====================================
// SIMULATION CONTROL METHODS
// =====================================
    /**
     * Starts the main animation timer that drives the simulation.
     * Controls frame rate, time advancement, and visualization updates.
     * Handles play/pause states and end-of-simulation detection.
     */
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
                    } else {
                        // Even when paused, update camera tracking so zoom continues to work
                        updateCameraTracking();
                    }
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    /**
     * Resets the simulation to the beginning and pauses playback.
     * Clears all orbital path histories for a fresh start.
     */
    private void restartSimulation() {
        currentTimeIndex = 0;
        currentTime = startTime;
        isPlaying = false; // Pause the simulation and only restarts when the user presses the play button.
        playPauseButton.setText("Play");
        timeSlider.setValue(0);
        updateVisualization();

        // Clear all path histories for a fresh start
        for (String bodyName : pathHistory.keySet()) {
            pathHistory.get(bodyName).clear();
            planetPaths.get(bodyName).getChildren().clear();
        }

        System.out.println("Simulation restarted from beginning");
    }

    /**
     * Switches between different mission datasets (probe vs engine mission).
     * Loads the appropriate ephemeris data and restarts the simulation.
     *
     * @param mission The mission type to load ("probe" or "engine")
     */
    private void startSpecifiedMission(String mission){
        if(mission.equals("probe")){
            timeStates = probeTimeStates;
        } else{
            timeStates = engineMissionTimeStates;
        }

        restartSimulation();
    }

    /**
     * Updates the 3D visualization based on the current simulation time.
     * Updates positions of all celestial bodies, rocket orientations, orbital paths,
     * camera tracking, and time display information.
     */
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

            // Get the node (sphere or rocket) for this body
            Node celestialNode = celestialNodes.get(bodyName);
            if (celestialNode != null) {
                // Update positions
                Vector position = body.getPosition();
                double relativeScalingFactor = body.getRelativeScalingFactor();
                celestialNode.setTranslateX(position.getX() * relativeScalingFactor * SCALE_FACTOR);
                celestialNode.setTranslateY(position.getY() * relativeScalingFactor * SCALE_FACTOR);
                celestialNode.setTranslateZ(position.getZ() * relativeScalingFactor * SCALE_FACTOR);

                if (bodyName.equals("probe") && celestialNode instanceof Group) {
                    Vector velocity = body.getVelocity();
                    updateRocketOrientation((Group) celestialNode, velocity);
                }

                // Update orbit paths
                updatePathVisualization(bodyName, body.getPosition());
            }
        }

        // Handle camera tracking using the extracted method
        updateCameraTracking();

        // Update the time label
        long daysBetween = ChronoUnit.DAYS.between(startTime, currentTime);
        double yearsFraction = daysBetween / 365.25;
        timeLabel.setText(String.format("Simulation Time: %s (%.2f days, %.2f years)",
                formatDateTime(currentTime), (double)daysBetween, yearsFraction));
    }

    /**
     * Updates the visual representation of orbital paths for a celestial body.
     * Manages path history, creates 3D line segments, and limits path length for performance.
     * Skips trajectory visualization for the sun.
     *
     * @param bodyName The name of the celestial body
     * @param position The current position of the body
     */
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

                    // Use simplified Line3D class
                    Line3D line = new Line3D(startX, startY, startZ, endX, endY, endZ,
                            getPlanetColor(bodyName).deriveColor(0, 1, 1, 0.5));
                    pathLines.getChildren().add(line);
                }
            }
        }
    }

// =====================================
// UTILITY METHODS
// =====================================
    /**
     * Formats a LocalDateTime object into a readable string format.
     *
     * @param time The LocalDateTime to format
     * @return Formatted date-time string in "yyyy-MM-dd HH:mm" format
     */
    private String formatDateTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return time.format(formatter);
    }

}