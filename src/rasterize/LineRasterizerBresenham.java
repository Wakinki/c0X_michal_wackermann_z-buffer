package rasterize;

import model.Line;
import model.Point;
import raster.Raster;
import raster.RasterBufferedImage;

/**
 * Implementace rasterizátoru úseček pomocí Bresenhamova algoritmu.
 */
public class LineRasterizerBresenham extends LineRasterizer {
    public LineRasterizerBresenham(Raster raster) {
        super(raster);
    }

    @Override
    public void rasterize(Line line) {
        this.rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    @Override
    public void rasterize(Point point1, Point point2){
        this.rasterize(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;

        while (true) {
            raster.setPixel(x1, y1, 0xff0000);

            if (x1 == x2 && y1 == y2)
                break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
}
