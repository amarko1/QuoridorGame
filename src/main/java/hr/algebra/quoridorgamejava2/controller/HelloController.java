package hr.algebra.quoridorgamejava2.controller;

import hr.algebra.quoridorgamejava2.model.CellState;
import hr.algebra.quoridorgamejava2.model.GameState;
import hr.algebra.quoridorgamejava2.utils.DialogUtils;
import hr.algebra.quoridorgamejava2.utils.DocumentationUtils;
import hr.algebra.quoridorgamejava2.utils.FileUtils;
import hr.algebra.quoridorgamejava2.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;

public class HelloController {

    private static final Integer NUM_OF_ROWS = 17;
    private static final Integer NUM_OF_COLS = 17;

    @FXML
    private Label playerLabel;

    @FXML
    private Label player1Walls;

    @FXML
    private Label player2Walls;

    @FXML
    private GridPane gameGrid;

    @FXML
    private BorderPane borderPane;

    ImageView Player1;
    ImageView Player2;

    private GameState gameState;


    private void loadGame()
    {
        GameState loadedGameState = FileUtils.loadGame();
        // Check if is not null
        if (loadedGameState != null) {
            // Update the controller's current game state with the loaded state
            this.gameState = loadedGameState;

            // Update the UI to reflect the loaded game state
            updateUI();

            GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);
        } else {
            DialogUtils.showErrorDialog("Error", "Load", "Cannot load saved game");
        }
    }

    private Node getNodeFromGridPane(int row, int col) {
        // retrieves the node at the specified row and column
        for (Node node : gameGrid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return node;
            }
        }
        return null;
    }

    private boolean isAbleToMove(int i, int j) {
        // Ensure the target cell is empty.
        if (gameState.getGameBoard()[i][j] == CellState.EMPTY) {
            CellState currentPlayerState = gameState.getCurrPlayer().equals("Player1") ? CellState.PLAYER1 : CellState.PLAYER2;
            CellState opponentState = currentPlayerState == CellState.PLAYER1 ? CellState.PLAYER2 : CellState.PLAYER1;

            // Check adjacent cells for the current player or opponent, considering walls.
            boolean canMove = checkMove(i, j, currentPlayerState, opponentState);

            if (canMove) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMove(int i, int j, CellState currentPlayerState, CellState opponentState) {
        // Check direct moves.
        if (isDirectMovePossible(i, j, currentPlayerState) || isJumpOverOpponentPossible(i, j, currentPlayerState, opponentState)) {
            return true;
        }
        return false;
    }

    private boolean isDirectMovePossible(int i, int j, CellState currentPlayerState) {
        // Check if a direct move is possible based on the current player's state.
        return ((i + 2 < NUM_OF_ROWS && gameState.getGameBoard()[i + 2][j] == currentPlayerState && gameState.getGameBoard()[i + 1][j] != CellState.WALL) ||
                (i - 2 >= 0 && gameState.getGameBoard()[i - 2][j] == currentPlayerState && gameState.getGameBoard()[i - 1][j] != CellState.WALL) ||
                (j + 2 < NUM_OF_COLS && gameState.getGameBoard()[i][j + 2] == currentPlayerState && gameState.getGameBoard()[i][j + 1] != CellState.WALL) ||
                (j - 2 >= 0 && gameState.getGameBoard()[i][j - 2] == currentPlayerState && gameState.getGameBoard()[i][j - 1] != CellState.WALL));
    }

    private boolean isJumpOverOpponentPossible(int i, int j, CellState currentPlayerState, CellState opponentState) {
        // Check if jumping over an opponent is possible.
        return ((i + 4 < NUM_OF_ROWS && gameState.getGameBoard()[i + 2][j] == opponentState && gameState.getGameBoard()[i + 4][j] == currentPlayerState && gameState.getGameBoard()[i + 1][j] != CellState.WALL && gameState.getGameBoard()[i + 3][j] != CellState.WALL) ||
                (i - 4 >= 0 && gameState.getGameBoard()[i - 2][j] == opponentState && gameState.getGameBoard()[i - 4][j] == currentPlayerState && gameState.getGameBoard()[i - 1][j] != CellState.WALL && gameState.getGameBoard()[i - 3][j] != CellState.WALL) ||
                (j + 4 < NUM_OF_COLS && gameState.getGameBoard()[i][j + 2] == opponentState && gameState.getGameBoard()[i][j + 4] == currentPlayerState && gameState.getGameBoard()[i][j + 1] != CellState.WALL && gameState.getGameBoard()[i][j + 3] != CellState.WALL) ||
                (j - 4 >= 0 && gameState.getGameBoard()[i][j - 2] == opponentState && gameState.getGameBoard()[i][j - 4] == currentPlayerState && gameState.getGameBoard()[i][j - 1] != CellState.WALL && gameState.getGameBoard()[i][j - 3] != CellState.WALL));
    }

    private boolean checkValidity(int row, int col, String orient) {
        // First, determine the current player and their remaining walls.
        int wallsLeft;
        if (gameState.getCurrPlayer().equals("Player1")) {
            wallsLeft = gameState.getPlayer1WallsLeft();
        }
        else {
            wallsLeft = gameState.getPlayer2WallsLeft();
        }

        // If the current player has no walls left, they cannot place a wall.
        if (wallsLeft <= 0) {
            return false;
        }

        // Check if the wall placement is out of bounds.
        if ((col + 2 >= NUM_OF_COLS && orient.equals("row")) || (row + 2 >= NUM_OF_ROWS && orient.equals("col"))) {
            return false;
        }

        // Check if the space for the wall is already occupied.
        CellState[][] gameBoard = gameState.getGameBoard();
        for (int i = 0; i < 2; i++) {
            if (orient.equals("row")) {
                if (gameBoard[row][col + i] != CellState.EMPTY) return false;
            }
            else if (orient.equals("col")) {
                if (gameBoard[row + i][col] != CellState.EMPTY) return false;
            }
        }

        return true;
    }

    private void updateButtonGraphic(Button btn, int i, int j) {
        CellState state = gameState.getGameBoard()[i][j];
        if (state == CellState.PLAYER1) {
            btn.setGraphic(Player1);
        } else if (state == CellState.PLAYER2) {
            btn.setGraphic(Player2);
        } else {
            btn.setGraphic(null); // No graphic for empty cells or walls
        }
    }

    private void updateUI() {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                Node node = getNodeFromGridPane(i, j); // A method to find the corresponding UI element
                if (node instanceof Button) {
                    // Update player positions
                    Button button = (Button) node;
                    CellState state = gameState.getGameBoard()[i][j];
                    if (state == CellState.PLAYER1) {
                        button.setGraphic(Player1);
                    } else if (state == CellState.PLAYER2) {
                        button.setGraphic(Player2);
                    } else {
                        button.setGraphic(null); // Clear the graphic for EMPTY or WALL
                    }
                }
                else if (node instanceof Pane) {
                    // Visually represent walls
                    Pane pane = (Pane) node;
                    CellState state = gameState.getGameBoard()[i][j];
                    if (state == CellState.WALL) {
                        pane.setStyle("-fx-background-color: orange; -fx-border-color: orange;");
                    } else {
                        // Reset the style for empty cells or other purposes
                        pane.setStyle("-fx-background-color: white; -fx-border-color: white;");
                    }
                }
            }
        }
        GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);
    }

    private String determineOrientation(int i, int j) {
        // Assuming the even indices for rows and columns represent buttons (players) and odd ones represent gaps for walls
        if (i % 2 == 0 && j % 2 != 0) {
            // This condition might be interpreted as a vertical wall placement
            return "col";
        } else if (i % 2 != 0 && j % 2 == 0) {
            // This condition might be interpreted as a horizontal wall placement
            return "row";
        }
        return null; // Error case or default case
    }

    public void placeWall(int row, int col, String orientation) {
        gameState.placeWall(row, col, orientation);

        updateUI();

        gameState.toggleCurrentPlayer();

        GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);
    }

    private MenuBar loadMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu editMenu = new Menu("Game Options");
        MenuItem loadMenuItem = new MenuItem("Load Game");
        loadMenuItem.setOnAction(e -> loadGame());

        MenuItem newMenuItem = new MenuItem("New Game");
        newMenuItem.setOnAction(e -> initialize());

        MenuItem saveMenuItem = new MenuItem("Save Game");
        saveMenuItem.setOnAction(e -> FileUtils.saveGame(gameState));
        editMenu.getItems().addAll(newMenuItem, saveMenuItem, loadMenuItem);

        Menu documentationMenu = new Menu("Documentation");
        MenuItem createDocumentation = new MenuItem("Create");
        createDocumentation.setOnAction(e -> generateHtmlDocumentation());
        documentationMenu.getItems().add(createDocumentation);

        menuBar.getMenus().addAll(editMenu, documentationMenu);

        return menuBar;
    }

    public void generateHtmlDocumentation() {
        DocumentationUtils.generateHtmlDocumentationFile();
    }

    public void initialize() {
        gameState = new GameState(); // Initializes game state with default settings

        borderPane.setCenter(gameGrid);
        gameGrid.getChildren().clear();

        GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);

        File f1 = new File("src/images/player1.png");
        File f2 = new File("src/images/player2.png");
        Player1 = new ImageView(new Image(f1.toURI().toString()));
        Player2 = new ImageView(new Image(f2.toURI().toString()));

        MenuBar menu = loadMenuBar();

        borderPane.setTop(menu);

        for (int i = 0; i< NUM_OF_ROWS ; i++){
            for (int j = 0; j< NUM_OF_COLS; j++ ){
                if (i % 2 == 0 && j % 2 == 0){
                    Button btn = new Button();
                    btn.setMaxWidth(100);
                    btn.setMaxHeight(100);
                    int finalI = i;
                    int finalJ = j;
                    btn.setWrapText(true);
                    btn.setOnMouseClicked(e -> {
                        // Check if the move is valid based on the gameState
                        if (isAbleToMove(finalI, finalJ)) {
                            // Update the gameState with the new player position
                            gameState.movePlayer(finalI, finalJ);
                            // Update the UI to reflect the new game state
                            updateUI();
                            // Toggle the current player in gameState
                            gameState.toggleCurrentPlayer();
                            GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);
                        }
                        GameUtils.checkWinner(gameState.getGameBoard(), NUM_OF_ROWS, gameGrid);
                    });
                    gameGrid.add(btn, j, i);
                    // initial graphic for button on the gameState
                    updateButtonGraphic(btn, i, j);
                }
                else {
                    Pane pane = new Pane();
                    gameGrid.add(pane, i, j);
                    pane.setStyle("-fx-background-color: white; -fx-border-color: white;");
                    int finalI = i;
                    int finalJ = j;

                    pane.setOnMouseClicked(e -> {
                        String orientation = determineOrientation(finalJ, finalI);
                        if (checkValidity(finalJ, finalI, orientation)) {
                            placeWall(finalJ, finalI, orientation);
                        } else {
                            System.out.println("Invalid wall placement.");
                        }
                    });
                }

            }
        }
    }
}