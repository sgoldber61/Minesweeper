/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.io.File;
import java.awt.*;

/**
 *
 * @author sgoldber
 */
public class JMinesweeperButton extends JButton implements Comparable<JMinesweeperButton> {
    private State state;
    private final MinesweeperBoard board;
    
    private final int i;
    private final int j;
    
    public static final int BUTTON_SIZE = 20;
    public static final int FONT_SIZE = 12;
    public static final int RED_CROSS_INSET = 2;
    
    private final int BUTTON_X;
    private final int BUTTON_Y;
    
    public enum State {
        NORMAL,
        FLAGGED,
        PRESSED,
        MINE,
        LANDED_MINE,
        FALSE_MINE
    }
    
    private static final ImageIcon RED_FLAG_IMAGE = new ImageIcon("images" + File.separator + "redFlagImage.png");
    private static final ImageIcon MINE_IMAGE_TRANSPARENT = new ImageIcon("images" + File.separator + "mineImageTransparent.png");
    
    public JMinesweeperButton(int i, int j, MinesweeperBoard board) {
        super();
        
        BUTTON_X = j * BUTTON_SIZE;
        BUTTON_Y = JMinesweeperBoard.TOP_PANEL_HEIGHT + i * BUTTON_SIZE;
        
        setBounds(BUTTON_X, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE);
        setMargin(new Insets(0, 0, 0, 0));
        setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        
        this.i = i;
        this.j = j;
        
        this.board = board;
        
        setState(State.NORMAL);
        
    }
    
    public int getBoardI() {
        return i;
    }
    
    public int getBoardJ() {
        return j;
    }
    
    public final void setState(State state) {
        this.state = state;
        
        switch (state) {
            case NORMAL: {
                setIcon(null);
                setText(null);
                setBackground(null);
                setEnabled(true);
                break;
            }
            case FLAGGED: {
                setIcon(RED_FLAG_IMAGE);
                break;
            }
            case PRESSED: {
                int value = board.getBoardValue(i, j);
                if (value > 0) {
                    setText(String.valueOf(value));
                }
                setDisabledIcon(null);
                setEnabled(false);
                break;
            }
            case MINE: {
                setIcon(MINE_IMAGE_TRANSPARENT);
                setDisabledIcon(MINE_IMAGE_TRANSPARENT);
                setEnabled(false);
                break;
            }
            case LANDED_MINE: {
                setIcon(MINE_IMAGE_TRANSPARENT);
                setDisabledIcon(MINE_IMAGE_TRANSPARENT);
                setEnabled(false);
                setBackground(Color.RED);
                break;
            }
            case FALSE_MINE: {
                setDisabledIcon(MINE_IMAGE_TRANSPARENT);
                setEnabled(false);
                break;
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (state == State.FALSE_MINE) {
            g.setColor(Color.RED);
            g.drawLine(RED_CROSS_INSET, RED_CROSS_INSET, 
                    BUTTON_SIZE - RED_CROSS_INSET, BUTTON_SIZE - RED_CROSS_INSET);
            g.drawLine(BUTTON_SIZE - RED_CROSS_INSET, RED_CROSS_INSET,
                    RED_CROSS_INSET, BUTTON_SIZE - RED_CROSS_INSET);
        }
    }
    
    public State getState() {
        return state;
    }
    
    @Override
    public int compareTo(JMinesweeperButton button) {
        int compareI = i - button.i;
        
        if (compareI == 0)
            return j - button.j;
        else
            return compareI;
    }
}
