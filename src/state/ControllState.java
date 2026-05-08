package state;

import controller.Controller3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class ControllState {
    protected final Controller3D ctrl;

    public ControllState(Controller3D ctrl) {
        this.ctrl = ctrl;
    }

    public void onMousePressed(MouseEvent e){};
    public void onMouseReleased(MouseEvent e){};
    public void onMouseMoved(MouseEvent e){};
    public void onMouseDragged(MouseEvent e){};
    public void onKeyPressed(KeyEvent e){};
    public void onKeyReleased(KeyEvent e){};


    public void onEnterState(){};   // resetuje stav
    public void onExitState(){};    // dodělá co je třeba
}
