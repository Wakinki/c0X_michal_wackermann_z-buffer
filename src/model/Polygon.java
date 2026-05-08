package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final List<Point> points;

    public Polygon() {
        this.points = new ArrayList<>();
    }

    public Polygon(List<Point> points) {
        this.points = points;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public Point getLastPoint() {return points.getLast();}

    public int getSize() {
        return points.size();
    }

    public List<Point> getPoints() {
        return points;
    }

}
