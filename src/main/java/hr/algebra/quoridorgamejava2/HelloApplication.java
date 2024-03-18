package hr.algebra.quoridorgamejava2;

import hr.algebra.quoridorgamejava2.chat.ChatRemoteService;
import hr.algebra.quoridorgamejava2.chat.ChatRemoteServiceImpl;
import hr.algebra.quoridorgamejava2.controller.HelloController;
import hr.algebra.quoridorgamejava2.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class HelloApplication extends Application {

    private static Scene mainScene;
    public static RoleName loggedInRoleName;
    public static ChatRemoteService chatRemoteService;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle(loggedInRoleName.name());
        stage.setScene(scene);
        stage.show();
        mainScene = scene;
    }

    public static void main(String[] args) {

        String inputRoleName = args[0];
        loggedInRoleName = RoleName.CLIENT;

        for (RoleName rn : RoleName.values()) {
            if (rn.name().equals(inputRoleName)) {
                loggedInRoleName = rn;
                break;
            }
        }

        new Thread(Application::launch).start();

        if (loggedInRoleName == RoleName.SERVER) {
            startChatService();
            acceptRequestsAsServer();
        } else if (loggedInRoleName == RoleName.CLIENT) {
            startChatClient();
            acceptRequestsAsClient();
        }
    }

    private static  void startChatClient(){
        try {
            Registry registry = LocateRegistry.getRegistry(
                    ConfigurationReader.getStringValueForKey(ConfigurationKey.HOST),
                    ConfigurationReader.getIntegerValueForKey(ConfigurationKey.RMI_PORT));
            chatRemoteService = (ChatRemoteService) registry.lookup(ChatRemoteService.REMOTE_CHAT_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startChatService() {
        try {
            Registry registry = LocateRegistry.createRegistry(
                    ConfigurationReader.getIntegerValueForKey(ConfigurationKey.RMI_PORT));
            chatRemoteService = new ChatRemoteServiceImpl();
            ChatRemoteService skeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(chatRemoteService,
                    ConfigurationReader.getIntegerValueForKey(ConfigurationKey.RANDOM_PORT_HINT));
            registry.rebind(ChatRemoteService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void acceptRequestsAsServer() {
        try (ServerSocket serverSocket = new ServerSocket(
                ConfigurationReader.getIntegerValueForKey(ConfigurationKey.SERVER_PORT))) {
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptRequestsAsClient() {
        try (ServerSocket serverSocket = new ServerSocket(
                ConfigurationReader.getIntegerValueForKey(ConfigurationKey.CLIENT_PORT))) {
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
            GameState gameState = (GameState) ois.readObject();
            Platform.runLater(() ->
                    HelloController.getInstance().setAndUpdateGameState(gameState)
            );
            System.out.println("Game state received!");
            oos.writeObject("Game state received confirmation!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Scene getMainScene(){
        return mainScene;
    }
}