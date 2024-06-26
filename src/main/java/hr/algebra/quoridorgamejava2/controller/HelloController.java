package hr.algebra.quoridorgamejava2.controller;

import hr.algebra.quoridorgamejava2.HelloApplication;
import hr.algebra.quoridorgamejava2.model.CellState;
import hr.algebra.quoridorgamejava2.model.GameMove;
import hr.algebra.quoridorgamejava2.model.GameState;
import hr.algebra.quoridorgamejava2.model.RoleName;
import hr.algebra.quoridorgamejava2.thread.GetLastGameMoveThread;
import hr.algebra.quoridorgamejava2.thread.SaveNewGameMoveThread;
import hr.algebra.quoridorgamejava2.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.File;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.getLogger;

public class HelloController {
    private static HelloController instance;

    public HelloController() {
        instance = this;
    }

    public static synchronized HelloController getInstance() {
        if (instance == null) {
            instance = new HelloController();
        }
        return instance;
    }

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

    @FXML
    private TextField chatMessageTextField;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextArea lastGameMoveTextArea;
    private ImageView Player1;
    private ImageView Player2;
    private GameState gameState;

    public void loadGame()
    {
        GameState loadedGameState = FileUtils.loadGame();
        if (loadedGameState != null) {
            this.gameState = loadedGameState;

            updateUI(gameState);

            GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);
        } else {
            DialogUtils.showErrorDialog("Error", "Load error", "Cannot load saved game");
        }
    }
    public void saveGame(){
        FileUtils.saveGame(gameState);
    }
    public void generateHtmlDocumentation() {
        DocumentationUtils.generateHtmlDocumentationFile();
    }

    public void replayGame() {
        Set<GameMove> gameMoves = XmlUtils.readAllGameMoves();
        final AtomicInteger counter = new AtomicInteger(0);

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (counter.get() < gameMoves.size()) {
                GameMove gameMove = (GameMove) gameMoves.toArray()[counter.getAndIncrement()];
                String[] parts = gameMove.getPosition().split(",");
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());

                Node node = getNodeFromGridPane(row, col);
                if (node instanceof Button) {
                    Button button = (Button) node;
                    ImageView playerGraphic = gameMove.getPlayer().equals("Player1") ? Player1 : Player2;
                    button.setGraphic(playerGraphic);

                } else if (node instanceof Pane) {
                    Pane pane = (Pane) node;
                    pane.setStyle("-fx-background-color: white; -fx-border-color: white;");
                }
            }
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
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
            btn.setGraphic(null);
        }
    }

    public void setAndUpdateGameState(GameState newState) {
        this.gameState = newState;

        updateUI(newState);
    }

    public void updateUI(GameState gameState) {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                Node node = getNodeFromGridPane(i, j); // A method to find the corresponding UI element
                if (node instanceof Button) {
                    // Update player positions
                    Button button = (Button) node;
                    updateButtonGraphic(button, i, j);
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
        GameUtils.enableBoard(gameGrid);
        GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);
        GameUtils.checkWinner(gameState.getGameBoard(), NUM_OF_ROWS, gameGrid);

        GameMove gameMove = new GameMove(gameState.getLastPlayer(), gameState.getLastMove(), LocalDateTime.now());

        XmlUtils.saveGameMove(gameMove);

        Set<GameMove> gameMoveList = XmlUtils.readAllGameMoves();

        SaveNewGameMoveThread saveNewGameMoveThread = new SaveNewGameMoveThread(gameMove);
        Thread starterThread = new Thread(saveNewGameMoveThread);
        starterThread.start();
    }

    public void placeWall(int row, int col, String orientation) {
        gameState.placeWall(row, col, orientation);
        gameState.toggleCurrentPlayer();

        updateUI(gameState);

        GameUtils.updatePlayerLabel(gameState, playerLabel, player1Walls, player2Walls);
    }

    public static void sendGameState(GameState gameState, GridPane gameGrid) {
        if (HelloApplication.loggedInRoleName == RoleName.CLIENT) {
            NetworkingUtils.sendGameStateToServer(gameState);
            GameUtils.disableBoard(gameGrid);
        } else if (HelloApplication.loggedInRoleName == RoleName.SERVER) {
            NetworkingUtils.sendGameStateToClient(gameState);
            GameUtils.disableBoard(gameGrid);
        }
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

        if (HelloApplication.loggedInRoleName != RoleName.SINGLE_PLAYER) {

            ThreadsUtils.startChatTimeLine(chatTextArea);
        }

        GetLastGameMoveThread getLastGameMoveThread = new GetLastGameMoveThread(lastGameMoveTextArea);
        Thread starterThread = new Thread(getLastGameMoveThread);
        starterThread.start();

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
                        if (isAbleToMove(finalI, finalJ)) {
                            // Update the gameState with the new player position
                            gameState.movePlayer(finalI, finalJ);

                            gameState.toggleCurrentPlayer();

                            updateUI(gameState);

                            sendGameState(gameState, gameGrid);
                        }
                    });
                    gameGrid.add(btn, j, i);
                    // initial graphic for button
                    updateButtonGraphic(btn, i, j);
                }
                else {
                    Pane pane = new Pane();
                    gameGrid.add(pane, i, j);
                    pane.setStyle("-fx-background-color: white; -fx-border-color: white;");
                    int finalI = i;
                    int finalJ = j;
                    pane.setOnMouseClicked(e -> {
                        String orientation = gameState.determineOrientation(finalJ, finalI);
                        if (checkValidity(finalJ, finalI, orientation)) {
                            placeWall(finalJ, finalI, orientation);
                            sendGameState(gameState, gameGrid);
                        } else {
                            System.out.println("Invalid wall placement.");
                        }
                    });
                }
                if (HelloApplication.loggedInRoleName == RoleName.SERVER) {
                    GameUtils.disableBoard(gameGrid);
                }
            }
        }
    }
    public void sendChatMessage(){
        String chatMessage = chatMessageTextField.getText();
        chatMessageTextField.clear();
        ChatUtils.sendChatMessage(chatMessage, chatTextArea);
    }
}