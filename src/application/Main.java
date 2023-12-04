package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Collections;


public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Connect4.fxml"));
        root.setStyle("-fx-background-color: black;"); 
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
    private ArrayList<ArrayList<Integer>> board;
    private ArrayList<ArrayList<Integer>> validMoves;
    private int rows, cols;

    public GameLogic(int rows, int cols) {
        this.setRows(rows);
        this.setCols(cols);
        this.initBoard();
        this.setValidMoves (new ArrayList<>(Collections.nCopies(rows, new ArrayList<>(Collections.nCopies(cols, 0)))));
        this.updateValidMoves();
        reset();
    }
    private void initBoard() {
        // Initialize board with all zeros
        this.board = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            ArrayList<Integer> row = new ArrayList<>(Collections.nCopies(cols, 0));
            board.add(row);
        }		
	}
    
	public void updateValidMoves() {
        // Initialize validMoves with all zeros
        this.validMoves = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            ArrayList<Integer> row = new ArrayList<>(Collections.nCopies(cols, 0));
            validMoves.add(row);
        }

        // Set the first empty cell in each column to 1 in validMoves
        for (int col = 0; col < cols; col++) {
            for (int row = rows - 1; row >= 0; row--) {
                if (this.board.get(row).get(col) == 0) {
                    this.validMoves.get(row).set(col, 1); 
                    break; // Found the first empty cell in this column
                }
            }
        }
        /*// Print validMoves
        System.out.println("Valid Moves");
        for (ArrayList<Integer> row : validMoves) {
            for (int move : row) {
                System.out.print(move + " ");
            }
            System.out.println();
        } */
    }
    
    public int[] makeMove(int row1, int col, Player player) {
        printBoard();
        int[] values = new int[2];
        values[0] = -1; // row index of the successful move
        values[1] = col; //column of the move
        /*i know that this is a workaround, but i realized that when i try to assign one cell value, it will change the whole column. 
         * i tried added unique rows and adding them to the grid, but it still didn't fix the issue. i hope you understand why i had to cast it back and forth like this
         */
        // convert board to 2d array
        int[][] boardArray = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardArray[i][j] = this.board.get(i).get(j);
            }
        }

        //make  move
        for (int row = 0; row < rows; row++) {
            if (getValidMoves().get(row).get(col) == 1) {
                boardArray[row][col] = player == Player.HUMAN ? 1 : 2;
                values[0] = row;
                break;
            }
        }

        // convert back to arrayList
        ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
        for (int i = 0; i < boardArray.length; i++) {
            ArrayList<Integer> rowList = new ArrayList<>();
            for (int j = 0; j < boardArray[i].length; j++) {
                rowList.add(boardArray[i][j]);
            }
            newBoard.add(rowList);
        }

        // update board
        this.setBoard(newBoard);
        updateValidMoves(); // Update valid moves

        //printBoard();
        return values; // Return the move coordinates, or [-1, col] if no valid move was found
    }


    public boolean checkWin(int player) {
        return checkRowsForWin(player) || checkColumnsForWin(player) || checkDiagonalsForWin(player);
    }

    private boolean checkRowsForWin(int player) {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols() - 3; col++) {
                if (getBoard().get(row).get(col) == player && 
                    getBoard().get(row).get(col + 1) == player && 
                    getBoard().get(row).get(col + 2) == player && 
                    getBoard().get(row).get(col + 3) == player) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkColumnsForWin(int player) {
        for (int col = 0; col < getCols(); col++) {
            for (int row = 0; row < getRows() - 3; row++) {
                if (getBoard().get(row).get(col) == player && 
                    getBoard().get(row + 1).get(col) == player && 
                    getBoard().get(row + 2).get(col) == player && 
                    getBoard().get(row + 3).get(col) == player) {
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
                if (getBoard().get(row).get(col) == player && 
                    getBoard().get(row + 1).get(col + 1) == player && 
                    getBoard().get(row + 2).get(col + 2) == player && 
                    getBoard().get(row + 3).get(col + 3) == player) {
                    return true;
                }
            }
        }
        // Check right-to-left diagonals
        for (int row = 0; row < getRows() - 3; row++) {
            for (int col = 3; col < getCols(); col++) {
                if (getBoard().get(row).get(col) == player && 
                    getBoard().get(row + 1).get(col - 1) == player && 
                    getBoard().get(row + 2).get(col - 2) == player && 
                    getBoard().get(row + 3).get(col - 3) == player) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reset() {
        this.setBoard(new ArrayList<>(Collections.nCopies(rows, new ArrayList<>(Collections.nCopies(cols, 0)))));
        this.setValidMoves (new ArrayList<>(Collections.nCopies(rows, new ArrayList<>(Collections.nCopies(cols, 0)))));
        updateValidMoves();
    }

	public int getRows() {return rows;}

	public void setRows(int rows) {this.rows = rows;}

	public int getCols() {return cols;}

	public void setCols(int cols) {this.cols = cols;}

	public ArrayList<ArrayList<Integer>> getBoard() {return board;}

	public void setBoard(ArrayList<ArrayList<Integer>> b) {this.board = b;}

	public ArrayList<ArrayList<Integer>> getValidMoves() {return validMoves;}

	public void setValidMoves(ArrayList<ArrayList<Integer>> vM) {this.validMoves = vM;}
	
	/*
	 * For debugging
	 */
	public void printValidMoves() {
	    System.out.println("Valid Moves:");
	    for (ArrayList<Integer> row : validMoves) {
	        for (int move : row) {
	            System.out.print(move + " ");
	        }
	        System.out.println();
	    }
	}

	public void printBoard() {
	    System.out.println("Board:");
	    for (ArrayList<Integer> row : board) {
	        for (int move : row) {
	            System.out.print(move + " ");
	        }
	        System.out.println();
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
	        int[] winningMove = findWinningMove(2); 
	        if (winningMove != null) {
	        	System.out.println("Found winner");
	            return winningMove;
	        }
	
	        // 2. Block user player's winning move
	        int[] blockingMove = findWinningMove(1); 
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
        // 4. Make a random move as last resort
        System.out.println("random");
        return findRandomMove();
    }

    private int[] findWinningMove(int player) {
        for (int row = 0; row < gameLogic.getRows(); row++) {
            for (int col = 0; col < gameLogic.getCols(); col++) {
                if ((gameLogic.getBoard().get(row).get(col) == 0) && gameLogic.getValidMoves().get(row).get(col) == 1) { 
                	// Temporarily make a move
                	ArrayList<ArrayList<Integer>> tmp = gameLogic.getBoard();
                	tmp.get(row).set(col, player);
                	gameLogic.setBoard(tmp);
                    if (gameLogic.checkWin(player)) { //
                    	tmp = gameLogic.getBoard();
                    	tmp.get(row).set(col, 0);
                    	gameLogic.setBoard(tmp);
                        return new int[] {row, col};
                    }
                    tmp.get(row).set(col, 0);//Undo the move
                	gameLogic.setBoard(tmp); 
                }
            }
        }
        return null;
    }


    private int[] findProgressMove() {
        int player = 2; 
        for (int row = 0; row < gameLogic.getRows(); row++) {
            for (int col = 0; col < gameLogic.getCols(); col++) {
                //check if the cell is valid for a move
                if ((gameLogic.getBoard().get(row).get(col)  == 0) && gameLogic.getValidMoves().get(row).get(col) == 1) {
                    //make a move temporarily
                	ArrayList<ArrayList<Integer>> tempBoard = gameLogic.getBoard();
                	tempBoard.get(row).set(col, player);
                    gameLogic.setBoard(tempBoard);

                    //check if this move creates a line of three
                    if (isProgressMove(row, col, player)) {
                    	tempBoard.get(row).set(col, 0);  //undo
                        gameLogic.setBoard(tempBoard);
                        return new int[] {row, col};
                    }

                    tempBoard.get(row).set(col, 0); //undo
                    gameLogic.setBoard(tempBoard);
                }
            }
        }
        return null;
    }

    private boolean isProgressMove(int row, int col, int player) {
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
                if (gameLogic.getBoard().get(newRow).get(newCol)  == player) {
                    count++;
                } else if (gameLogic.getBoard().get(newRow).get(newCol)  != 0) {
                    return false; //the line is blocked by the opponent
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
                if ((gameLogic.getBoard().get(row).get(col) == 0) && gameLogic.getValidMoves().get(row).get(col) == 1) {
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
	 * @return smart value
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
