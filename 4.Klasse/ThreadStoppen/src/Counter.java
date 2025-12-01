import javax.swing.*;

public class Counter extends Thread {

    JTextField tf;
    int z;
    public Counter(JTextField tf) {
        this.tf = tf;
    }

    @Override
    public void run(){
        while(!isInterrupted()){
            tf.setText(""+z);
            z++;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }
}
