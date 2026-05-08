package rasterize;

import model.Line;
import raster.Raster;
import raster.RasterBufferedImage;

import java.awt.*;

public class LineRasterizerGraphics extends LineRasterizer {

    public LineRasterizerGraphics(Raster raster) {
        super(raster);
    }

    public void rasterize(Line line) {
        this.rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        Graphics g = ((RasterBufferedImage)raster).getImg().getGraphics();
        g.setColor(this.color);
        g.drawLine(x1, y1, x2, y2);
    }
}
