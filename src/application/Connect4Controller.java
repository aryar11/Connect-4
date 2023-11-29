package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class Connect4Controller {

    @FXML
    private GridPane gameGrid;
    @FXML
    private Button toggleSmartButton;
    @FXML
    private Button resetButton;
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private Button[][] boardButtons = new Button[ROWS][COLS];
    private GameLogic gameLogic;
    private AIPlayer aiPlayer;

    @FXML
    private void initialize() {
        gameLogic = new GameLogic(ROWS, COLS);
        aiPlayer = new AIPlayer(gameLogic);
        aiPlayer.setSmart(true);
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
            if(gameLogic.checkWin(1)) {
                showAlert("Congratulations!", "You win!");
                return; 
            }

            // AI move logic
            int[] aiMove = aiPlayer.makeMove();
            if(aiMove != null) {
                int [] AI_Values = gameLogic.makeMove(aiMove[0], aiMove[1], GameLogic.Player.AI);
                updateCircle(aiMove[0], aiMove[1], Color.RED); // Update circle for AI player
                gameLogic.updateValidMoves();
                if(gameLogic.checkWin(2)) {
                    showAlert("Game Over", "You lose :(");
                    return; 
                }
            } else {
                // Handle case where AI cannot make a move
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message + "\n\nPress OK to Restart");
        alert.showAndWait().ifPresent(response -> resetGame());
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
    @FXML
    private void toggleAISmartMode() {
        if (aiPlayer.getSmart()) {
            aiPlayer.setSmart(false);
            toggleSmartButton.setText("AI Smart Mode: OFF");
        } else {
            aiPlayer.setSmart(true);
            toggleSmartButton.setText("AI Smart Mode: ON");
        }
    }

}
