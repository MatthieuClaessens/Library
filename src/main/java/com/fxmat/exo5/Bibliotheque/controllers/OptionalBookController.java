package com.fxmat.exo5.Bibliotheque.controllers;

import com.fxmat.exo5.Bibliotheque.models.Livre;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Contrôleur pour la fenêtre d'ajout ou de modification d'un livre
public class OptionalBookController {

    // Enum pour savoir si on est en mode ajout ou édition
    public enum Mode {
        ADD, EDIT
    }

    private Mode currentMode;
    private Livre bookToEdit;
    private List<Livre> booksList; // On récupère la liste de la fenêtre principale

    // Liens avec les éléments graphiques du FXML
    @FXML private Label titleOptionalBook;
    @FXML private TextField titreField;
    @FXML private TextField auteurField;
    @FXML private TextField anneeField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private CheckBox dispoCheckBox;
    @FXML private Button validateButton;

    /**
     * Méthode appelée par la fenêtre principale pour initialiser la fenêtre d'ajout/modif
     */
    public void initData(List<Livre> booksList, Mode mode, Livre bookToEdit) {
        this.booksList = booksList;
        this.currentMode = mode;
        this.bookToEdit = bookToEdit;

        // On prépare une liste de genres par défaut
        List<String> defaultGenres = Arrays.asList(
            "Fiction", "Animé", "Roman", "Policier", "Science-fiction", "Fantastique",
            "Biographie", "Essai", "Poésie", "Théâtre", "Jeunesse", "Manga", "BD"
        );
        // On récupère tous les genres déjà présents dans la liste
        Set<String> genres = booksList.stream()
            .map(Livre::getGenre)
            .filter(g -> g != null && !g.trim().isEmpty())
            .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
        // On ajoute les genres par défaut (pas de doublons)
        genres.addAll(defaultGenres);
        genreComboBox.getItems().clear();
        genreComboBox.getItems().addAll(genres);
        genreComboBox.setEditable(true); // On laisse l'utilisateur saisir un nouveau genre

        // On adapte le titre de la fenêtre et on pré-remplit les champs si besoin
        if (mode == Mode.ADD) {
            titleOptionalBook.setText("Ajouter un livre");
        } else if (mode == Mode.EDIT && bookToEdit != null) {
            titleOptionalBook.setText("Modifier le livre");
            titreField.setText(bookToEdit.getTitre());
            auteurField.setText(bookToEdit.getAuteur());
            anneeField.setText(bookToEdit.getAnnee());
            genreComboBox.setValue(bookToEdit.getGenre());
            dispoCheckBox.setSelected(bookToEdit.isDisponible());
        }
    }

    /**
     * Quand on clique sur "Valider", on vérifie les champs et on ajoute/modifie le livre
     */
    @FXML
    protected void onValidateButton() {
        String titre = titreField.getText().trim();
        String auteur = auteurField.getText().trim();
        String annee = anneeField.getText().trim();
        String genre = genreComboBox.getValue() != null ? genreComboBox.getValue().trim() : "";
        boolean dispo = dispoCheckBox.isSelected();

        // On vérifie que tous les champs sont remplis
        if (titre.isEmpty() || auteur.isEmpty() || annee.isEmpty() || genre.isEmpty()) {
            showAlert("Tous les champs sont obligatoires, y compris le genre.");
            return;
        }

        // On vérifie que l'année est bien un nombre
        try {
            Integer.parseInt(annee);
        } catch (NumberFormatException e) {
            showAlert("L'année de sortie doit être un nombre.");
            return;
        }

        // Selon le mode, on ajoute ou on modifie le livre
        if (currentMode == Mode.ADD) {
            Livre newBook = new Livre(titre, auteur, annee, genre, dispo);
            booksList.add(newBook);
        } else if (currentMode == Mode.EDIT && bookToEdit != null) {
            bookToEdit.setTitre(titre);
            bookToEdit.setAuteur(auteur);
            bookToEdit.setAnnee(annee);
            bookToEdit.setGenre(genre);
            bookToEdit.setDisponible(dispo);
        }

        // On sauvegarde la liste dans le fichier
        saveBooksToFile();

        // On ferme la fenêtre d'ajout/modif
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    // Affiche une boîte de dialogue d'erreur
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Sauvegarde la liste des livres dans le fichier books.ser
    private void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books.ser"))) {
            oos.writeObject(booksList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
