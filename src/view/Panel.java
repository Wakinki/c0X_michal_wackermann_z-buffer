package view;

import raster.Raster;
import raster.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Panel extends JPanel {

    private RasterBufferedImage raster;
    private boolean wireframeMode = false;


    private static final int FPS = 1000 / 20;
    public static final int WIDTH = 800, HEIGHT = 600;

    public Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        raster = new RasterBufferedImage(WIDTH, HEIGHT);

    }

    public Panel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        raster = new RasterBufferedImage(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(raster.getImage(), 0, 0, null);

        drawStartupHelp((Graphics2D) g);
    }

    public RasterBufferedImage getRaster() {
        return raster;
    }

    public void setWireframeMode(boolean wireframeMode) {
        this.wireframeMode = wireframeMode;
    }

//    public void resize(){
//        if (this.getWidth()<1 || this.getHeight()<1)
//            return;
//        if (this.getWidth()<=raster.getWidth() && this.getHeight()<=raster.getHeight()) //no resize if new is smaller
//            return;
//        RasterBufferedImage newRaster = new RasterBufferedImage(this.getWidth(), this.getHeight());
//
//        newRaster.setElement(raster);
//        raster = newRaster;
//    }

    public void drawStartupHelp(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        int x = 10;
        int y = 15;
        int lineHeight = 12;

        String modeText = "Fill";
        if (wireframeMode) {modeText = "Wireframe";}

        String[] lines = new String[]{
                "Shift + V: Kamera mód ",
                "  WSAD  : pohyb dopředu, dozadu, vlevo, vpravo",
                "  Space, Shift : pohyb nahoru, dolu",
                "  LMB/MMB : rozhlížení myší",
                "  P     : přepínání Ortogonální/Perspektiva",
                "  M     : přepínání Wireframe/Fill (aktuálně: " + modeText + ")",
                "  T     : přepinání textury",
                "",
                "Shift + R: mód rotace",
                "  X/Y/Z : otáčení kolem osy X/Y/Z",
                "  Shift + X/Y/Z : opačná rotace",
                "",
                "Shift + T: mód posunu",
                "  X/Y/Z : posouvání v součadnicích X/Y/Z",
                "  Shift + X/Y/Z : opačný posun",
                "",
                "Shift + H: mód škálování",
                "  X/Y/Z : škálování v součadnicích X/Y/Z",
                "  Shift + X/Y/Z : opačné škálování",
                "  Ctrl + X/Y/Z : škálování celého objektu",
                "",
                "Shift + L: mód světla:",
                "  B : animace pohybu osvětlení",
                "  C : změna barvy osvětlení",
                "",
                "Shift + K: mód výběru",
                "  1/2/3/4 : Krychle / Válec / Koule / Světlo",
                "",
                "Shift + E     : vrátit objekty na počatečnou pozici",
        };

        for (String s : lines) {
            g.drawString(s, x, y);
            y += lineHeight;
        }
    }

    private void setLoop() {
        //TODO časovač, který 30 krát za vteřinu obnoví obsah plátna aktuálním img
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, FPS);
    }

    public void clear() {
        raster.clear();
    }

}
