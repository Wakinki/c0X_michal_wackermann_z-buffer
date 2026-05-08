import controller.Controller3D;
import view.Window;

public class Main {
    public static void main(String[] args) {
        Window window = new Window();
        new Controller3D(window.getPanel());
        window.setVisible(true);
    }
}