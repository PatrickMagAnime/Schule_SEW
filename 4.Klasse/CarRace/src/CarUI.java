import javax.swing.*;
import java.awt.*;

public class CarUI {
    public JPanel CarGUI;

    private JButton startButton;
    private JButton car1Button;
    private JButton car2Button;
    private JButton car3Button;

    private CarRace race;

    public CarUI() {
        CarGUI = new JPanel(null);

        startButton = new JButton("Start");
        car1Button = new JButton("Car 1");
        car2Button = new JButton("Car 2");
        car3Button = new JButton("Car 3");

        // Icon laden aus dem Arbeitsverzeichnis
        ImageIcon icon = loadIcon("nuke.png");
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaled);

            car1Button.setText("");
            car2Button.setText("");
            car3Button.setText("");

            car1Button.setIcon(scaledIcon);
            car2Button.setIcon(scaledIcon);
            car3Button.setIcon(scaledIcon);
        }

        // Größen und Positionen
        Dimension carSize = new Dimension(64, 64);
        car1Button.setSize(carSize);
        car2Button.setSize(carSize);
        car3Button.setSize(carSize);

        int startX = 20;
        car1Button.setLocation(startX, 20);
        car2Button.setLocation(startX, 100);
        car3Button.setLocation(startX, 180);

        startButton.setBounds(20, 250, 120, 30);

        // Namen für Anzeige
        car1Button.setName("Car 1");
        car2Button.setName("Car 2");
        car3Button.setName("Car 3");

        // Hinzufügen
        CarGUI.add(startButton);
        CarGUI.add(car1Button);
        CarGUI.add(car2Button);
        CarGUI.add(car3Button);

        // Rennen
        race = new CarRace(car1Button, car2Button, car3Button);
        race.captureStartPositionsFromCurrentBounds();

        startButton.addActionListener(e -> {
            race.reset();
            race.start();
        });
    }

    private static ImageIcon loadIcon(String path) {
        java.io.File file = new java.io.File(path);
        return file.exists() ? new ImageIcon(file.getAbsolutePath()) : null;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Run! Run! Run!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CarUI ui = new CarUI();
        frame.setContentPane(ui.CarGUI);

        frame.setSize(1800, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}