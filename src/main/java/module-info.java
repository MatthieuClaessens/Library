module com.fxmat.exo5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    exports com.fxmat.exo5.Bibliotheque; // Ajouté pour permettre à JavaFX d'instancier Main
    exports com.fxmat.exo5.Bibliotheque.controllers;

    opens com.fxmat.exo5.Bibliotheque to javafx.fxml;
    opens com.fxmat.exo5.Bibliotheque.controllers to javafx.fxml;
    opens com.fxmat.exo5.Bibliotheque.models to javafx.fxml;
}
