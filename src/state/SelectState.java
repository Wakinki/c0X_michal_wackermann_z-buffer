package state;

import controller.Controller3D;
import controller.Mode;
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
    public void onEnterState() {
        resetBuffer();
        System.out.println("Výběr tělesa: zadejte číslo + ENTER, ESC pro vymazání výběru");
    }

    @Override
    public void onKeyPressed(KeyEvent e) {

        if (isEscape(e)) {
            if(buffer.isEmpty()){
                clearSelection();
                returnToCamera();
                return;
            }else {
                onEnterState();
                return;
            }
        }

        if (isEnter(e)) {
            confirmSelection();
            returnToCamera();
            return;
        }

        if (isDigit(e)) {
            appendDigit(e);
        }
    }

    @Override
    public void onExitState() {
        System.out.println("Vybráno těleso: " + (this.ctrl.getSelectedSolid() != null ? this.ctrl.getSelectedSolid().toString() : "null"));
        resetBuffer();
        ctrl.update();
    }


    private boolean isEscape(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ESCAPE;
    }

    private boolean isEnter(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }

    private boolean isDigit(KeyEvent e) {
        return Character.isDigit(e.getKeyChar());
    }

    private void appendDigit(KeyEvent e) {
        buffer.append(e.getKeyChar());
        System.out.println("Vybírám: " + buffer);
    }

    private void confirmSelection() {
        if (buffer.length() == 0) return;

        int index = parseIndex();
        if (index == -1) return;

        if (isValidIndex(index)) {
            selectSolid(index);
        }
    }

    private int parseIndex() {
        try {
            return Integer.parseInt(buffer.toString()) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < ctrl.getSolids().size();
    }

    private void selectSolid(int index) {
        if(ctrl.getSelectedSolid() != null){
            ctrl.getSelectedSolid().setColor(ctrl.getSelectedSolidColor());
        }

        Solid selected = ctrl.getSolids().get(index);
        ctrl.setSelectedSolidColor(selected.getColor());
        selected.setColor(SELECT_COLOR);
        ctrl.setSelectedSolid(selected);
        System.out.println("Vybrýno těleso #" + (index + 1));
    }

    private void clearSelection() {
        Solid s = ctrl.getSelectedSolid();
        if (s != null) {
            s.setColor(ctrl.getSelectedSolidColor());
        }
        ctrl.setSelectedSolid(null);
        System.out.println("Výběr zrušen");
    }

    private void resetBuffer() {
        buffer.setLength(0);
    }

    private void returnToCamera() {
        ctrl.setMode(Mode.CAMERA);
    }
}