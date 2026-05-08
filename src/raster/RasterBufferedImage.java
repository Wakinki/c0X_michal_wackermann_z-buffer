package raster;

import transforms.Col;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class RasterBufferedImage implements Raster<Col> {

    private BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void resize(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setElement(int x, int y, Col color) {
        if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
            image.setRGB(x, y, color.getRGB());
        }
    }

    @Override
    public Optional<Col> getElement(int x, int y) {
        if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
            return Optional.of(new Col(image.getRGB(x, y)));
        }
        return Optional.empty();
    }

    @Override
    public int getWidth() {return image.getWidth();}

    @Override
    public int getHeight() {return image.getHeight();}

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
    }

    public BufferedImage getImage() {return image;}
}