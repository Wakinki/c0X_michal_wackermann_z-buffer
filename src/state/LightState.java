package state;

import controller.Controller3D;
import controller.LightAnimator;
import controller.TransformController;

import java.awt.event.KeyEvent;


public class LightState extends ControllState{


    public LightState(Controller3D ctrl) {
        super(ctrl);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        LightAnimator lightAnimator = ctrl.getLightAnimator();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_B -> lightAnimator.toggle();
            case KeyEvent.VK_C -> lightAnimator.randomizeColor();

        }
        ctrl.update();
    }
}
