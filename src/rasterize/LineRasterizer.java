package rasterize;

import model.Line;
import model.Point;
import raster.Raster;
import raster.RasterBufferedImage;

import java.awt.*;

public abstract class LineRasterizer {
    protected Raster raster;
    protected Color color;

    public LineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }


    public void rasterize(Line line) {

    }

    public void rasterize(Point point1, Point point2){

    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }
}
