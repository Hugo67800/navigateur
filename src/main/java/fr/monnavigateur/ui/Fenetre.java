package fr.monnavigateur.ui;

import fr.monnavigateur.layout.Boite;
import fr.monnavigateur.paint.Rendu;

import javax.swing.*;

public class Fenetre {

    public static void afficher(String titre, Boite boiteRacine, int largeur) {
        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = new JFrame(titre);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Rendu rendu = new Rendu(boiteRacine, largeur, boiteRacine.hauteur + 40);
            JScrollPane scroll = new JScrollPane(rendu);

            fenetre.add(scroll);
            fenetre.setSize(largeur + 20, 700);
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });
    }
}