package hr.algebra.quoridorgamejava2.repository;

import hr.algebra.quoridorgamejava2.model.GameMove;

import java.util.List;

public interface GameMoveRepository {
    void saveNewGameMove(GameMove gameMove);
    List<GameMove> getAllGameMoves();
}
