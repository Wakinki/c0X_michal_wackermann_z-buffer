package state;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface DrawingState {
    void onMousePressed(MouseEvent e);
    void onMouseReleased(MouseEvent e);
    void onMouseMoved(MouseEvent e);
    void onMouseDragged(MouseEvent e);
    void onKeyPressed(KeyEvent e);
    void onKeyReleased(KeyEvent e);

    void drawPreview();
    void onEnterState();   // vyčistí preview, resetuje stav
    void onExitState();    // dodělá co je třeba
}