package state;

import controller.Controller3D;
import solid.Solid;
import transforms.Vec3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class RotateState extends ControllState {

    private static final float ROTATE_RATE = 1f;

    public RotateState(Controller3D ctrl) {
        super(ctrl);
    }
    @Override
    public void onMousePressed(MouseEvent e) {

    }

    @Override
    public void onMouseReleased(MouseEvent e) {

    }

    @Override
    public void onMouseMoved(MouseEvent e) {

    }

    @Override
    public void onMouseDragged(MouseEvent e) {

    }

    @Override
    public void onKeyPressed(KeyEvent e) {

        Solid s = ctrl.getSelectedSolid();

        float delta = e.isShiftDown() ? -ROTATE_RATE : ROTATE_RATE;

        if (s != null) {
            Vec3D r = s.getRotation();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> s.setRotation(r.withX(r.getX() + delta));
                case KeyEvent.VK_Y -> s.setRotation(r.withY(r.getY() + delta));
                case KeyEvent.VK_Z -> s.setRotation(r.withZ(r.getZ() + delta));
            }
        } else {
            Vec3D r = ctrl.getSceneRotate();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> ctrl.setSceneRotate(r.withX(r.getX() + delta));
                case KeyEvent.VK_Y -> ctrl.setSceneRotate(r.withY(r.getY() + delta));
                case KeyEvent.VK_Z -> ctrl.setSceneRotate(r.withZ(r.getZ() + delta));
            }
        }

        ctrl.update();
    }

    @Override
    public void onKeyReleased(KeyEvent e) {

    }

    @Override
    public void onEnterState() {

    }

    @Override
    public void onExitState() {

    }
}
