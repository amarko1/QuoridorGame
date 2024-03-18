package hr.algebra.quoridorgamejava2.thread;

import hr.algebra.quoridorgamejava2.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable {

    private TextArea lastGameMoveArea;

    public GetLastGameMoveThread(TextArea lastGameMoveArea) {
        this.lastGameMoveArea = lastGameMoveArea;
    }

    @Override
    public void run() {

        while(true) {
            Optional<GameMove> lastGameMoveOptional = getLastGameMove();

            if(lastGameMoveOptional.isPresent()) {
                Platform.runLater(() -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");
                    String formattedDateTime = lastGameMoveOptional.get().getLocalDateTime().format(formatter);

                    String textToSet = lastGameMoveOptional.get().getPlayer() + "\n"
                            + lastGameMoveOptional.get().getPosition() + " \n"
                            + formattedDateTime;

                    lastGameMoveArea.setText(textToSet);
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
