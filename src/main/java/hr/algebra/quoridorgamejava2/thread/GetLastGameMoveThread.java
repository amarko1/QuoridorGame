package hr.algebra.quoridorgamejava2.thread;

import hr.algebra.quoridorgamejava2.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.Optional;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable {

    private TextArea lastGameMoveLabel;

    public GetLastGameMoveThread(TextArea lastGameMoveLabel) {
        this.lastGameMoveLabel = lastGameMoveLabel;
    }

    @Override
    public void run() {

        while(true) {
            Optional<GameMove> lastGameMoveOptional = getLastGameMove();

            if(lastGameMoveOptional.isPresent()) {
                Platform.runLater(() -> {
                    lastGameMoveLabel.setText("The last game move: \n"
                            + lastGameMoveOptional.get().getPosition() + " \n"
                            + lastGameMoveOptional.get().getLocalDateTime());
                });
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
