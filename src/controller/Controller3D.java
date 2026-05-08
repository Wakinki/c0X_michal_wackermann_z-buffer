package controller;


import raster.ZBuffer;
import raster.Raster;
import rasterize.LineRasterizerZBuffer;
import rasterize.TriangleRasterizer;
import renderer.Renderer3D;
import solid.*;
import state.*;
import transforms.*;
import view.Panel;
import java.awt.event.*;


public class Controller3D implements Controller {

    private Renderer3D renderer3D;
    private final Panel panel;
    private ZBuffer zBuffer;
    private TriangleRasterizer triangleRasterizer;
    private LineRasterizerZBuffer lineRasterizer;

    private Scene scene;


    private Camera camera;


    // Solids

    private Solid selectedSolid;
    private Col selectedSolidColor;

    private Vec3D sceneTranslate = new Vec3D();
    private Vec3D sceneRotate = new Vec3D();
    private Vec3D sceneScale = new Vec3D(1);

    private ControllState currentState;

    private final ControllState DEFAULT_STATE = new CameraState(this);
    private static final double CAM_STEP = 0.15;
    private static final double MOUSE_SENS = 0.005;
    private static final double MOVE_STEP = 0.1;
    private static final double ROT_STEP = Math.toRadians(5);
    private static final double SCALE_STEP = 1.1;
    private int lastMouseX, lastMouseY;



    public Controller3D(Panel panel) {
        this.panel = panel;


        initObjects(panel.getRaster());
        initListeners(panel);
        update();
    }



    public void initObjects(Raster raster) {

        zBuffer = new ZBuffer(panel.getRaster());

        lineRasterizer = new LineRasterizerZBuffer(zBuffer);
        lineRasterizer.setColor(0x00ff00);
        renderer3D = new Renderer3D(lineRasterizer, triangleRasterizer, panel, camera.getViewMatrix(), scene.getProj());
        scene = new Scene(panel.getRaster().getWidth(), panel.getRaster().getHeight());

        renderer3D = new Renderer3D(lineRasterizer, triangleRasterizer, panel, camera.getViewMatrix(), scene.getProj());

        currentState = DEFAULT_STATE;



        selectedSolid = null;
        selectedSolidColor = null;



        camera = new Camera()
                .withPosition(new Vec3D(1, -2, 1.5))
                .withAzimuth(Math.toRadians(110))
                .withZenith(Math.toRadians(-25))
                .withFirstPerson(true);
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                currentState.onKeyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C -> hardClear();
                    case KeyEvent.VK_R -> setMode(Mode.ROTATE);
                    case KeyEvent.VK_T -> setMode(Mode.TRANSLATE);
                    case KeyEvent.VK_H -> setMode(Mode.ZOOM);
                    case KeyEvent.VK_V -> setMode(Mode.CAMERA);
                    case KeyEvent.VK_K -> setMode(Mode.SELECTION);
                    case KeyEvent.VK_P -> {
                        scene.setProjection(!scene.isPerspective());
                        renderer3D.setProjection(scene.getProj());
                        update();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                currentState.onKeyReleased(e);

            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentState.onMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentState.onMouseReleased(e);
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                currentState.onMouseMoved(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                currentState.onMouseDragged(e);
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                update();
//                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    public void setState(ControllState newState) {
        if (currentState != null) {
            currentState.onExitState();
        }
        currentState = newState;
        currentState.onEnterState();
        update();
    }

    public void setMode(Mode mode) {
        switch (mode) {
            case CAMERA -> setState(DEFAULT_STATE);
            case ROTATE -> setState(new RotateState(this));
            case TRANSLATE -> setState(new TranslateState(this));
            case ZOOM -> setState(new ScaleState(this));
            case CUT -> setState(DEFAULT_STATE);
            case SELECTION -> setState(new SelectState(this));
        }
    }

    private void update() {
        renderer3D.setView(camera.getViewMatrix());
    }

    private void drawAxes() {
        Solid[] axes = scene.getAxes();
        for (Solid axi : axes) {
            renderer3D.setModel(axi.getModel());
            renderer3D.setTexture(null, false);
            renderer3D.setLightingEnabled(false);
            renderer3D.drawAxes(axi.getParts(), axi.getIb(), axi.getVb());
        }
    }

    private void drawSolid(Solid solid) {
        renderer3D.setModel(solid.getModel());
        renderer3D.setTexture(solid.getTexture(), solid.isTextureEnabled());
        renderer3D.setLightingEnabled(solid.isLightingEnabled());
        renderer3D.draw(solid.getParts(), solid.getIb(), solid.getVb());
    }

    private void drawScene() {
        panel.getRaster().clear();
        zBuffer.clear();

        drawAxes();

        for (Solid solid : scene.getManipulableSolids()) {
            drawSolid(solid);
        }

        panel.repaint();
    }

    private void hardClear() {
        panel.clear();
    }



    public Solid getSelectedSolid() {
        return selectedSolid;
    }

    public Vec3D getSceneTranslate() {
        return sceneTranslate;
    }

    public void setSceneTranslate(Vec3D sceneTranslate) {
        this.sceneTranslate = sceneTranslate;
    }

    public Vec3D getSceneRotate() {
        return sceneRotate;
    }

    public void setSceneRotate(Vec3D sceneRotate) {
        this.sceneRotate = sceneRotate;
    }

    public Vec3D getSceneScale() {
        return sceneScale;
    }

    public void setSceneScale(Vec3D sceneScale) {
        this.sceneScale = sceneScale;
    }

    public void setSelectedSolid(Solid selectedSolid) {
        this.selectedSolid = selectedSolid;
    }

    public Col getSelectedSolidColor() {
        return selectedSolidColor;
    }

    public void setSelectedSolidColor(Col selectedSolidColor) {
        this.selectedSolidColor = selectedSolidColor;
    }


    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
