package fr.monnavigateur.ui;

import javax.swing.*;
import java.awt.*;

public class Fenetre {

    public static void afficher(String titre, String texte) {
        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = new JFrame(titre);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setSize(900, 600);

            JTextArea zoneTexte = new JTextArea(texte);
            zoneTexte.setLineWrap(true);
            zoneTexte.setWrapStyleWord(true);
            zoneTexte.setEditable(false);
            zoneTexte.setFont(new Font("SansSerif", Font.PLAIN, 14));
            zoneTexte.setMargin(new Insets(10, 10, 10, 10));

            fenetre.add(new JScrollPane(zoneTexte));
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });
    }
}