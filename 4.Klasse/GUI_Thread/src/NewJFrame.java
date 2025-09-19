import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewJFrame {
    private JButton b1;
    private JLabel lb1;
    private JPanel Panel1;
    private JLabel lb2Datum;
    private JProgressBar pBsek;
    private JProgressBar pBmin;
    private JProgressBar pBhour;

    public NewJFrame() {
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                CounterThread c1 = new CounterThread(lb1);
                c1.start();
                Datum d1 = new Datum(lb2Datum);
                d1.start();


            }
        });
    }

        public static void main(String[] args) {
            JFrame frame = new JFrame("Fenster");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new NewJFrame().Panel1);
            frame.pack();
            frame.setSize(800, 300);
            frame.setVisible(true);

        }
    }

