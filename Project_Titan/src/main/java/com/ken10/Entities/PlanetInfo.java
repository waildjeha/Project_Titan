package com.ken10.Entities;
import java.util.List;

public class PlanetInfo {
    public static void main(String[] args) {
        List<Planets> planets = SolarSystem.CreatePlanets();

        for (Planets planet : planets) {
            System.out.println(planet);
        }
    }
}
