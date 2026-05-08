package rasterize;

import model.Line;
import raster.RasterBufferedImage;

public class LineRasterizerTrivial extends LineRasterizer {
    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(Line line) {
        this.rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {

        if (x1 > x2) {
            int w = x1; x1 = x2; x2 = w;
            int h = y1; y1 = y2; y2 = h;
        }

        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        for (int x = x1; x <= x2; x++) {
            int y = Math.round(k * x + q);
            raster.setPixel(x, y, 0xff0000);
        }
    }

}
