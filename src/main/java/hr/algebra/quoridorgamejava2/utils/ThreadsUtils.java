package hr.algebra.quoridorgamejava2.utils;

import hr.algebra.quoridorgamejava2.HelloApplication;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.List;

public class ThreadsUtils {
    public static void startChatTimeLine(TextArea chatTextArea)
    {
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(1000),
                        event -> {
                            List<String> chatMessages = null;
                            try {
                                chatMessages = HelloApplication.chatRemoteService.getAllChatMessages();
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }

                            chatTextArea.clear();

                            for (String message : chatMessages) {
                                chatTextArea.appendText(message + "\n");
                            }
                        }
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
