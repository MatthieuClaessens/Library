package com.fxmat.exo5.Bibliotheque.models;

import java.io.Serializable;

// Classe qui représente un livre dans la bibliothèque
public class Livre implements Serializable {
    private String titre;
    private String auteur;
    private String annee;
    private String genre;
    private boolean disponible;

    // Constructeur classique
    public Livre(String titre, String auteur, String annee, String genre, boolean disponible) {
        this.titre = titre;
        this.auteur = auteur;
        this.annee = annee;
        this.genre = genre;
        this.disponible = disponible;
    }

    // Getters et setters pour chaque propriété
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
