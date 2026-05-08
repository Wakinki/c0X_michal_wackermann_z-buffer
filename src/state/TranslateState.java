package state;

import controller.Controller3D;
import controller.TransformController;
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
    public void onKeyPressed(KeyEvent e) {

        TransformController manipulator = ctrl.getManipulator();

        float delta = e.isShiftDown() ? -TRANSLATE_RATE : TRANSLATE_RATE;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> manipulator.translateX(delta);
                case KeyEvent.VK_Y -> manipulator.translateY(delta);
                case KeyEvent.VK_Z -> manipulator.translateZ(delta);
            }

        ctrl.drawScene();
    }


}
