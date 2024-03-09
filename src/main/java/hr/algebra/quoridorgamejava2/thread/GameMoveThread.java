package hr.algebra.quoridorgamejava2.thread;

import hr.algebra.quoridorgamejava2.model.GameMove;
import hr.algebra.quoridorgamejava2.repository.GameMoveRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameMoveThread implements GameMoveRepository {
    private static Boolean isFileAccessInProgress = false;

    private static final String GAME_MOVES_FILE = "files/moves.dat";

    protected synchronized Optional<GameMove> getLastGameMove() {

        if(isFileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        isFileAccessInProgress = true;

        List<GameMove> allGameMoves = getAllGameMoves();

        Optional<GameMove> lastGameMoveOptional = Optional.empty();

        if(!allGameMoves.isEmpty()){
            lastGameMoveOptional = Optional.of(allGameMoves.getLast());
        }

        isFileAccessInProgress = false;

        notifyAll();

        return lastGameMoveOptional;
    }

    @Override
    public synchronized void saveNewGameMove(GameMove gameMove) {

        if(isFileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        isFileAccessInProgress = true;

        List<GameMove> allGameMoves = getAllGameMoves();
        allGameMoves.add(gameMove);

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GAME_MOVES_FILE))) {
            oos.writeObject(allGameMoves);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

        isFileAccessInProgress = false;

        notifyAll();
    }

    @Override
    public synchronized List<GameMove> getAllGameMoves() {
        List<GameMove> gameMoveList = new ArrayList<>();

        if(Files.exists(Path.of(GAME_MOVES_FILE))) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GAME_MOVES_FILE))) {
                gameMoveList.addAll((List<GameMove>) ois.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return gameMoveList;
    }
}
