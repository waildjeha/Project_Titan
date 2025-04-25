//package com.ken10;
//
//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;
//import java.util.List;
//
///**
// * Evaluates mathematical expressions for ODEs using exp4j.
// */
//public class ODEFunctionEvaluator {
//    /**
//     * Evaluates the given mathematical expression dynamically.
//     *
//     * @param equation      The right-hand side of the ODE.
//     * @param variables     The current values of state variables.
//     * @param variableNames The names of the state variables.
//     * @return The computed derivative value.
//     */
//    public double evaluate(String equation, double[] variables, List<String> variableNames) {
//        try {
//            // Build the expression
//            ExpressionBuilder builder = new ExpressionBuilder(equation);
//            for (String varName : variableNames) {
//                builder.variable(varName);
//            }
//            Expression expression = builder.build();
//
//            // Set variable values
//            for (int i = 0; i < variableNames.size(); i++) {
//                expression.setVariable(variableNames.get(i), variables[i]);
//            }
//
//            // Evaluate and return result
//            return expression.evaluate();
//        } catch (Exception e) {
//            throw new RuntimeException("Error evaluating equation: " + equation + " - " + e.getMessage());
//        }
//    }
//}
