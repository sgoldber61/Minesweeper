/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sgoldber
 */
public class MinesweeperBoard {
    public static final int MINE_CONSTANT = -1;
    
    private final int[][] board;
    
    private final int m;
    private final int n;
    private final int nMines;
    
    public MinesweeperBoard(int m, int n, int nMines) {
        // board will initially be all zeroes at the beginning, then later upon first click we will generate it
        board = new int[m][n];
        
        this.m = m;
        this.n = n;
        this.nMines = nMines;
    }
    
    public int getBoardValue(int i, int j) {
        return board[i][j];
    }
    
    public void generateBoard(int I, int J) {
        // zero-out board
        // randomly generate the locations in (i, j) form of where the mines will be
        // the first nMines of perm will be the mine indices
        int[][] perm = new int[m * n][2];
        int permIndex = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = 0;
                
                // if the (i, j) pair is not in the vicinity of where we clicked, then add it to perm
                if (Math.abs(i - I) > 1 || Math.abs(j - J) > 1) {
                    perm[permIndex][0] = i;
                    perm[permIndex][1] = j;
                    permIndex++;
                }
            }
        }
        
        // create random sample in perm[0], perm[1], ..., perm[nMines - 1]
        for (int i = 0; i < nMines; i++) {
            // random integer between i and permIndex - 1
            int r = i + (int) (Math.random() * (permIndex - i));
            // swap elements at indices i and r
            int[] t = perm[r];
            perm[r] = perm[i];
            perm[i] = t;
        }
        
        // initialize mines
        for (int i = 0; i < nMines; i++) {
            board[perm[i][0]][perm[i][1]] = MINE_CONSTANT;
        }
        
        // initialize mine neighbor counts
        for (int i = 0; i < nMines; i++) {
            // the mine's neighbors, horizontal bounds
            for (int i0 = perm[i][0] - 1; i0 <= perm[i][0] + 1; i0++) {
                // the mine's neighbors, vertical bounds
                for (int j0 = perm[i][1] - 1; j0 <= perm[i][1] + 1; j0++) {
                    // make sure we stay within the bounds of the game board
                    if (0 <= i0 && i0 < m && 0 <= j0 && j0 < n) {
                        // if our square is not a mine (this includes the central square), then increment the square
                        if (board[i0][j0] != MINE_CONSTANT) {
                            board[i0][j0]++;
                        }
                    }
                }
            }
        }
        
        /*
        // debugging information for board generation
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == MINE_CONSTANT)
                    System.out.print("X ");
                else
                    System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        */
    }
}
