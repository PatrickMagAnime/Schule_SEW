import javax.swing.*;
import java.util.Date;

public class ProgressBarThread {
        private JLabel lb1;

        public ProgressBarThread(JLabel lb1){
            this.lb1=lb1;
        }

        @Override
        public void run(){
            while(true){
                for (int i = 0; i < 60; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Date d1 = new Date();
                }


            }
        }
    }



