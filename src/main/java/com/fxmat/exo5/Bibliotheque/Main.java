package com.fxmat.exo5.Bibliotheque;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

// Point d'entrée de l'application JavaFX
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // On charge la vue principale depuis le fichier FXML
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/com/fxmat/exo5/Bibliotheque/bibliotheque-view.fxml")
        );

        // On crée la scène principale avec une taille de base
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        // On applique la feuille de style CSS
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/fxmat/exo5/Bibliotheque/style.css")
                ).toExternalForm()
        );


        // On configure la fenêtre principale
        stage.setTitle("Bibliothèque");
        stage.setScene(scene);
        stage.show();
    }

    // Méthode main classique pour lancer l'application
    public static void main(String[] args) {
        launch();
    }
}
