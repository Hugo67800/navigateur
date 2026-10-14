package fr.monnavigateur.ui;

import fr.monnavigateur.css.Cascade;
import fr.monnavigateur.css.ExtracteurCss;
import fr.monnavigateur.css.ParseurCss;
import fr.monnavigateur.css.Regle;
import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.ParseurHtml;
import fr.monnavigateur.layout.Boite;
import fr.monnavigateur.layout.MiseEnPage;
import fr.monnavigateur.net.ClientHttp;
import fr.monnavigateur.net.ReponseHttp;
import fr.monnavigateur.net.Url;
import fr.monnavigateur.paint.Rendu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fenetre {

    private final JFrame fenetre;
    private final BarreAdresse barreAdresse;
    private final int largeurContenu = 780;

    private final List<Url> historique = new ArrayList<>();
    private int indexHistorique = -1;

    private Fenetre() {
        fenetre = new JFrame();
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.setSize(820, 700);
        fenetre.setLocationRelativeTo(null);

        barreAdresse = new BarreAdresse();
        barreAdresse.surValidationAdresse(this::naviguerDepuisAdresse);
        barreAdresse.surClicPrecedent(this::precedent);
        barreAdresse.surClicSuivant(this::suivant);

        fenetre.add(barreAdresse, BorderLayout.NORTH);
    }

    public static void demarrer(Url urlInitiale) {
        SwingUtilities.invokeLater(() -> {
            Fenetre f = new Fenetre();
            f.fenetre.setVisible(true);
            f.naviguer(urlInitiale, true);
        });
    }

    private void naviguerDepuisAdresse(String texte) {
        if (texte.isBlank()) return;
        try {
            naviguer(new Url(texte), true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(fenetre, "Adresse invalide : " + e.getMessage());
        }
    }

    private void precedent() {
        if (indexHistorique > 0) {
            indexHistorique--;
            chargerPage(historique.get(indexHistorique));
        }
    }

    private void suivant() {
        if (indexHistorique < historique.size() - 1) {
            indexHistorique++;
            chargerPage(historique.get(indexHistorique));
        }
    }

    private void naviguer(Url url, boolean ajouterHistorique) {
        if (ajouterHistorique) {
            while (historique.size() > indexHistorique + 1) {
                historique.remove(historique.size() - 1);
            }
            historique.add(url);
            indexHistorique = historique.size() - 1;
        }
        chargerPage(url);
    }

    private void chargerPage(Url url) {
        try {
            ReponseHttp reponse = ClientHttp.recuperer(url);
            Element racine = ParseurHtml.analyser(reponse.corps);
            String css = ExtracteurCss.extraireTout(racine, url);
            List<Regle> regles = ParseurCss.analyser(css);
            Map<Element, StyleCalcule> styles = new Cascade(regles).calculer(racine);
            Boite boite = MiseEnPage.disposer(racine, largeurContenu, styles, url);

            Rendu rendu = new Rendu(boite, largeurContenu, boite.hauteur + 40);
            rendu.surLienClique(href -> naviguer(resoudreLien(url, href), true));

            fenetre.getContentPane().removeAll();
            fenetre.getContentPane().add(barreAdresse, BorderLayout.NORTH);
            fenetre.getContentPane().add(new JScrollPane(rendu), BorderLayout.CENTER);
            fenetre.setTitle("Mon navigateur - " + url);

            barreAdresse.afficherAdresse(url.toString());
            barreAdresse.activerBoutons(indexHistorique > 0, indexHistorique < historique.size() - 1);

            fenetre.revalidate();
            fenetre.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(fenetre, "Erreur de chargement : " + e.getMessage());
        }
    }

    private Url resoudreLien(Url base, String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) return new Url(href);
        if (href.startsWith("//")) return new Url(base.schema + ":" + href);
        if (href.startsWith("/")) return new Url(base.schema + "://" + base.hote + ":" + base.port + href);
        String chemin = base.chemin;
        int dernierSlash = chemin.lastIndexOf('/');
        String dossier = dernierSlash >= 0 ? chemin.substring(0, dernierSlash + 1) : "/";
        return new Url(base.schema + "://" + base.hote + ":" + base.port + dossier + href);
    }
}