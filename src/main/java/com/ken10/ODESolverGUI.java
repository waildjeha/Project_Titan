package com.ken10;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


public class ODESolverGUI extends Application{
    private TextArea equationInput; // Text box for the user to input equations.
    private Button parseButton, runButton;  // Buttons for user actions.
    private GridPane dynamicInputsGrid;     // Flexible layout to dynamically add input fields.
    private ComboBox<String> solverSelection; // Combobox for selecting the appropriate solver.
    private Map<String, TextField> initialConditionMap; // Map to store the initial condition for every variable.
    private List<String[]> variableEquations; // Stores equations as variable, equation pairs.
    private TextField stepSizeField, durationField; // Fields for the simulation settings.
    private VBox root;  // Vertical layout that arranges all elements in the window.
    private LineChart<Number, Number> resultChart; // Line chart for displaying results
    private NumberAxis xAxis, yAxis; // Axes for the chart

    /**
     * The main entry point for the JavaFX application.
     * This method is automatically called when the application is launched.
     * 
     * @param primaryStage The primary stage (window) for this application, provided by JavaFX.
     *                     It serves as the main container for the application's Scene.
    */
    @Override
    public void start(Stage primaryStage){
        // Set window title
        primaryStage.setTitle("ODE Solver");

        // Create textarea for equations
        equationInput = new TextArea();
        equationInput.setPromptText("Enter ODEs (one per line)");
        equationInput.setPrefRowCount(4);

        // Create buttons.
        parseButton = new Button("Generate input fields for initial conditions");
        runButton = new Button("Run Simulation");

        // Create Combobox for selecting the appropriate solver.
        solverSelection = new ComboBox<>();
        solverSelection.getItems().addAll("Euler Method", "Runge-Kutta 4 Method");
        solverSelection.setValue("Euler Method");

        // Create the dynamic input grid for the initial conditions.
        dynamicInputsGrid = new GridPane();
        dynamicInputsGrid.setPadding(new Insets(10));
        dynamicInputsGrid.setHgap(10);
        dynamicInputsGrid.setVgap(5);

        // Create step size and duration fields with default values.
        stepSizeField = new TextField("0.01");
        durationField = new TextField("100");

        // Initialize the initial conditions map and the variables and equations list.
        initialConditionMap = new LinkedHashMap<>();
        variableEquations = new ArrayList<>();

        // Set up X and Y Axes for the chart
        xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        yAxis = new NumberAxis();
        yAxis.setLabel("State Variable Value");

        // Create the LineChart with the above axes
        resultChart = new LineChart<>(xAxis, yAxis);
        resultChart.setTitle("ODE Simulation Results");

        // Set chart size and appearance
        resultChart.setMinHeight(400);

        // Set up the button actions
        parseButton.setOnAction(e -> generateInputFields()); // Method generateInputFields is called when the button is clicked.
        runButton.setOnAction(e -> runSimulation());    // Method runSimulation is called when the button is clicked.
        
        // Create a VBox for all the content together.
        root = new VBox(10, equationInput, parseButton, solverSelection, dynamicInputsGrid, runButton, resultChart);
        root.setPadding(new Insets(15));
        
        // Create a scrollPane to scroll over the content.
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);

