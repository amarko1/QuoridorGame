package hr.algebra.quoridorgamejava2.model;


import java.util.Comparator;

public class GameMoveSorter implements Comparator<GameMove> {
    @Override
    public int compare(GameMove gm1, GameMove gm2) {
        return gm1.getLocalDateTime().compareTo(gm2.getLocalDateTime());
    }
}
