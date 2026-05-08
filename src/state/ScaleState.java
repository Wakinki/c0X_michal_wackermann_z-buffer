package state;

import controller.Controller3D;
import controller.TransformController;
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
    public void onKeyPressed(KeyEvent e) {
        TransformController manipulator = ctrl.getManipulator();

        float delta = e.isShiftDown() ? -SCALE_RATE : SCALE_RATE;

        if(e.isControlDown()){
           switch (e.getKeyCode()) {
               case KeyEvent.VK_X, KeyEvent.VK_Y, KeyEvent.VK_Z -> manipulator.scale(delta);
           }
        }else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_X -> manipulator.scaleX(delta);
                case KeyEvent.VK_Y -> manipulator.scaleY(delta);
                case KeyEvent.VK_Z -> manipulator.scaleZ(delta);
            }
        }

        ctrl.update();
    }

}
