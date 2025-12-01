import javax.swing.*;
import java.awt.*;

public class FractalViewer {

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("OVER-ENGINEERED Infinite Fractal Explorer (BigDouble)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            FractalPanel canvas = new FractalPanel();
            ControlPanel sidebar = new ControlPanel(canvas);

            canvas.setSidebarReference(sidebar);

            frame.add(canvas, BorderLayout.CENTER);
            frame.add(sidebar, BorderLayout.EAST);

            frame.setSize(1400, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            canvas.requestFocusInWindow();
            canvas.requestRender(true);
        });
    }
}