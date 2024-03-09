package hr.algebra.quoridorgamejava2.thread;

import hr.algebra.quoridorgamejava2.model.GameMove;

public class SaveNewGameMoveThread extends GameMoveThread implements Runnable  {
    private GameMove gameMove;

    public SaveNewGameMoveThread(GameMove gameMove) {
        this.gameMove = gameMove;
    }

    @Override
    public void run() {
        saveNewGameMove(gameMove);
    }
}
