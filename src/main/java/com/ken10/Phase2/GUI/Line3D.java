package com.ken10.Phase2.GUI;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point3D;

/**
 * A simple 3D line implementation using a thin cylinder
*/
public class Line3D extends Group {
    public Line3D(double startX, double startY, double startZ, 
                double endX, double endY, double endZ, Color color) {
        
        // Calculate the midpoint of the line
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;
        double midZ = (startZ + endZ) / 2;
        
        // Calculate length and direction
        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;
        double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
        
        // Create a thin cylinder to represent the line
        Cylinder line = new Cylinder(0.2, length);
        
        // Set the material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        line.setMaterial(material);
        
        // Position at midpoint
        line.setTranslateX(midX);
        line.setTranslateY(midY);
        line.setTranslateZ(midZ);
        
        // Rotate to align with the direction
        // Default orientation of cylinder is along y-axis
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D direction = new Point3D(dx, dy, dz);
        
        // Skip rotation if direction is too small
        if (length > 0.001) {
            // Normalize the direction vector
            direction = direction.normalize();
            
            // Find the rotation axis (perpendicular to both vectors)
            Point3D rotationAxis = yAxis.crossProduct(direction);
            double rotationAxisLength = rotationAxis.magnitude();
            
            // If rotation axis length is not zero, perform rotation
            if (rotationAxisLength > 0.001) {
                double angle = Math.acos(yAxis.dotProduct(direction));
                Rotate rotate = new Rotate(Math.toDegrees(angle), rotationAxis);
                line.getTransforms().add(rotate);
            } else if (direction.getY() < 0) {
                // Special case when direction is opposite to y-axis
                line.getTransforms().add(new Rotate(180, 1, 0, 0));
            }
        }
        
        getChildren().add(line);
    }
}
