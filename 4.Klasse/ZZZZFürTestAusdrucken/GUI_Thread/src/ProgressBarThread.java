import javax.swing.*;
import java.util.Date;

public class ProgressBarThread extends Thread {
    private final JProgressBar pBsek;
    private final JProgressBar pBmin;
    private final JProgressBar pBhour;

    public ProgressBarThread(JProgressBar pBsek, JProgressBar pBmin, JProgressBar pBhour) {
        this.pBsek = pBsek;
        this.pBmin = pBmin;
        this.pBhour = pBhour;

        this.pBsek.setMinimum(0);
        this.pBsek.setMaximum(60);
        this.pBmin.setMinimum(0);
        this.pBmin.setMaximum(60);
        this.pBhour.setMinimum(0);
        this.pBhour.setMaximum(24);
    }

    @Override
    public void run() {
        while (true) {
            Date now = new Date();
            pBsek.setValue(now.getSeconds());
            pBmin.setValue(now.getMinutes());
            pBhour.setValue(now.getHours());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}