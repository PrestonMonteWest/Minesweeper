package minesweeper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author Preston West
 */
public final class Clock extends JLabel implements ActionListener {
    private Timer timer;
    private long startTime;
    boolean running;

    public static Clock Factory(int interval) {
        Clock clockInstance = new Clock();
        clockInstance.timer = new Timer(interval, clockInstance);
        clockInstance.timer.setInitialDelay(interval);

        return clockInstance;
    }

    private Clock() {
        startTime = 0;
        setText("0");
        running = false;
    }

    public void start() {
        timer.start();
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        timer.stop();
        startTime = 0;
        setText("0");
        running = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        long diffInSecs = (currentTime - startTime) / 1000;
        setText(String.valueOf(diffInSecs));
    }
}
