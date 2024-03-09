package hr.algebra.quoridorgamejava2.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GameMove implements Serializable {
    private String position;
    private LocalDateTime localDateTime;

    public GameMove(String position, LocalDateTime localDateTime) {
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
}
