package hr.algebra.quoridorgamejava2.utils;

import hr.algebra.quoridorgamejava2.HelloApplication;
import javafx.scene.control.TextArea;

import java.rmi.RemoteException;
import java.util.List;

public class ChatUtils {

    public static void sendChatMessage(String chatMessage, TextArea chatTextArea)
    {
        try {
            HelloApplication.chatRemoteService.sendChatMessage(HelloApplication.loggedInRoleName + ": " + chatMessage);

            List<String> chatMessages =
                    HelloApplication.chatRemoteService.getAllChatMessages();

            chatTextArea.clear();

            for (String message : chatMessages) {
                chatTextArea.appendText(message + "\n");
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
