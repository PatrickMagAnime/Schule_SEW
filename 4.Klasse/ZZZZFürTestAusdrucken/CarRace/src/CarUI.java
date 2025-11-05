import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CarUI {
    // .form: Root-Panel Feldname muss genau "CarGUI" sein. Layout im Designer auf null (Absolute) setzen!
    public JPanel CarGUI;

    // .form: Buttons GENAU so benennen: "startButton", "car1Button", "car2Button", "car3Button"
    private JButton startButton;
    private JButton car1Button;
    private JButton car2Button;
    private JButton car3Button;

    private CarRace race;

    public CarUI() {
        // Falls keine .form verwendet wird: Fallback-UI programmgesteuert erstellen
        if (CarGUI == null) {
            CarGUI = new JPanel(null); // Absolute Positionierung für bewegte Buttons
        }
        // Buttons nur erstellen, wenn sie nicht aus der .form kommen
        if (startButton == null) {
            startButton = new JButton("Start");
            startButton.setBounds(20, 250, 120, 30);
            CarGUI.add(startButton);
        }
        if (car1Button == null) {
            car1Button = new JButton("Car 1");
            car1Button.setBounds(20, 20, 80, 30);
            CarGUI.add(car1Button);
        }
        if (car2Button == null) {
            car2Button = new JButton("Car 2");
            car2Button.setBounds(20, 100, 80, 30);
            CarGUI.add(car2Button);
        }
        if (car3Button == null) {
            car3Button = new JButton("Car 3");
            car3Button.setBounds(20, 180, 80, 30);
            CarGUI.add(car3Button);
        }

        // Für Siegertext: logische Namen setzen (optional)
        car1Button.setName("Car 1");
        car2Button.setName("Car 2");
        car3Button.setName("Car 3");

        race = new CarRace(car1Button, car2Button, car3Button);
        race.captureStartPositionsFromCurrentBounds();

        startButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                race.reset();
                race.start();
            }
        });
    }

    // Testbare Main-Methode (optional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JFrame frame = new JFrame("Car Race");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                CarUI ui = new CarUI();
                frame.setContentPane(ui.CarGUI);
                frame.setSize(800, 350);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}