module hr.algebra.quoridorgamejava2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens hr.algebra.quoridorgamejava2 to javafx.fxml;
    exports hr.algebra.quoridorgamejava2;
    exports hr.algebra.quoridorgamejava2.controller;
    opens hr.algebra.quoridorgamejava2.controller to javafx.fxml;
}