package state;

import controller.Controller3D;
import controller.TransformController;
import solid.Solid;
import transforms.Vec3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class RotateState extends ControllState {

    private static final float ROTATE_RATE = 0.1f;

    public RotateState(Controller3D ctrl) {
        super(ctrl);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {

        TransformController manipulator = ctrl.getManipulator();

        float delta = e.isShiftDown() ? -ROTATE_RATE : ROTATE_RATE;


            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> manipulator.rotateX(delta);
                case KeyEvent.VK_Y -> manipulator.rotateY(delta);
                case KeyEvent.VK_Z -> manipulator.rotateZ(delta);
            }

        ctrl.drawScene();
    }

}
