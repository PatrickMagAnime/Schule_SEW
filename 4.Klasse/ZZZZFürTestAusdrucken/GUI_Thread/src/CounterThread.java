import javax.swing.*;

public class CounterThread extends Thread{
    private JLabel lb1;

    public CounterThread(JLabel lb1){
        this.lb1=lb1;
    }

    @Override
    public void run(){
        for (int i = 0; i < 1000; i++) {
                lb1.setText(""+i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
