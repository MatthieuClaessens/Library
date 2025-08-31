package com.fxmat.exo5.Bibliotheque.controllers;

import com.fxmat.exo5.Bibliotheque.models.Livre;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// Contrôleur principal de la bibliothèque
public class BibliothequeController {
    // Liens avec les éléments graphiques du FXML
    @FXML private TextField searchBook;
    @FXML private TableView<Livre> tabledata;
    @FXML private TableColumn<Livre, String> titreColumn;
    @FXML private TableColumn<Livre, String> auteurColumn;
    @FXML private TableColumn<Livre, String> anneeColumn;
    @FXML private TableColumn<Livre, String> genreColumn;
    @FXML private TableColumn<Livre, Boolean> dispoColumn;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private CheckBox dispoCheckBox;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label detailTitre;
    @FXML private Label detailAuteur;
    @FXML private Label detailAnnee;
    @FXML private Label detailGenre;
    @FXML private Label detailDispo;

    // Liste observable pour la TableView
    private ObservableList<Livre> observableBooks;
    // Liste principale des livres (persistée)
    private List<Livre> booksList = new ArrayList<>();

    // Méthode appelée automatiquement à l'initialisation du contrôleur
    @FXML
    public void initialize() {
        // On charge les livres depuis le fichier de sauvegarde
        loadBooksFromFile();
        observableBooks = FXCollections.observableArrayList(booksList);
        tabledata.setItems(observableBooks);

        // On relie chaque colonne de la table à la propriété correspondante du livre
        titreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitre()));
        auteurColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuteur()));
        anneeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnnee()));
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));
        dispoColumn.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isDisponible()));

        // Pour la colonne disponibilité, on affiche "Oui" ou "Non" au lieu de true/false
        dispoColumn.setCellFactory(col -> new TableCell<Livre, Boolean>() {
            @Override
            protected void updateItem(Boolean dispo, boolean empty) {
                super.updateItem(dispo, empty);
                setText(empty ? "" : (dispo ? "Oui" : "Non"));
            }
        });

        // On remplit la ComboBox des genres à partir des livres existants
        updateGenreComboBox();

        // On relie les filtres à la méthode de filtrage
        genreComboBox.setOnAction(e -> filterBooks());
        dispoCheckBox.setOnAction(e -> filterBooks());
        searchBook.textProperty().addListener((obs, oldVal, newVal) -> filterBooks());

        // Actions des boutons principaux
        addButton.setOnAction(e -> onClickAddButton());
        editButton.setOnAction(e -> onEditButton());
        deleteButton.setOnAction(e -> onDeleteButton());

        // Quand on sélectionne un livre dans la table, on affiche ses détails en bas
        tabledata.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends Livre> obs, Livre oldSel, Livre newSel) -> updateDetailLabels(newSel)
        );

        // On vide les détails au lancement
        updateDetailLabels(null);
    }

    // Met à jour la ComboBox des genres avec tous les genres présents dans la liste
    private void updateGenreComboBox() {
        Set<String> genres = booksList.stream().map(Livre::getGenre).filter(g -> g != null && !g.isEmpty()).collect(Collectors.toSet());
        genreComboBox.getItems().clear();
        genreComboBox.getItems().add(""); // Pour "tous genres"
        genreComboBox.getItems().addAll(genres);
        genreComboBox.getSelectionModel().selectFirst();
    }

    // Filtre la liste des livres selon la recherche, le genre et la disponibilité
    private void filterBooks() {
        String query = searchBook.getText().trim().toLowerCase();
        String genre = genreComboBox.getValue();
        boolean onlyAvailable = dispoCheckBox.isSelected();
        List<Livre> filtered = booksList.stream()
            .filter(l -> (query.isEmpty() || l.getTitre().toLowerCase().contains(query) || l.getAuteur().toLowerCase().contains(query)))
            .filter(l -> (genre == null || genre.isEmpty() || l.getGenre().equals(genre)))
            .filter(l -> (!onlyAvailable || l.isDisponible()))
            .collect(Collectors.toList());
        observableBooks.setAll(filtered);
    }

    // Affiche les détails du livre sélectionné dans la zone du bas
    private void updateDetailLabels(Livre livre) {
        if (livre == null) {
            detailTitre.setText("");
            detailAuteur.setText("");
            detailAnnee.setText("");
            detailGenre.setText("");
            detailDispo.setText("");
        } else {
            detailTitre.setText(livre.getTitre());
            detailAuteur.setText(livre.getAuteur());
            detailAnnee.setText(livre.getAnnee());
            detailGenre.setText(livre.getGenre());
            detailDispo.setText(livre.isDisponible() ? "Oui" : "Non");
        }
    }

    // Ouvre la fenêtre d'ajout d'un livre (modale)
    @FXML
    protected void onClickAddButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fxmat/exo5/Bibliotheque/optional-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            OptionalBookController optController = loader.getController();
            optController.initData(booksList, OptionalBookController.Mode.ADD, null);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ajouter un livre");
            stage.initOwner(addButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL); // La fenêtre bloque la principale
            stage.showAndWait(); // On attend la fermeture
            // Après fermeture, on recharge la liste
            loadBooksFromFile();
            observableBooks.setAll(booksList);
            updateGenreComboBox();
            filterBooks();
            updateDetailLabels(null); // On vide les détails
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ouvre la fenêtre de modification du livre sélectionné
    @FXML
    protected void onEditButton() {
        Livre selected = tabledata.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un livre à modifier.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fxmat/exo5/Bibliotheque/optional-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            OptionalBookController optController = loader.getController();
            optController.initData(booksList, OptionalBookController.Mode.EDIT, selected);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier le livre");
            stage.initOwner(editButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
            loadBooksFromFile();
            observableBooks.setAll(booksList);
            updateGenreComboBox();
            filterBooks();
            updateDetailLabels(tabledata.getSelectionModel().getSelectedItem());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Supprime le livre sélectionné après confirmation
    @FXML
    protected void onDeleteButton() {
        Livre selected = tabledata.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un livre à supprimer.");
            return;
        }
        booksList.remove(selected);
        saveBooksToFile();
        observableBooks.setAll(booksList);
        updateGenreComboBox();
        filterBooks();
        updateDetailLabels(null);
    }

    // Lance le filtrage quand on clique sur "Rechercher" ou appuie sur Entrée
    @FXML
    protected void onSearch(javafx.event.ActionEvent event) {
        filterBooks();
    }

    // Affiche une boîte de dialogue d'erreur
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Charge la liste des livres depuis le fichier books.ser (si présent)
    private void loadBooksFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("books.ser"))) {
            booksList = (List<Livre>) ois.readObject();
        } catch (Exception e) {
            booksList = new ArrayList<>();
        }
    }

    // Sauvegarde la liste des livres dans le fichier books.ser
    public void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books.ser"))) {
            oos.writeObject(booksList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Permet d'accéder à la liste des livres depuis l'extérieur si besoin
    public List<Livre> getBooksList() {
        return booksList;
    }
}
