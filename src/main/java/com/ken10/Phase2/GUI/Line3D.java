package com.ken10.Phase2.GUI;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point3D;
import javafx.scene.transform.Translate;

/**
 * Class for creating 3D line segments using thin cylinders.
 * Used for visualizing orbital paths in 3D space.
 * Calculates proper positioning and rotation to connect two 3D points.
*/
public class Line3D extends Group {
    /**
     * Constructor for Line3D that creates a 3D line between two points.
     * 
     * @param startX X coordinate of the line start point
     * @param startY Y coordinate of the line start point  
     * @param startZ Z coordinate of the line start point
     * @param endX X coordinate of the line end point
     * @param endY Y coordinate of the line end point
     * @param endZ Z coordinate of the line end point
     * @param color Color of the line
    */
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