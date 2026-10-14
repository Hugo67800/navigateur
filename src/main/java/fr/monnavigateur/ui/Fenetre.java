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
import java.util.List;
import java.util.Map;

public class Fenetre {

    public static void demarrer(Url urlInitiale) {
        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = new JFrame();
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setSize(820, 700);
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
            chargerPage(fenetre, urlInitiale, 780);
        });
    }

    private static void chargerPage(JFrame fenetre, Url url, int largeur) {
        try {
            ReponseHttp reponse = ClientHttp.recuperer(url);
            Element racine = ParseurHtml.analyser(reponse.corps);
            String css = ExtracteurCss.extraireTout(racine, url);
            List<Regle> regles = ParseurCss.analyser(css);
            Map<Element, StyleCalcule> styles = new Cascade(regles).calculer(racine);
            Boite boite = MiseEnPage.disposer(racine, largeur, styles);

            Rendu rendu = new Rendu(boite, largeur, boite.hauteur + 40);
            rendu.surLienClique(href -> {
                Url nouvelleUrl = resoudreLien(url, href);
                chargerPage(fenetre, nouvelleUrl, largeur);
            });

            fenetre.getContentPane().removeAll();
            fenetre.getContentPane().add(new JScrollPane(rendu));
            fenetre.setTitle("Mon navigateur - " + url);
            fenetre.revalidate();
            fenetre.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(fenetre, "Erreur de chargement : " + e.getMessage());
        }
    }

    private static Url resoudreLien(Url base, String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return new Url(href);
        }
        if (href.startsWith("//")) {
            return new Url(base.schema + ":" + href);
        }
        if (href.startsWith("/")) {
            return new Url(base.schema + "://" + base.hote + ":" + base.port + href);
        }
        String chemin = base.chemin;
        int dernierSlash = chemin.lastIndexOf('/');
        String dossier = dernierSlash >= 0 ? chemin.substring(0, dernierSlash + 1) : "/";
        return new Url(base.schema + "://" + base.hote + ":" + base.port + dossier + href);
    }
}