package state;

import controller.Controller3D;
import renderer.Renderer3D;
import solid.Scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class CameraState extends ControllState{
    private int lastMouseX, lastMouseY;
    private boolean mousePressed = false;


    private static final double MOUSE_SENSITIVITY = 0.005;
    private static final double CAM_STEP = 0.15;

    public CameraState(Controller3D ctrl) {
        super(ctrl);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        mousePressed = true;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        super.onMouseMoved(e);
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        if (!mousePressed) return;

        int dx = e.getX() - lastMouseX;
        int dy = e.getY() - lastMouseY;

        lastMouseX = e.getX();
        lastMouseY = e.getY();

        ctrl.setCamera(ctrl.getCamera()
                .addAzimuth(-dx * MOUSE_SENSITIVITY)
                .addZenith(-dy * MOUSE_SENSITIVITY));

        ctrl.update();
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
       switch (e.getKeyCode()){
           case KeyEvent.VK_W -> ctrl.setCamera(ctrl.getCamera().forward(CAM_STEP));
           case KeyEvent.VK_S -> ctrl.setCamera(ctrl.getCamera().backward(CAM_STEP));
           case KeyEvent.VK_A -> ctrl.setCamera(ctrl.getCamera().left(CAM_STEP));
           case KeyEvent.VK_D -> ctrl.setCamera(ctrl.getCamera().right(CAM_STEP));
           case KeyEvent.VK_SHIFT -> ctrl.setCamera(ctrl.getCamera().down(CAM_STEP));
           case KeyEvent.VK_SPACE -> ctrl.setCamera(ctrl.getCamera().up(CAM_STEP));
           case KeyEvent.VK_P -> {
               Scene scene = ctrl.getScene();

               scene.setProjection(!scene.isPerspective());
               ctrl.getRenderer3D().setProjection(scene.getProj());
           }
           case KeyEvent.VK_M -> {
               Renderer3D renderer3D =  ctrl.getRenderer3D();
               renderer3D.toggleRenderMode();
               ctrl.getPanel().setWireframeMode(renderer3D.isWireframeMode());
           }
           case KeyEvent.VK_T -> {
               ctrl.getManipulator().getActive().toggleTexture();
           }
       }
        ctrl.update();
    }

    @Override
    public void onEnterState() {
        super.onEnterState();
    }

    @Override
    public void onExitState() {
        super.onExitState();
    }
}
