package fr.monnavigateur.layout;

import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.Noeud;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class MiseEnPage {

    private static final Set<String> BALISES_BLOC = Set.of(
            "html", "body", "document", "div", "p",
            "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "li", "header", "footer",
            "section", "article", "nav", "main"
    );

    private static final int MARGE_VERTICALE = 8;

    public static Boite disposer(Element racine, int largeur, Map<Element, StyleCalcule> styles) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        Boite boite = disposerBloc(racine, 0, 0, largeur, styles, g2d);

        g2d.dispose();
        return boite;
    }

    private static Boite disposerBloc(Element element, int x, int y, int largeur,
                                       Map<Element, StyleCalcule> styles, Graphics2D g2d) {
        Boite boite = new Boite();
        boite.element = element;
        boite.style = styles.getOrDefault(element, new StyleCalcule());
        boite.x = x;
        boite.y = y;
        boite.largeur = largeur;

        List<Element> enfantsBlocs = enfantsBlocs(element);

        if (!enfantsBlocs.isEmpty()) {
            int yCourant = y;
            for (Element enfant : enfantsBlocs) {
                Boite boiteEnfant = disposerBloc(enfant, x, yCourant, largeur, styles, g2d);
                boite.enfants.add(boiteEnfant);
                yCourant += boiteEnfant.hauteur + MARGE_VERTICALE;
            }
            boite.hauteur = yCourant - y;
        } else {
            String texte = element.texteVisible().trim().replaceAll("\\s+", " ");
            if (!texte.isEmpty()) {
                Font police = policeDepuisStyle(boite.style);
                g2d.setFont(police);
                FontMetrics fm = g2d.getFontMetrics();

                boite.lignes = couperEnLignes(texte, largeur, fm);
                boite.hauteur = boite.lignes.size() * fm.getHeight();
            } else {
                boite.hauteur = 0;
            }
        }

        return boite;
    }

    private static List<Element> enfantsBlocs(Element element) {
        List<Element> resultat = new ArrayList<>();
        for (Noeud enfant : element.enfants) {
            if (enfant instanceof Element e && BALISES_BLOC.contains(e.nomBalise)) {
                resultat.add(e);
            }
        }
        return resultat;
    }

    private static List<String> couperEnLignes(String texte, int largeurMax, FontMetrics fm) {
        List<String> lignes = new ArrayList<>();
        String[] mots = texte.split(" ");
        StringBuilder ligneActuelle = new StringBuilder();

        for (String mot : mots) {
            String essai = ligneActuelle.isEmpty() ? mot : ligneActuelle + " " + mot;
            if (fm.stringWidth(essai) > largeurMax && !ligneActuelle.isEmpty()) {
                lignes.add(ligneActuelle.toString());
                ligneActuelle = new StringBuilder(mot);
            } else {
                ligneActuelle = new StringBuilder(essai);
            }
        }
        if (!ligneActuelle.isEmpty()) {
            lignes.add(ligneActuelle.toString());
        }
        return lignes;
    }

    private static Font policeDepuisStyle(StyleCalcule style) {
        String taillePx = style.obtenir("font-size", "16px").replace("px", "").trim();
        int taille;
        try {
            taille = (int) Double.parseDouble(taillePx);
        } catch (NumberFormatException e) {
            taille = 16;
        }

        int styleFonte = Font.PLAIN;
        if (style.obtenir("font-weight", "normal").equals("bold")) {
            styleFonte |= Font.BOLD;
        }
        if (style.obtenir("font-style", "normal").equals("italic")) {
            styleFonte |= Font.ITALIC;
        }

        return new Font("SansSerif", styleFonte, taille);
    }
}