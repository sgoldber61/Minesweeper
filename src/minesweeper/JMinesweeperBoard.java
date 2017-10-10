/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author sgoldber
 */
public class JMinesweeperBoard extends JFrame {
    // swing fields on top panel
    private final JPanel topPanel;
    private final JLabel flagsLabel;
    private final JButton resetButton;
    private final JMinesweeperTimer gameTimer;

    // swing fields on center panel
    private final JPanel centerPanel;
    private final JMinesweeperButton[][] buttons;

    // numerical board data
    private final MinesweeperBoard board;
    
    // other numerical data
    private final int m;
    private final int n;
    
    private final int nMines;
    private int nFlags;
    private int nRemainingSquares;
    
    private boolean gamePending; // whether numerical board data should be generated or not upon first click
    private boolean gameActive; // whether the game is active i.e. the timer is running
    private boolean gameFinished; // whether the game has been finished i.e. completed
    private boolean gameWon; // whether the game is won, only true when the game is finished and won, otherwise false
    
    // geometric constants
    public static final int TOP_PANEL_HEIGHT = 50;
    
    public JMinesweeperBoard(int m, int n, int nMines) {
        // set layout of entire JMinesweeperBoard
        super();
        setLayout(new BorderLayout());

        // define top and center panels and set their layouts
        topPanel = new JPanel();
        centerPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 3));
        centerPanel.setLayout(null);

        // add flagsLabel, resetButton, and gameTimer to topPanel
        flagsLabel = new JLabel(String.valueOf(nMines), JLabel.CENTER);
        resetButton = new JButton("reset");
        resetButton.setMargin(new Insets(0,0,0,0));
        resetButton.setActionCommand("reset button clicked");
        resetButton.addActionListener(new ActionHandler());
        gameTimer = new JMinesweeperTimer();
        
        topPanel.add(flagsLabel);
        topPanel.add(resetButton);
        topPanel.add(gameTimer);
        
        // create board data and buttons
        // make sure that the board is created before the buttons
        board = new MinesweeperBoard(m, n, nMines);
        buttons = new JMinesweeperButton[m][n];

        // put buttons onto bottom JComponent
        placeButtons(m, n);

        // add top and center panels
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER); // CENTER panel fills out the JMinesweeperBoard

        // set preferred sizes
        int vPixelLength = JMinesweeperButton.BUTTON_SIZE * m;
        int hPixelLength = JMinesweeperButton.BUTTON_SIZE * n;
        Dimension dimensionCenter = new Dimension(hPixelLength, vPixelLength);  // width first, height second
        Dimension dimensionTop = new Dimension(hPixelLength, TOP_PANEL_HEIGHT); // width first, height second

        centerPanel.setPreferredSize(dimensionCenter);
        topPanel.setPreferredSize(dimensionTop);

        // other numerical data
        this.m = m;
        this.n = n;

        this.nMines = nMines;
        nFlags = nMines;
        nRemainingSquares = m * n;

        gamePending = true;
        gameActive = false;
        gameFinished = false;
        gameWon = false;

        // make unresizable, pack, and set to be visible
        setResizable(false);
        pack();
        setVisible(true);

        // program finishes when JFrame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void placeButtons(int m, int n) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                buttons[i][j] = new JMinesweeperButton(i, j, board);
                buttons[i][j].addMouseListener(new MouseHandler());
                add(buttons[i][j]);
            }
        }
    }

    // ActionHandler is used for the reset button
    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("reset button clicked")) {
                // maintain board data, no need to regenerate it just yet

                // set button states back to normal
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        buttons[i][j].setState(JMinesweeperButton.State.NORMAL);
                    }
                }

                // reset the required numerical data
                nFlags = nMines;
                updateFlagsLabel();
                nRemainingSquares = m * n;

                gamePending = true;
                gameActive = false;
                gameFinished = false;
                gameWon = false;

                // stop and rezero the timer
                gameTimer.stopTimer();
                gameTimer.rezeroTimer();
            }
        }
    }

    // MouseHandler is used for Minesweeper buttons
    private class MouseHandler implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent me) {
            if (gameFinished)
                return;

            JMinesweeperButton button = (JMinesweeperButton) me.getSource();
            if (!button.isEnabled())
                return;

            int i = button.getBoardI();
            int j = button.getBoardJ();

            // left click
            if (SwingUtilities.isLeftMouseButton(me)) {
                if (button.getState() == JMinesweeperButton.State.NORMAL) {
                    if (gamePending) {
                        // generate the board at (i, j) (i.e. exclude any mine from appearing adjacent to (i, j))
                        board.generateBoard(i, j);

                        gamePending = false;
                        gameActive = true;

                        // activate the timer
                        gameTimer.startTimer();

                        // "click" the board at (i, j)
                        clearSquares(i, j);
                        if (nFlags == nRemainingSquares) {
                            completeGame();
                        }
                    }
                    else {
                        if (board.getBoardValue(i, j) == MinesweeperBoard.MINE_CONSTANT) {
                            // stop the timer, and activate the mines
                            gameTimer.stopTimer();
                            activateMines(i, j);
                        }
                        else {
                            clearSquares(i, j);
                            if (nFlags == nRemainingSquares) {
                                completeGame();
                            }
                        }
                    }
                }
            }
            // right click
            else if (SwingUtilities.isRightMouseButton(me)) {
                // if the square is normal and we have a flag, then flag it
                if (button.getState() == JMinesweeperButton.State.NORMAL) {
                    if (nFlags > 0) {
                        button.setState(JMinesweeperButton.State.FLAGGED);
                        nFlags--;
                        updateFlagsLabel();
                        nRemainingSquares--;
                    }
                }
                // if the square is flagged, unflag it
                else if (button.getState() == JMinesweeperButton.State.FLAGGED) {
                    button.setState(JMinesweeperButton.State.NORMAL);
                    nFlags++;
                    updateFlagsLabel();
                    nRemainingSquares++;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
            // do nothing
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            // do nothing
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            // do nothing
        }

        @Override
        public void mouseExited(MouseEvent me) {
            // do nothing
        }

    }

    private void activateMines(int I, int J) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // if mine, display mine state if not flagged
                if (board.getBoardValue(i, j) == MinesweeperBoard.MINE_CONSTANT) {
                    if (buttons[i][j].getState() != JMinesweeperButton.State.FLAGGED)
                        buttons[i][j].setState(JMinesweeperButton.State.MINE);
                }
                // if no mine, display false mine if flagged
                else {
                    if (buttons[i][j].getState() == JMinesweeperButton.State.FLAGGED)
                        buttons[i][j].setState(JMinesweeperButton.State.FALSE_MINE);
                }
            }
        }

        // set the button at I, J to be the landed mine image
        buttons[I][J].setState(JMinesweeperButton.State.LANDED_MINE);

        gameActive = false;
        gameFinished = true;
        gameWon = false;
    }

    private void clearSquares(int I, int J) {
        // press our initial button no matter what
        buttons[I][J].setState(JMinesweeperButton.State.PRESSED);

        // if our initial button has adjacent mines, simply decrement remaining squares and exit
        if (board.getBoardValue(I, J) != 0) {
            nRemainingSquares--;
            return;
        }

        // since our initial button has value 0, prepare a buttonSet of buttons that need to be cleared
        // add to the buttonSet our initial button
        TreeSet<JMinesweeperButton> buttonSet = new TreeSet<>();
        buttonSet.add(buttons[I][J]);

        JMinesweeperButton button;
        // take a given button, press it, and add all neighbors to buttonList if the button's value is 0
        while ((button = buttonSet.pollFirst()) != null) {
            button.setState(JMinesweeperButton.State.PRESSED);
            nRemainingSquares--;

            int i = button.getBoardI();
            int j = button.getBoardJ();

            if (board.getBoardValue(i, j) != 0) {
                continue;
            }

            for (int i0 = i - 1; i0 <= i + 1; i0++) {
                for (int j0 = j - 1; j0 <= j + 1; j0++) {
                    if (0 <= i0 && i0 < m && 0 <= j0 && j0 < n) {
                        if (!(i0 == i && j0 == j) && buttons[i0][j0].getState() == JMinesweeperButton.State.NORMAL) {
                            buttonSet.add(buttons[i0][j0]);
                        }
                    }
                }
            }

        }

    }
    
    private void completeGame() {
        // stop the timer
        gameTimer.stopTimer();
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // flag anything that hasn't been flagged
                if (board.getBoardValue(i, j) == MinesweeperBoard.MINE_CONSTANT) {
                    if (buttons[i][j].getState() == JMinesweeperButton.State.NORMAL)
                        buttons[i][j].setState(JMinesweeperButton.State.FLAGGED);
                }
            }
        }

        nFlags = 0;
        updateFlagsLabel();
        nRemainingSquares = 0;

        gameActive = false;
        gameFinished = true;
        gameWon = true;
    }
    
    private void updateFlagsLabel() {
        flagsLabel.setText(String.valueOf(nFlags));
    }
    
    public static void main(String args[]) {
        // test-beginner
        // JMinesweeperBoard boardGame = new JMinesweeperBoard(6, 6, 3);
        
        // beginner
        JMinesweeperBoard boardGame = new JMinesweeperBoard(9, 9, 10);
        
        // intermediate
        // JMinesweeperBoard boardGame = new JMinesweeperBoard(16, 16, 40);
        
        // advanced
        // JMinesweeperBoard boardGame = new JMinesweeperBoard(16, 30, 99);
    }
}
