package hr.algebra.quoridorgamejava2.utils;

import hr.algebra.quoridorgamejava2.model.CellState;
import hr.algebra.quoridorgamejava2.model.GameState;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GameUtils {
    public static void checkWinner(CellState[][] gameBoard, int numRows, GridPane gameGrid) {
        boolean isWinner = false;
        String winner = "";

        for (int i = 0; i < numRows; i += 2) {
            if (gameBoard[0][i] == CellState.PLAYER2) {
                winner = "Player 2";
                isWinner = true;
                break;
            } else if (gameBoard[numRows - 1][i] == CellState.PLAYER1) {
                winner = "Player 1";
                isWinner = true;
                break;
            }
        }

        if (isWinner) {
            DialogUtils.showInformationDialog("Winner", winner, winner + " won the game");
            disableBoard(gameGrid);
        }
    }

    public static void disableBoard(GridPane gameGrid) {
        for (Node node : gameGrid.getChildren()) {
            node.setDisable(true);
        }
    }

    public static void enableBoard(GridPane gameGrid) {
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(false);
            }
            else if(node instanceof Pane){
                node.setDisable(false);
            }
        }
    }

    public static void updatePlayerLabel(GameState gameState, Label playerLabel, Label player1Walls, Label player2Walls ) {
        if (gameState.getCurrPlayer().equals("Player2")) {
            playerLabel.setText("Player 2");
            playerLabel.setTextFill(Color.ROYALBLUE);
        }
        else { // Default
            playerLabel.setText("Player 1");
            playerLabel.setTextFill(Color.TOMATO);
        }

        player1Walls.setText(String.valueOf(gameState.getPlayer1WallsLeft()));
        player2Walls.setText(String.valueOf(gameState.getPlayer2WallsLeft()));
    }
}

