package hr.algebra.quoridorgamejava2.utils;

import javafx.scene.control.Alert;

public class DialogUtils {
    public static void showInformationDialog(String title, String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.show();
    }

    public static void showErrorDialog(String title, String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.show();
    }
}
