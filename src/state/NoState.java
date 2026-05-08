package state;

import controller.Controller3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class NoState extends ControllState{

    public NoState(Controller3D ctrl) {
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
