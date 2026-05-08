package state;

import controller.Controller3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class CameraState extends ControllState{
    private int lastMouseX, lastMouseY;
    private boolean mousePressed = false;


    private static final double MOUSE_SENSITIVITY = 0.005;

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
           case KeyEvent.VK_W -> ctrl.setForward(true);
           case KeyEvent.VK_S -> ctrl.setBackward(true);
           case KeyEvent.VK_A -> ctrl.setLeft(true);
           case KeyEvent.VK_D -> ctrl.setRight(true);
           case KeyEvent.VK_SHIFT -> ctrl.setDown(true);
           case KeyEvent.VK_SPACE -> ctrl.setUp(true);
       }
        ctrl.update();
    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_W -> ctrl.setForward(false);
            case KeyEvent.VK_S -> ctrl.setBackward(false);
            case KeyEvent.VK_A -> ctrl.setLeft(false);
            case KeyEvent.VK_D -> ctrl.setRight(false);
            case KeyEvent.VK_SHIFT -> ctrl.setDown(false);
            case KeyEvent.VK_SPACE -> ctrl.setUp(false);
        }

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
