package hr.algebra.quoridorgamejava2.utils;

import hr.algebra.quoridorgamejava2.model.CellState;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

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
}

