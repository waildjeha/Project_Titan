/*
package com.ken10.ODEs;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.List;

*/
/**
 * Evaluates mathematical expressions for ODEs using exp4j.
 *//*

public class ODEFunctionEvaluator {
    */
/**
     * Evaluates the given mathematical expression dynamically.
     * 
     * @param equation      The right-hand side of the ODE.
     * @param variables     The current values of state variables.
     * @param variableNames The names of the state variables.
     * @return The computed derivative value.
     *//*

    public double evaluate(String equation, double[] variables, List<String> variableNames) {
        // Check if the variable names and values match.
        if (variableNames.size() != variables.length) {
            throw new IllegalArgumentException("Mismatch between variable names and values.");
        }

        // Check if equation is empty or null
        if (equation == null || equation.trim().isEmpty()) {
            throw new IllegalArgumentException("Equation cannot be null or empty.");
        }
        
        try {
            // Build the expression
            ExpressionBuilder builder = new ExpressionBuilder(equation);
            for (String varName : variableNames) {
                builder.variable(varName);
            }

            // Compile the mathematical expression so that it can be evaluated.
            Expression expression = builder.build();
            
            // Set variable values
            for (int i = 0; i < variableNames.size(); i++) {
                expression.setVariable(variableNames.get(i), variables[i]);
            }
            
            // Evaluate and return result
            return expression.evaluate();
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating equation: " + equation + " - " + e.getMessage());
        }
    }
}
*/
