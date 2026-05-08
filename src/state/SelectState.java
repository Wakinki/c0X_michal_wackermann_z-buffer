package state;

import controller.Controller3D;
import controller.Mode;
import controller.TransformController;
import solid.Solid;
import transforms.Col;

import java.awt.event.KeyEvent;

public class SelectState extends ControllState {

    private static final Col SELECT_COLOR = new Col(0xffffff);

    private final StringBuilder buffer = new StringBuilder();

    public SelectState(Controller3D ctrl) {
        super(ctrl);
    }


    @Override
    public void onKeyPressed(KeyEvent e) {
        TransformController manipulator = ctrl.getManipulator();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_1 -> manipulator.setActiveIndex(0);
            case KeyEvent.VK_2 -> manipulator.setActiveIndex(1);
            case KeyEvent.VK_3 -> manipulator.setActiveIndex(2);
            case KeyEvent.VK_4 -> manipulator.setActiveIndex(3);
        }
        ctrl.update();
        //returnToCamera();
    }


















    private void returnToCamera() {
        ctrl.setMode(Mode.CAMERA);
    }
}