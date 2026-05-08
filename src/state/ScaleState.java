package state;

import controller.Controller3D;
import solid.Solid;
import transforms.Vec3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ScaleState extends ControllState {
    private static final float SCALE_RATE = 0.1f;

    public ScaleState(Controller3D ctrl) {
        super(ctrl);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        super.onMousePressed(e);
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        super.onMouseReleased(e);
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        super.onMouseMoved(e);
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        super.onMouseDragged(e);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        Solid s = ctrl.getSelectedSolid();


        float delta = e.isShiftDown() ? -SCALE_RATE : SCALE_RATE;


        if (s != null) {
            Vec3D scale = s.getScale();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> s.setScale(scale.withX(scale.getX() + delta));
                case KeyEvent.VK_Y -> s.setScale(scale.withY(scale.getY() + delta));
                case KeyEvent.VK_Z -> s.setScale(scale.withZ(scale.getZ() + delta));
            }
        } else {
            Vec3D scale = ctrl.getSceneScale();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> ctrl.setSceneScale(scale.withX(scale.getX() + delta));
                case KeyEvent.VK_Y -> ctrl.setSceneScale(scale.withY(scale.getY() + delta));
                case KeyEvent.VK_Z -> ctrl.setSceneScale(scale.withZ(scale.getZ() + delta));
            }
        }
        ctrl.update();
    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        super.onKeyReleased(e);
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
