package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
    private Label titleLabel;
    @FXML
    private void initialize() {
        gameLogic = new GameLogic(ROWS, COLS);
        aiPlayer = new AIPlayer(gameLogic);
        aiPlayer.setSmart(true);
        animateTitle();
        setupBoard();
        clearMovesFile();
    }

    private void setupBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Circle circle = new Circle(50); // set radius
                circle.setFill(Color.LIGHTGREY);
                circle.setStroke(Color.BLACK); //outline

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
            updateCircle(values[0] /*row*/, values[1] /*column*/, Color.BLUE); //update circle for human player
            writeMoveToFile("Human", 1);
            gameLogic.updateValidMoves();
            if(gameLogic.checkWin(1)) {
                showAlert("Congratulations!", "You win!");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("moves.txt", true))) {
                    writer.write("You Won!");
                }catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }
                return; 
            }

            // AI move logic
            int[] aiMove = aiPlayer.makeMove();
            if(aiMove != null) {
                int [] AI_Values = gameLogic.makeMove(aiMove[0], aiMove[1], GameLogic.Player.AI);
                updateCircle(aiMove[0], aiMove[1], Color.RED); //Update circle for AI player
                gameLogic.updateValidMoves();
                writeMoveToFile("AI", 2);
                if(gameLogic.checkWin(2)) {
                    showAlert("Game Over", "You lose :(");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("moves.txt", true))) {
                        writer.write("You Lost :(");
                    }catch (IOException e) {
                        System.err.println("Error writing to file: " + e.getMessage());
                    }
                    return; 
                }
            } 
        }
    }
    
    private void writeMoveToFile(String player, int i) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("moves.txt", true))) {
            writer.write(player + " (" + i + ")" + " played:\n");

            ArrayList<ArrayList<Integer>> board = gameLogic.getBoard();
            for (ArrayList<Integer> row : board) {
                for (int cell : row) {
                    writer.write(cell + " ");
                }
                writer.write("\n"); 
            }
            writer.write("\n"); 
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    
    private void clearMovesFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("moves.txt"))) {} 
        catch (IOException e) {
            System.err.println("Error clearing the file: " + e.getMessage());
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
                ((Circle) node).setFill(Color.LIGHTGREY);
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
    private void animateTitle() {
        final Timeline timeline = new Timeline(
            new KeyFrame(
                Duration.ZERO,
                event -> titleLabel.setTextFill(Color.RED)
            ),
            new KeyFrame(
                Duration.seconds(0.5),
                event -> titleLabel.setTextFill(Color.BLUE)
            )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }
}
