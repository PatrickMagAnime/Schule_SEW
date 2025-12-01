import javax.swing.*;

public class Reaktion extends Thread {
    private final JLabel jl;
    private final long startNs;
    private volatile boolean cancelled = false;

    public Reaktion(JLabel jl) {
        this.jl = jl;
        this.startNs = System.nanoTime();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            if (!cancelled) {
                long elapsedNs = System.nanoTime() - startNs;
                double elapsedMs = elapsedNs / 1000000.0;
                SwingUtilities.invokeLater(() -> {
                    if (jl != null) jl.setText(String.format("Reaktionszeit: %.0f ms", elapsedMs));
                });
            }
        }
    }

    public void reportAndStop() {
        this.interrupt();
    }

    public void cancel() {
        this.cancelled = true;
        this.interrupt();
    }
}