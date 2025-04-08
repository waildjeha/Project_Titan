package com.ken10.phase2;

public class Body {
    private String name;
    private double x;
    private double y;
    private double z;
    private double Vx;
    private double Vy;
    private double Vz;
    private double mass;
    public Body(String name, double x, double y, double z, double Vx, double Vy, double Vz, double mass) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.Vx = Vx;
        this.Vy = Vy;
        this.Vz = Vz;
        this.mass = mass;
    }
    public Body(String name, Object[][] state){
        this.name = name;
        for (Object[] objects : state) {
            if (objects[0].toString().equals(name)) {
                this.x = Double.parseDouble(objects[1].toString());
                this.y = Double.parseDouble(objects[2].toString());
                this.z = Double.parseDouble(objects[3].toString());
                this.Vx = Double.parseDouble(objects[4].toString());
                this.Vy = Double.parseDouble(objects[5].toString());
                this.Vz = Double.parseDouble(objects[6].toString());
                this.mass = Double.parseDouble(objects[7].toString());
            }
        }
    }
    public Body(int index){
        this.name = InitialDataset.dataset[index][0].toString();
        String mass = InitialDataset.dataset[index][InitialDataset.dataset[index].length-1].toString();
        this.mass = Double.parseDouble(mass);
    }

    public Body(Body body){
        this.name = body.getName();
        this.x = body.getX();
        this.y = body.getY();
        this.z = body.getZ();
        this.Vx = body.getVx();
        this.Vy = body.getVy();
        this.Vz = body.getVz();
        this.mass = body.getMass();
    }
     public Body(){
     }


    // GETTERS
    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getVx() {
        return Vx;
    }

    public double getVy() {
        return Vy;
    }

    public double getVz() {
        return Vz;
    }

    public double getMass() {
        return mass;
    }
    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setVx(double vx) {
        this.Vx = vx;
    }

    public void setVy(double vy) {
        this.Vy = vy;
    }

    public void setVz(double vz) {
        this.Vz = vz;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    @Override
    public String toString() {
        return "Body{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", Vx=" + Vx +
                ", Vy=" + Vy +
                ", Vz=" + Vz +
                ", mass=" + mass +
                '}';
    }
}
