package rasterize;

import model.Line;
import model.Point;
import raster.Raster;
import raster.RasterBufferedImage;

import java.awt.*;

public abstract class LineRasterizer {

    protected Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }


    public void rasterize(Line line) {
        setColor(line.getColor());
        drawLine(
                line.getX1(), line.getY1(), line.getZ1(),
                line.getX2(), line.getY2(), line.getZ2()
        );
    }

    public void rasterize(Point point1, Point point2){

    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }

    protected void drawLine(int x1, int y1, double z1, int x2, int y2, double z2) {
    }
}
