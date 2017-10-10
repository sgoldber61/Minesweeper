/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author sgoldber
 */
public class JMinesweeperTimer extends JLabel {
    private static final int MAX_SECONDS = 999;
    
    private final Timer timer;
    
    private long startTime;
    
    public JMinesweeperTimer() {
        super("000", JLabel.CENTER);
        
        // Create a timer that fires every 0.1 seconds, and register our
        // own action-event listener for the timer's events.
        timer = new Timer(100, new TimerHandler());
    }
    
    // handle events from the internal timer
    private class TimerHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // do stuff
            long currTime = System.currentTimeMillis();
            
            int elapsedSeconds = ((int) (currTime - startTime)) / 1000;
            
            if (elapsedSeconds < MAX_SECONDS) {
                setText(getTimeString(elapsedSeconds));
            }
            else {
                stopTimer();
            }
        }
    }
    
    private String getTimeString(int seconds) {
        String secondsString = String.valueOf(seconds);
        
        if (seconds < 10) {
            return "00" + secondsString;
        }
        else if (seconds < 100) {
            return "0" + secondsString;
        }
        else {
            return secondsString;
        }
        
    }
    
    public void startTimer() {
        startTime = System.currentTimeMillis();
        timer.start();
    }
    
    public void stopTimer() {
        timer.stop();
    }
    
    public void rezeroTimer() {
        setText("000");
    }
}
