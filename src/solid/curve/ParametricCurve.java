package solid.curve;

import solid.Solid;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.function.Function;


public class ParametricCurve extends Solid {

    private static final int DEFAULT_STEPS = 24;
    private Function<Double, Point3D> curveFunction;
    private double tStart, tEnd;
    private int steps;

    public ParametricCurve(Function<Double, Point3D> func, double tStart, double tEnd) {
        this(func, tStart, tEnd,DEFAULT_STEPS, new Col(0xffffff));
    }

    public ParametricCurve(Function<Double, Point3D> func, double tStart, double tEnd,  int steps) {
        this(func, tStart, tEnd,steps, new Col(0xffffff));
    }

    public ParametricCurve(Function<Double, Point3D> curveFunction, double tStart, double tEnd, int steps, Col color) {
        this.curveFunction = curveFunction;
        this.tStart = tStart;
        this.tEnd = tEnd;
        this.steps = steps;
        this.color = color;

        generateCurve();
        updateModel();
    }

    private void generateCurve() {
        vb.clear();
        ib.clear();

        // generování bodů podle parametrické funkce
        for (int i = 0; i <= steps; i++) {
            double t = tStart + i * (tEnd - tStart) / steps;
            vb.add(curveFunction.apply(t));
        }

        // spojení bodů lineárními segmenty
        for (int i = 0; i < vb.size() - 1; i++) {
            ib.add(i);
            ib.add(i + 1);
        }
    }

    // Gettery
    public Vec3D getPosition() { return position; }
    public Vec3D getRotation() { return rotation; }
    public Vec3D getScale() { return scale; }
    public int getSteps() { return steps; }
    public double getTStart() { return tStart; }
    public double getTEnd() { return tEnd; }
    public Function<Double, Point3D> getCurveFunction() { return curveFunction; }

    // Withery (vrátí novou instanci se změněnou hodnotou)
    public ParametricCurve withPosition(Vec3D newPos) {
        this.position = newPos;
        dirty = true;
        return this;
    }

    public ParametricCurve withRotation(Vec3D newRot) {
        this.rotation = newRot;
        dirty = true;
        return this;
    }

    public ParametricCurve withScale(Vec3D newScale) {
        this.scale = newScale;
        dirty = true;
        return this;
    }

    public ParametricCurve withSegments(int segments) {
        this.steps = segments;
        generateCurve();
        dirty = true;
        return this;
    }

    public ParametricCurve withCurveFunction(Function<Double, Point3D> curveFunction) {
        this.curveFunction = curveFunction;
        generateCurve();
        dirty = true;
        return this;
    }

    public ParametricCurve withTStart(double tStart) {
        this.tStart = tStart;
        generateCurve();
        dirty = true;
        return this;
    }

    public ParametricCurve withTEnd(double tEnd) {
        this.tEnd = tEnd;
        generateCurve();
        dirty = true;
        return this;
    }


}
