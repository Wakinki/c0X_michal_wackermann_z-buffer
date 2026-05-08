package model;

import objectdata.Vertex;

public class Line {

    private final int x1, x2, y1, y2;
    private final double z1, z2;
    private final int color;

    public Line(int x1, int y1, double z1, int x2, int y2, double z2, int color) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.color = color;
    }

    public Line(Vertex p1, Vertex p2, int color) {
        this.x1 = (int)p1.getX();
        this.x2 = (int)p2.getX();
        this.y1 = (int)p1.getY();
        this.y2 = (int)p2.getY();
        this.z1 = p1.getZ();
        this.z2 = p2.getZ();
        this.color = color;
    }

    public Line(Vertex p1, Vertex p2) {
        this.x1 = (int)p1.getX();
        this.x2 = (int)p2.getX();
        this.y1 = (int)p1.getY();
        this.y2 = (int)p2.getY();
        this.z1 = p1.getZ();
        this.z2 = p2.getZ();
        this.color = 0xff0000;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public double getZ1() {return z1;}

    public double getZ2() {return z2;}

    public int getColor() {
        return color;
    }
}