        // Create the scene and show the window.
        Scene scene = new Scene(scrollPane, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void generateInputFields(){
        // Clear previous input fields before generating new input fields.
        dynamicInputsGrid.getChildren().clear();
        initialConditionMap.clear();
        variableEquations.clear();

        // Split the user-entered text into separate equations.
        String[] equations = equationInput.getText().split("\n");

        // Parse variable names and the right hand sides of equations.
        variableEquations = extractVariablesAndEquations(equations);

        // Dynamically generate labels and textfields based on the equations that the user gave as input.
        // then add labels and text fields to the GridPane.
        for (int i = 0; i < variableEquations.size(); i++) {
            String varName = variableEquations.get(i)[0];
            Label label = new Label("Initial Condition " + varName + ":");
            TextField field = new TextField("0.0");
            initialConditionMap.put(varName, field);
            dynamicInputsGrid.add(label, 0, i);
            dynamicInputsGrid.add(field, 1, i);
        }
    
        // Add step size and duration fields
        dynamicInputsGrid.add(new Label("Step Size:"), 0, variableEquations.size());
        dynamicInputsGrid.add(stepSizeField, 1, variableEquations.size());
        dynamicInputsGrid.add(new Label("Simulation Duration:"), 0, variableEquations.size() + 1);
        dynamicInputsGrid.add(durationField, 1, variableEquations.size() + 1);
    }

    private List<String[]> extractVariablesAndEquations(String[] equations){
        // Create a list to store arrays with for every array a variable name and a right hand side of an equation.
        List<String[]> varEquations = new ArrayList<>();

        // Loop through the equations entered by the user.
        for(String eq : equations){
            eq = eq.trim(); // Trim whitespace
            if(eq.startsWith("d") && eq.contains("/dt") && eq.contains("=")){   // Check if the equation is correctly formatted.
                // If the equation is correctly formatted, extract the variable names.
                int start = 1;
                int end = eq.indexOf("/dt");
                int equalSign = eq.indexOf("=");
                if (end > start && equalSign > end) {
                    String varName = eq.substring(start, end).trim(); // Extract the appropriate variable name.
                    String rhs = eq.substring(equalSign + 1).trim(); // Extract everything after the '=' sign.
                    varEquations.add(new String[]{varName, rhs});   // Store the variable and its corresponding equation.
                }
            }
        }
        return varEquations;
    }

    private void runSimulation(){
        try{
            // Retrieve step size and duration entered by the user.
            double stepSize = Double.parseDouble(stepSizeField.getText());
            double duration = Double.parseDouble(durationField.getText());

            // Create an array to store the system's state variables.
            double[] initialState = new double[variableEquations.size()];

            // Create a list for variable names.
            List<String> variableNames = new ArrayList<>();

            // Loop over all the equations and their variables. Initialize the initialState array with the correct initial States.
            for(int i = 0; i < variableEquations.size(); i++){
                String varName = variableEquations.get(i)[0];
                variableNames.add(varName);
                initialState[i] = Double.parseDouble(initialConditionMap.get(varName).getText());
            }

            System.out.println("Variable Mapping:");
            for (int i = 0; i < variableEquations.size(); i++) {
                System.out.println("Variable: " + variableEquations.get(i)[0] + 
                                " -> Initial State: " + initialState[i]);
            }

            // Create derivatives funtion.
            OdeFunction derivatives = (time, state) -> {
                ODEFunctionEvaluator evaluator = new ODEFunctionEvaluator();
                double[] derivativesArray = new double[state.length];
                System.out.println("Time: " + time);  // Debugging print
                for (int i = 0; i < state.length; i++) {
                    try {
                        String rhs = variableEquations.get(i)[1];
                        derivativesArray[i] = evaluator.evaluate(rhs, state, variableNames);
                        // Debugging print to check if the derivative is computed correctly
                        System.out.println("Variable: " + variableEquations.get(i)[0] + 
                        ", Equation: " + rhs + 
                        ", Evaluated Derivative: " + derivativesArray[i]);
                    } catch (Exception e) {
                        showAlert("Error evaluating equation: " + e.getMessage());
                        return null;
                    }
                }
                return derivativesArray;
            };

            System.out.println("Variable Equations:");
            for (String[] eq : variableEquations) {
                System.out.println("Variable: " + eq[0] + " => Equation: " + eq[1]);
            }
            if (derivatives.equals(null)) {
                System.err.println("ERROR: OdeFunction derivatives is NULL!");
            }

            ODESolver solver;
            if(solverSelection.getValue().equals("Euler Method")){
                solver = new EulerSolver(derivatives, initialState, 0, duration, stepSize);
            } else{
                solver = new RungeKutta4Solver(derivatives, initialState, 0, duration, stepSize);
            }

            // Run simulation
            List<ODESolver.TimeState> results = solver.solve();
            plotResults(results);
        } catch(NumberFormatException e){
            showAlert("Error parsing input: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void plotResults(List<ODESolver.TimeState> results) {
        // Clear previous data
        resultChart.getData().clear();
    
        // Create series for each variable
        for (int i = 0; i < variableEquations.size(); i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(variableEquations.get(i)[0]);  // Set the series name to the variable name
    
            // Add data points to the series
            for (ODESolver.TimeState result : results) {
                double time = result.time;
                double stateValue = result.state[i];  // Get the state value for this variable
                series.getData().add(new XYChart.Data<>(time, stateValue));
            }
    
            // Add the series to the chart
            resultChart.getData().add(series);

            // Remove symbols (dots)
            resultChart.setCreateSymbols(false);

            // Make the line thinner
            series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 1px;");
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
