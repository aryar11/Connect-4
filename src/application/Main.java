package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Connect4.fxml"));
        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class GameLogic {
    enum Player { HUMAN, AI }
    private int[][] board;
    private int[][] validMoves;
    private int rows, cols;

    public GameLogic(int rows, int cols) {
        this.setRows(rows);
        this.setCols(cols);
        this.setBoard(new int[rows][cols]);
        this.setValidMoves (new int[rows][cols]);
        this.updateValidMoves();
        reset();
    }
    
    public void updateValidMoves() {
    	this.setValidMoves (new int[rows][cols]);
        for (int col = 0; col < cols; col++) {
            for (int row = rows - 1; row >= 0; row--) { // Start from the bottom row
                if (this.board[row][col] == 0) { 
                    this.validMoves[row][col] = 1;                    
                    break; 
                }
            }
        }
        // Print validMoves
        /*System.out.println("Valid Moves:");
        for (int[] row : validMoves) {
            for (int move : row) {
                System.out.print(move + " ");
            }
            System.out.println();
        }*/
    }
    
    public int[] makeMove(int row, int col, Player player) {
    	int[] values = new int[2];
    	values[0] = -1;
    	values[1] = col;
        for (int row1 = getRows() - 1; row1 >= 0; row1--) {
            if (getValidMoves()[row1][col] == 1) { // Find the first valid move in the column from the bottom
                getBoard()[row1][col] = player == Player.HUMAN ? 1 : 2; // Update the board with the player's move
                updateValidMoves(); // Don't forget to update the valid moves after making a move
                values[0] = row1;
                break;
            }
        }
        return values; // Return false if no valid move is found
    }


    public boolean checkWin(int player) {
        return checkRowsForWin(player) || checkColumnsForWin(player) || checkDiagonalsForWin(player);
    }

    private boolean checkRowsForWin(int player) {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols() - 3; col++) {
                if (getBoard()[row][col] == player && 
                    getBoard()[row][col + 1] == player && 
                    getBoard()[row][col + 2] == player && 
                    getBoard()[row][col + 3] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkColumnsForWin(int player) {
        for (int col = 0; col < getCols(); col++) {
            for (int row = 0; row < getRows() - 3; row++) {
                if (getBoard()[row][col] == player && 
                    getBoard()[row + 1][col] == player && 
                    getBoard()[row + 2][col] == player && 
                    getBoard()[row + 3][col] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDiagonalsForWin(int player) {
        // Check left-to-right diagonals
        for (int row = 0; row < getRows() - 3; row++) {
            for (int col = 0; col < getCols() - 3; col++) {
                if (getBoard()[row][col] == player && 
                    getBoard()[row + 1][col + 1] == player && 
                    getBoard()[row + 2][col + 2] == player && 
                    getBoard()[row + 3][col + 3] == player) {
                    return true;
                }
            }
        }
        // Check right-to-left diagonals
        for (int row = 0; row < getRows() - 3; row++) {
            for (int col = 3; col < getCols(); col++) {
                if (getBoard()[row][col] == player && 
                    getBoard()[row + 1][col - 1] == player && 
                    getBoard()[row + 2][col - 2] == player && 
                    getBoard()[row + 3][col - 3] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reset() {
        this.setBoard(new int[rows][cols]);
        this.setValidMoves (new int[rows][cols]);
        updateValidMoves();
    }

	public int getRows() {return rows;}

	public void setRows(int rows) {this.rows = rows;}

	public int getCols() {return cols;}

	public void setCols(int cols) {this.cols = cols;}

	public int[][] getBoard() {return board;}

	public void setBoard(int[][] board) {this.board = board;}

	public int[][] getValidMoves() {return validMoves;}

	public void setValidMoves(int[][] validMoves) {this.validMoves = validMoves;}
	
	public void printValidMoves() {
	    System.out.println("Valid Moves:");
	    for (int[] row : validMoves) {
	        for (int move : row) {
	            System.out.print(move + " ");
	        }
	        System.out.println(); // Move to the next line after printing each row
	    }
	}

}

class AIPlayer {
    private GameLogic gameLogic;
    private Boolean smart;
    public AIPlayer(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public int[] makeMove() {
        return findBestMove();
    }

    private int[] findBestMove() {
    	if(getSmart()) {
	        // 1. Check for AI winning move
	        int[] winningMove = findWinningMove(2); // Assuming 2 represents AI
	        if (winningMove != null) {
	        	System.out.println("Found winner");
	            return winningMove;
	        }
	
	        // 2. Block user player's winning move
	        int[] blockingMove = findWinningMove(1); // Assuming 1 represents human player
	        if (blockingMove != null) {
	        	System.out.println("blocking a win");
	            return blockingMove;
	        }
	
	        // 3. Progress AI's 
	        int[] progressMove = findProgressMove();
	        if (progressMove != null) {
	        	System.out.println("progreess");
	            return progressMove;
	        }
    	}
        // 4. Make a random move as a last resort
        System.out.println("random");
        return findRandomMove();
    }

    private int[] findWinningMove(int player) {
        for (int row = 0; row < gameLogic.getRows(); row++) {
            for (int col = 0; col < gameLogic.getCols(); col++) {
                if ((gameLogic.getBoard()[row][col] == 0) && gameLogic.getValidMoves()[row][col] == 1) { 
                	// Temporarily make a move
                	int [][] tmp = gameLogic.getBoard();
                	tmp[row][col] = player;
                	gameLogic.setBoard(tmp);
                    if (gameLogic.checkWin(player)) { // Check if this move wins the game
                    	tmp = gameLogic.getBoard();
                    	tmp[row][col] = 0;
                    	gameLogic.setBoard(tmp);
                        return new int[] {row, col};
                    }
                	tmp[row][col] = 0;// Undo the move
                	gameLogic.setBoard(tmp); 
                }
            }
        }
        return null;
    }


    private int[] findProgressMove() {
        int player = 2; // Assuming 2 represents AI
        for (int row = 0; row < gameLogic.getRows(); row++) {
            for (int col = 0; col < gameLogic.getCols(); col++) {
                // Check if the cell is valid for a move
                if ((gameLogic.getBoard()[row][col] == 0) && gameLogic.getValidMoves()[row][col] == 1) {
                    // Temporarily make a move
                    int [][] tempBoard = gameLogic.getBoard();
                    tempBoard[row][col] = player;
                    gameLogic.setBoard(tempBoard);

                    // Check if this move creates a line of three
                    if (isProgressMove(row, col, player)) {
                        tempBoard[row][col] = 0; // Undo the move
                        gameLogic.setBoard(tempBoard);
                        return new int[] {row, col};
                    }

                    tempBoard[row][col] = 0; // Undo the move
                    gameLogic.setBoard(tempBoard);
                }
            }
        }
        return null;
    }

    private boolean isProgressMove(int row, int col, int player) {
        // Check horizontal, vertical, and diagonal lines for two chips and an empty space
        return checkLineForProgress(row, col, player, 1, 0) || // Horizontal
               checkLineForProgress(row, col, player, 0, 1) || // Vertical
               checkLineForProgress(row, col, player, 1, 1) || // Diagonal (down-right)
               checkLineForProgress(row, col, player, 1, -1);  // Diagonal (down-left)
    }

    private boolean checkLineForProgress(int row, int col, int player, int dRow, int dCol) {
        int count = 0;
        for (int i = -2; i <= 2; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            if (newRow >= 0 && newRow < gameLogic.getRows() && newCol >= 0 && newCol < gameLogic.getCols()) {
                if (gameLogic.getBoard()[newRow][newCol] == player) {
                    count++;
                } else if (gameLogic.getBoard()[newRow][newCol] != 0) {
                    return false; // The line is blocked by the opponent
                }
            }
        }
        return count == 2;
    }

    private int[] findRandomMove() {
        //randomly pick an empty cell from the board
        List<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < gameLogic.getRows(); row++) {
            for (int col = 0; col < gameLogic.getCols(); col++) {
                if ((gameLogic.getBoard()[row][col] == 0) && gameLogic.getValidMoves()[row][col] == 1) {
                    emptyCells.add(new int[] {row, col});
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            return emptyCells.get(new Random().nextInt(emptyCells.size()));
        }
        return null;
    }

	/**
	 * @return the smart
	 */
	public Boolean getSmart() {
		return smart;
	}

	/**
	 * @param smart the smart to set
	 */
	public void setSmart(Boolean smart) {
		this.smart = smart;
	}
}

