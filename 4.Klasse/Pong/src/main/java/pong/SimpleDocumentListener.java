package pong;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Objects;

public final class SimpleDocumentListener implements DocumentListener {
    private final Runnable runnable;

    private SimpleDocumentListener(Runnable runnable) {
        this.runnable = Objects.requireNonNull(runnable);
    }

    public static SimpleDocumentListener of(Runnable runnable) {
        return new SimpleDocumentListener(runnable);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        runnable.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        runnable.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        runnable.run();
    }
}
