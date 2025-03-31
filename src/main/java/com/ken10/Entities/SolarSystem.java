package com.ken10.Entities;
import com.ken10.Other.Vector;
import java.util.ArrayList;
import java.util.List;

public class SolarSystem {
    public static List<Planets> CreatePlanets() {
        ArrayList<Planets> planets = new ArrayList<>();

        planets.add(new Planets("Sun",new Vector(0,0,0),new Vector(0,0,0),1.99e30));
        planets.add(new Planets("Mercury",new Vector(-1.47e8,-2.97e7,2.75e4),new Vector(5.31e00,-2.93e1,6.69e-4),3.30e23 ));
        planets.add(new Planets("Venus",new Vector(0,0,0),new Vector(), 0));
        planets.add(new Planets("Earth",new Vector(0,0,0),new Vector(), 0));
        planets.add(new Planets("Moon",new Vector(0,0,0),new Vector(),0));
        planets.add(new Planets("Mars",new Vector(0,0,0),new Vector(), 0));
        planets.add(new Planets("Jupiter",new Vector(0,0,0),new Vector(), 0));
        planets.add(new Planets("Saturn",new Vector(0,0,0),new Vector(), 0));
        planets.add(new Planets("Titan",new Vector(0,0,0),new Vector(),0));
        planets.add(new Planets("Uranus",new Vector(0,0,0),new Vector(), 0));
        planets.add(new Planets("Neptune",new Vector(0,0,0),new Vector(), 0));

        return planets;
    }
}
