package com.ken10.Phase1;

import com.ken10.Phase1.LotkaVolterraSimulation.LotkaVolterraDerivatives;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

    // Model parameters with default values
    private double alpha = 0.1; // prey growth rate
    private double beta = 0.02; // predation rate
    private double gamma = 0.3; // predator death rate
    private double delta = 0.01; // predator growth from consuming prey

    // Initial conditions
    private double initialPrey = 40.0;
    private double initialPredator = 9.0;

    // Simulation settings
    private double endTime = 200.0;
    private double stepSize = 0.1;

    // UI components
    private LineChart<Number, Number> chart;
    private TextField alphaField, betaField, gammaField, deltaField;
    private TextField preyField, predatorField;
    private TextField endTimeField, stepSizeField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lotka-Volterra Simulation");

        // Create chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Population");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Predator-Prey Population Dynamics");
        chart.setCreateSymbols(false); // Don't show data points
        chart.setAnimated(false); // Better performance

        // Create parameter input fields
        GridPane paramGrid = createParameterInputs();

        // Create buttons
        Button eulerButton = new Button("Solve with Euler");
        Button rk4Button = new Button("Solve with RK4");
        Button clearButton = new Button("Clear Plot");

        eulerButton.setOnAction(e -> runSimulation(true));
        rk4Button.setOnAction(e -> runSimulation(false));
        clearButton.setOnAction(e -> chart.getData().clear());

        HBox buttonBox = new HBox(10, eulerButton, rk4Button, clearButton);
        buttonBox.setPadding(new Insets(10));

        // Layout
        VBox root = new VBox(10, paramGrid, buttonBox, chart);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createParameterInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Model parameters
        Label modelLabel = new Label("Model Parameters:");
        alphaField = new TextField(String.valueOf(alpha));
        betaField = new TextField(String.valueOf(beta));
        gammaField = new TextField(String.valueOf(gamma));
        deltaField = new TextField(String.valueOf(delta));

        grid.add(modelLabel, 0, 0, 2, 1);
        grid.add(new Label("Alpha (prey growth):"), 0, 1);
        grid.add(alphaField, 1, 1);
        grid.add(new Label("Beta (predation rate):"), 0, 2);
        grid.add(betaField, 1, 2);
        grid.add(new Label("Gamma (predator death):"), 2, 1);
        grid.add(gammaField, 3, 1);
        grid.add(new Label("Delta (predator growth):"), 2, 2);
        grid.add(deltaField, 3, 2);

        // Initial conditions
        Label initialLabel = new Label("Initial Conditions:");
        preyField = new TextField(String.valueOf(initialPrey));
        predatorField = new TextField(String.valueOf(initialPredator));

        grid.add(initialLabel, 0, 3, 2, 1);
        grid.add(new Label("Initial Prey:"), 0, 4);
        grid.add(preyField, 1, 4);
        grid.add(new Label("Initial Predator:"), 2, 4);
        grid.add(predatorField, 3, 4);

        // Simulation settings
        Label simLabel = new Label("Simulation Settings:");
        endTimeField = new TextField(String.valueOf(endTime));
        stepSizeField = new TextField(String.valueOf(stepSize));

        grid.add(simLabel, 0, 5, 2, 1);
        grid.add(new Label("End Time:"), 0, 6);
        grid.add(endTimeField, 1, 6);
        grid.add(new Label("Step Size:"), 2, 6);
        grid.add(stepSizeField, 3, 6);

        return grid;
    }

    private void runSimulation(boolean useEuler) {
        try {
            // Parse parameters from UI
            readParametersFromUI();

            // Create derivatives function
            OdeFunction derivatives = new LotkaVolterraDerivatives(alpha, beta, gamma, delta);

            // Setup initial state
            double[] initialState = new double[] { initialPrey, initialPredator };

            // Create appropriate solver
            ODESolver solver;
            if (useEuler) {
                solver = new EulerSolver(derivatives, initialState, 0, endTime, stepSize);
            } else {
                solver = new RungeKutta4Solver(derivatives, initialState, 0, endTime, stepSize);
            }

            // Run simulation
            List<ODESolver.TimeState> results = solver.solve();

            // Plot results
            plotResults(results, useEuler);

        } catch (NumberFormatException e) {
            System.err.println("Error parsing input: " + e.getMessage());
        }
    }

    private void readParametersFromUI() {
        alpha = Double.parseDouble(alphaField.getText());
        beta = Double.parseDouble(betaField.getText());
        gamma = Double.parseDouble(gammaField.getText());
        delta = Double.parseDouble(deltaField.getText());

        initialPrey = Double.parseDouble(preyField.getText());
        initialPredator = Double.parseDouble(predatorField.getText());

        endTime = Double.parseDouble(endTimeField.getText());
        stepSize = Double.parseDouble(stepSizeField.getText());
    }

    private void plotResults(List<ODESolver.TimeState> results, boolean isEuler) {
        // Create data series
        XYChart.Series<Number, Number> preySeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> predatorSeries = new XYChart.Series<>();

        String methodName = isEuler ? "Euler" : "RK4";
        preySeries.setName("Prey (" + methodName + ")");
        predatorSeries.setName("Predator (" + methodName + ")");

        // Populate series with data
        for (ODESolver.TimeState state : results) {
            // Only add every 10th point to improve performance
            if (Math.round(state.time / stepSize) % 10 == 0) {
                preySeries.getData().add(new XYChart.Data<>(state.time, state.state[0]));
                predatorSeries.getData().add(new XYChart.Data<>(state.time, state.state[1]));
            }
        }

        // Add series to chart
        chart.getData().addAll(preySeries, predatorSeries);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
