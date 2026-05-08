package state;

import controller.Controller3D;
import solid.Solid;
import transforms.Vec3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TranslateState extends ControllState {

    private static final float TRANSLATE_RATE = 0.1f;

    public TranslateState(Controller3D ctrl) {
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

        float delta = e.isShiftDown() ? -TRANSLATE_RATE : TRANSLATE_RATE;


        if (s != null) {
            Vec3D position = s.getPosition();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> s.setPosition(position.withX(position.getX() + delta));
                case KeyEvent.VK_Y -> s.setPosition(position.withY(position.getY() + delta));
                case KeyEvent.VK_Z -> s.setPosition(position.withZ(position.getZ() + delta));
            }
        } else {
            Vec3D position = ctrl.getSceneTranslate();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> ctrl.setSceneTranslate(position.withX(position.getX() + delta));
                case KeyEvent.VK_Y -> ctrl.setSceneTranslate(position.withY(position.getY() + delta));
                case KeyEvent.VK_Z -> ctrl.setSceneTranslate(position.withZ(position.getZ() + delta));
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
