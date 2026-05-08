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

    private LightAnimator lightAnimator;

    private Scene scene;
    private TransformController manipulator;

    private Camera camera;

    private ControllState currentState;

    private final ControllState DEFAULT_STATE = new CameraState(this);



    public Controller3D(Panel panel) {
        this.panel = panel;

        initObjects(panel);
        initListeners(panel);
        drawScene();
    }


    public void initObjects(Panel panel) {
        scene = new Scene(panel.getRaster().getWidth(), panel.getRaster().getHeight());

        camera = new Camera()
                .withPosition(new Vec3D(1, -2, 1.5))
                .withAzimuth(Math.toRadians(110))
                .withZenith(Math.toRadians(-25))
                .withFirstPerson(true);

        zBuffer = new ZBuffer(panel.getRaster());

        lineRasterizer = new LineRasterizerZBuffer(zBuffer);
        triangleRasterizer = new TriangleRasterizer(zBuffer);
        renderer3D = new Renderer3D(lineRasterizer, triangleRasterizer, panel, camera.getViewMatrix(), scene.getProj());
        renderer3D.setLight(scene.getLight());

        lightAnimator = new LightAnimator(scene.getLight(), this::drawScene);
        manipulator = new TransformController(scene.getManipulableSolids(), scene.getBaseModels(), 0);

        currentState = DEFAULT_STATE;



        panel.setWireframeMode(renderer3D.isWireframeMode());
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                currentState.onKeyPressed(e);

                if(e.isShiftDown()){
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_C -> lightAnimator.randomizeColor();
                        case KeyEvent.VK_R -> setMode(Mode.ROTATE);
                        case KeyEvent.VK_T -> setMode(Mode.TRANSLATE);
                        case KeyEvent.VK_H -> setMode(Mode.ZOOM);
                        case KeyEvent.VK_V -> setMode(Mode.CAMERA);
                        case KeyEvent.VK_K -> setMode(Mode.SELECTION);
                        case KeyEvent.VK_L -> setMode(Mode.LIGHT);
                        case KeyEvent.VK_E -> manipulator.resetActive();
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

                initObjects(panel);
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
            case LIGHT -> setState(new LightState(this));
            case SELECTION -> setState(new SelectState(this));
        }
    }

    public void update() {
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

    public void drawScene() {
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


    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public TransformController getManipulator() {
        return manipulator;
    }

    public Scene getScene() {
        return scene;
    }

    public LightAnimator getLightAnimator() {
        return lightAnimator;
    }

    public Renderer3D getRenderer3D() {
        return renderer3D;
    }

    public Panel getPanel() {
        return panel;
    }
}
