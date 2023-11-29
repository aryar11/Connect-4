package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Connect4Controller {

    @FXML
    private GridPane gameGrid;

    private static final int ROWS = 6;
    private static final int COLS = 7;
    private Button[][] boardButtons = new Button[ROWS][COLS];
    private GameLogic gameLogic;
    private AIPlayer aiPlayer;

    @FXML
    private void initialize() {
        gameLogic = new GameLogic(ROWS, COLS);
        aiPlayer = new AIPlayer(gameLogic);
        setupBoard();
    }

    private void setupBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Circle circle = new Circle(50); // Set the radius as needed
                circle.setFill(Color.WHITE); // Default color
                circle.setStroke(Color.BLACK); // Outline color

                final int finalRow = row;
                final int finalCol = col;

                circle.setOnMouseClicked(e -> makeMove(finalRow, finalCol));

                gameGrid.add(circle, col, row);
                // Store the circle reference if needed
            }
        }
    }

    private void makeMove(int row, int col) {
    	int[] values = gameLogic.makeMove(row, col, GameLogic.Player.HUMAN);
    	if(values[0] != -1) { //if row value is not -1
	        updateCircle(values[0] /*row*/, values[1] /*column*/, Color.BLUE); // Update circle for human player
	        gameLogic.updateValidMoves();
	        // AI move logic
	        int[] aiMove = aiPlayer.makeMove();
	        int [] AI_Values = gameLogic.makeMove(aiMove[0], aiMove[1], GameLogic.Player.AI);
            updateCircle(aiMove[0], aiMove[1], Color.RED); // Update circle for AI player
            gameLogic.updateValidMoves();
	        
    	}
    }

    private void updateCircle(int row, int col, Color color) {
        for (Node node : gameGrid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col && node instanceof Circle) {
                ((Circle) node).setFill(color);
                break;
            }
        }
    }



    @FXML
    private void resetGame() {
        gameLogic.reset();
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Circle) {
                ((Circle) node).setFill(Color.WHITE); // Reset color to white or another neutral color
            }
        }
        
    }

}
