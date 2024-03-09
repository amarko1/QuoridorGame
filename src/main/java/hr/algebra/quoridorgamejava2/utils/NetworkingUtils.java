package hr.algebra.quoridorgamejava2.utils;

import hr.algebra.quoridorgamejava2.model.ConfigurationKey;
import hr.algebra.quoridorgamejava2.model.ConfigurationReader;
import hr.algebra.quoridorgamejava2.model.GameState;
import hr.algebra.quoridorgamejava2.model.NetworkConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {
    public static void sendGameStateToServer(GameState gameState) {
        try (Socket clientSocket = new Socket(ConfigurationReader.getStringValueForKey(ConfigurationKey.HOST),
                ConfigurationReader.getIntegerValueForKey(ConfigurationKey.SERVER_PORT)))
        {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendGameStateToClient(GameState gameState) {
        try (Socket clientSocket = new Socket(
                ConfigurationReader.getStringValueForKey(ConfigurationKey.HOST),
                ConfigurationReader.getIntegerValueForKey(ConfigurationKey.CLIENT_PORT)))
        {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequest(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.println("Game state sent to server!");
        String confirmationMessage = (String) ois.readObject();
        System.out.println("Confirmation message: " + confirmationMessage);
    }
}
