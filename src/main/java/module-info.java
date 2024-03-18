module hr.algebra.quoridorgamejava2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.naming;
    requires java.xml;


    exports hr.algebra.quoridorgamejava2.chat to java.rmi;
    opens hr.algebra.quoridorgamejava2 to javafx.fxml;
    exports hr.algebra.quoridorgamejava2;
    exports hr.algebra.quoridorgamejava2.model;
    opens hr.algebra.quoridorgamejava2.model to javafx.fxml;
    exports hr.algebra.quoridorgamejava2.controller;
    opens hr.algebra.quoridorgamejava2.controller to javafx.fxml;
}