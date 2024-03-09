package hr.algebra.quoridorgamejava2.model;

import hr.algebra.quoridorgamejava2.HelloApplication;
import hr.algebra.quoridorgamejava2.controller.HelloController;
import hr.algebra.quoridorgamejava2.utils.DialogUtils;
import hr.algebra.quoridorgamejava2.utils.GameUtils;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final Integer NUM_OF_ROWS = 17;
    private static final Integer NUM_OF_COLS = 17;
    private String currPlayer;
    private int player1WallsLeft;
    private int player2WallsLeft;
    private CellState[][] gameBoard;
    private String lastMove;

    public GameState() {
        this.currPlayer = "Player1";
        this.player1WallsLeft = 10;
        this.player2WallsLeft = 10;
        this.gameBoard = new CellState[NUM_OF_ROWS][NUM_OF_COLS];
        // Initialize all cells as EMPTY
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                this.gameBoard[i][j] = CellState.EMPTY;
            }
        }
        // starting positions
        gameBoard[0][8] = CellState.PLAYER1;
        gameBoard[NUM_OF_ROWS - 1][8] = CellState.PLAYER2;
    }

    public GameState(String currPlayer, int player1WallsLeft, int player2WallsLeft, CellState[][] gameBoard) {
        this.currPlayer = currPlayer;
        this.player1WallsLeft = player1WallsLeft;
        this.player2WallsLeft = player2WallsLeft;
        this.gameBoard = gameBoard;
    }

    public String getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(String currPlayer) {
        this.currPlayer = currPlayer;
    }

    public int getPlayer1WallsLeft() {
        return player1WallsLeft;
    }

    public void setPlayer1WallsLeft(int player1WallsLeft) {
        this.player1WallsLeft = player1WallsLeft;
    }

    public int getPlayer2WallsLeft() {
        return player2WallsLeft;
    }

    public void setPlayer2WallsLeft(int player2WallsLeft) {
        this.player2WallsLeft = player2WallsLeft;
    }

    public CellState[][] getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(CellState[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void toggleCurrentPlayer() {
        currPlayer = currPlayer.equals("Player1") ? "Player2" : "Player1";
    }

    public String getLastMove() {
        return lastMove;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public void movePlayer(int newRow, int newCol) {
        // Find the current player's position
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {
                if ((currPlayer.equals("Player1") && gameBoard[i][j] == CellState.PLAYER1) ||
                        (currPlayer.equals("Player2") && gameBoard[i][j] == CellState.PLAYER2)) {
                    gameBoard[i][j] = CellState.EMPTY; // Mark old position as EMPTY
                    break; // Assuming one player instance on the board
                }
            }
        }
        // Set the new position based on the current player
        gameBoard[newRow][newCol] = currPlayer.equals("Player1") ? CellState.PLAYER1 : CellState.PLAYER2;
        lastMove = (currPlayer) + "\n" + "(" + (newRow + 1) + ", " + (newCol + 1) + ")";
    }

    public void placeWall(int row, int col, String orientation) {
        if (this.gameBoard == null) {
            System.out.println("GameState gameBoard is null!");
            DialogUtils.showErrorDialog("Error", "Game state error", "GameState gameBoard is null!");
            return; // exit if gameBoard hasn't been initialized
        }
        // Logic to place a wall in the gameBoard
        if ("row".equals(orientation)) {
            // Place a horizontal wall
            for (int offset = 0; offset < 3; offset++) {
                gameBoard[row][col + offset] = CellState.WALL; // Adjust for correct row handling
            }
        } else if ("col".equals(orientation)) {
            // Place a vertical wall
            for (int offset = 0; offset < 3; offset++) {
                gameBoard[row + offset][col] = CellState.WALL; // Adjust for correct column handling
            }
        }
        // Decrement the appropriate player's wall count
        if (this.currPlayer.equals("Player1")) {
            this.player1WallsLeft--;
        } else if (this.currPlayer.equals("Player2")) {
            this.player2WallsLeft--;
        }
        lastMove = (currPlayer) + "\n" + "Wall placed " +  "(" + (row + 1) + ", " + (col + 1) + ")";
    }

}
