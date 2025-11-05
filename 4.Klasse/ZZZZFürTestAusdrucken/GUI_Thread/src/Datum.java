import javax.swing.*;
import java.util.*;

public class Datum extends Thread{
    private JLabel lb1;

    public Datum(JLabel lb1){
        this.lb1=lb1;
    }

    @Override
    public void run(){
    while(true){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Date d1 = new Date();
        lb1.setText(""+d1);
    }
    }
}

