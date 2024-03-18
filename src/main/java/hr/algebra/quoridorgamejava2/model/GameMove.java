package hr.algebra.quoridorgamejava2.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GameMove implements Serializable {
    @Serial
    private static final long serialVersionUID = 4699630860174291314L;
    private String player;
    private String position;
    private LocalDateTime localDateTime;

    public GameMove(String player, String position, LocalDateTime localDateTime) {
        this.player = player;
        this.position = position;
        this.localDateTime = localDateTime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMove gameMove = (GameMove) o;
        return Objects.equals(position, gameMove.position)
                && localDateTime.isEqual(gameMove.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, localDateTime);
    }
}
