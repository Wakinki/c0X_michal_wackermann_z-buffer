package rasterize;

import model.Line;
import model.Point;
import raster.RasterBufferedImage;

import java.awt.*;

/**
 * Bresenhamův rasterizátor úseček s plynulým přechodem barev.
 */
public class LineRasterizerColorTransition extends LineRasterizer {

    public LineRasterizerColorTransition(RasterBufferedImage raster) {
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
        // Výchozí barvy (můžeš je změnit dle potřeby)
        Color c1 = Color.RED;
        Color c2 = Color.GREEN;

        // Získání složek (float 0–1)
        float[] col1 = c1.getColorComponents(null);
        float[] col2 = c2.getColorComponents(null);

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int totalSteps = Math.max(dx, dy) + 1;
        int step = 0;

        int err = dx - dy;

        while (true) {
            float t = (totalSteps <= 1) ? 0f : (float) step / (float) (totalSteps - 1);

            // Interpolace složek (R,G,B)
            float[] newCol = new float[3];
            for (int i = 0; i < 3; i++) {
                newCol[i] = (1 - t) * col1[i] + t * col2[i];
            }

            Color interpolated = new Color(newCol[0], newCol[1], newCol[2]);
            raster.setPixel(x1, y1, interpolated.getRGB());

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

            step++;
        }
    }
}
