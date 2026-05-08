package controller;


import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import raster.Raster;
import renderer.Renderer;
import solid.*;
import solid.curve.BezierCurve;
import solid.curve.CoonsCurve;
import solid.curve.FergusonCurve;
import solid.curve.ParametricCurve;
import state.*;
import transforms.*;
import view.Panel;

import java.awt.event.*;
import java.util.ArrayList;

public class Controller3D implements Controller {

    private final Panel panel;
    private LineRasterizer lineRasterizer;

    private Renderer renderer;

    private Camera camera;
    private Mat4PerspRH proj;

    // Solids
    private ArrayList<Solid> solids;
    private ArrayList<Solid> axis;
    private Solid selectedSolid;
    private Col selectedSolidColor;

    private Vec3D sceneTranslate = new Vec3D();
    private Vec3D sceneRotate = new Vec3D();
    private Vec3D sceneScale = new Vec3D(1);

    private ControllState currentState;

    private boolean forward, backward, left, right, up, down;

    private final ControllState DEFAULT_STATE = new CameraState(this);
    private static final double MOVE_SPEED = 0.05;

    public Controller3D(Panel panel) {
        this.panel = panel;

        initObjects(panel.getRaster());
        initListeners(panel);
        update();
    }



    public void initObjects(Raster raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        lineRasterizer.setColor(0x00ff00);
        currentState = DEFAULT_STATE;


        solids = new ArrayList<>();
        axis = new ArrayList<>();
        selectedSolid = null;
        selectedSolidColor = null;

        renderer = new Renderer(
                lineRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight()
        );

        camera = new Camera()
                .withPosition(new Vec3D(1, -2, 1.5))
                .withAzimuth(Math.toRadians(110))
                .withZenith(Math.toRadians(-25))
                .withFirstPerson(true);

        proj = new Mat4PerspRH(
                Math.toRadians(70),
                panel.getRaster().getHeight() / (float)panel.getRaster().getWidth(),
                0.01,
                200
        );
        renderer.setProj(proj);

        // Solids
        axis.add(new AxisX());
        axis.add(new AxisY());
        axis.add(new AxisZ());
        solids.add(new Arrow());
        solids.add(new SaddlePlane(0.5, 0.5, 0.5));
        solids.add(new Cylinder(0.2, 1, new Point3D(0, 1,0.3), new Col(0xC77DBB)));
        solids.add(new Cube());
        solids.add( new ParametricCurve(
                t -> new Point3D(Math.cos(t), Math.sin(t), t / 10),
                0, 10 * Math.PI,
                100,
                new Col(0xff0f00)
        ));
        solids.add(new FergusonCurve(

                new Point3D(0, 2, 3),
                new Point3D(0, 2, 0),
                new Point3D(0, 3, 2),
                new Point3D(9, 1, 1)

        ));
        solids.add(new CoonsCurve(
                new Point3D(0, 4, 0),
                new Point3D(3, 5, 7),
                new Point3D(3, 52, 20),
                new Point3D(0, 10, 0),
                4
        ));
        solids.add(new BezierCurve(
                new Point3D(0, 14, 0),
                new Point3D(0, 10, 3),
                new Point3D(0, 18, 2),
                new Point3D(-6, 16, 1),
                new Col(0xff00ff)
        ));
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
                        renderer.setPerspectiveProjection(!renderer.isPerspectiveProjection());
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
                panel.resize();
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

    public void update() {
        panel.clear();

        if (forward)  camera = camera.forward(MOVE_SPEED);
        if (backward) camera = camera.backward(MOVE_SPEED);
        if (left)     camera = camera.left(MOVE_SPEED);
        if (right)    camera = camera.right(MOVE_SPEED);
        if (up)    camera = camera.up(MOVE_SPEED);
        if (down)    camera = camera.down(MOVE_SPEED);

        renderer.setView(camera.getViewMatrix());

        for(Solid axi : axis) {
            renderer.render(axi);
        }

        Mat4 sceneModel =
                new Mat4Transl(sceneTranslate)
                        .mul(new Mat4RotXYZ(
                                Math.toRadians(sceneRotate.getX()),
                                Math.toRadians(sceneRotate.getY()),
                                Math.toRadians(sceneRotate.getZ())
                        ))
                        .mul(new Mat4Scale(sceneScale));

        for (Solid solid : solids) {

            solid.updateModel();
            solid.setModel(sceneModel.mul(solid.getModel()));
            renderer.render(solid);
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

    public ArrayList<Solid> getSolids() {
        return solids;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void setBackward(boolean backward) {
        this.backward = backward;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
