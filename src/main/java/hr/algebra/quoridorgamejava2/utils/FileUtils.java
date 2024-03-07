package hr.algebra.quoridorgamejava2.utils;

import hr.algebra.quoridorgamejava2.model.GameState;

import java.io.*;

public class FileUtils {

    public static final String filePath = "gameState.ser";

    public static void saveGame(GameState gameState) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(gameState);

            DialogUtils.showInformationDialog("Information Dialog", "Game Saved", "Game Saved");

        } catch (IOException e) {
            DialogUtils.showErrorDialog("Error Dialog", "Save Error", "Error saving game data: "  + e.getMessage());
        }
    }

    public static GameState loadGame() {
        GameState gameState = null;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            gameState = (GameState) ois.readObject();

            DialogUtils.showInformationDialog("Information Dialog",
                    "Game Loaded", "Game Data has been loaded.");

        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showErrorDialog("Error Dialog",
                    "Load Error", "Error loading game data: "  + e.getMessage());
        }

        return gameState;
    }

}
