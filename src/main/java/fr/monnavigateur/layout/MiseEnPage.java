package fr.monnavigateur.layout;

import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.Noeud;
import fr.monnavigateur.html.NoeudTexte;

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
            List<Mot> mots = extraireMots(element);
            if (!mots.isEmpty()) {
                Font police = policeDepuisStyle(boite.style);
                g2d.setFont(police);
                FontMetrics fm = g2d.getFontMetrics();
                int largeurEspace = fm.stringWidth(" ");

                List<String> lignes = new ArrayList<>();
                List<Lien> liens = new ArrayList<>();
                StringBuilder ligneActuelle = new StringBuilder();
                int largeurLigne = 0;
                int yCourant = y;

                for (Mot mot : mots) {
                    int largeurMot = fm.stringWidth(mot.texte);
                    int espacePrefixe = ligneActuelle.length() > 0 ? largeurEspace : 0;

                    if (largeurLigne + espacePrefixe + largeurMot > largeur && ligneActuelle.length() > 0) {
                        lignes.add(ligneActuelle.toString());
                        ligneActuelle = new StringBuilder();
                        largeurLigne = 0;
                        espacePrefixe = 0;
                        yCourant += fm.getHeight();
                    }

                    int xMot = x + largeurLigne + espacePrefixe;
                    if (espacePrefixe > 0) ligneActuelle.append(" ");
                    ligneActuelle.append(mot.texte);
                    largeurLigne += espacePrefixe + largeurMot;

                    if (mot.href != null) {
                        liens.add(new Lien(mot.href, mot.texte, xMot, yCourant, largeurMot, fm.getHeight()));
                    }
                }
                if (ligneActuelle.length() > 0) {
                    lignes.add(ligneActuelle.toString());
                }

                boite.lignes = lignes;
                boite.liens = liens;
                boite.hauteur = lignes.size() * fm.getHeight();
            } else {
                boite.hauteur = 0;
            }
        }

        return boite;
    }

    private static class Mot {
        String texte;
        String href;
    }

    private static List<Mot> extraireMots(Element element) {
        List<Mot> mots = new ArrayList<>();
        for (Noeud enfant : element.enfants) {
            String href = null;
            String texte;
            if (enfant instanceof Element e && e.nomBalise.equals("a")) {
                href = e.attributs.get("href");
                texte = e.texteVisible();
            } else if (enfant instanceof Element e) {
                texte = e.texteVisible();
            } else if (enfant instanceof NoeudTexte t) {
                texte = t.texte;
            } else {
                continue;
            }
            texte = texte.trim();
            if (texte.isEmpty()) continue;
            for (String m : texte.split("\\s+")) {
                Mot mot = new Mot();
                mot.texte = m;
                mot.href = href;
                mots.add(mot);
            }
        }
        return mots;
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

    private static Font policeDepuisStyle(StyleCalcule style) {
        String taillePx = style.obtenir("font-size", "16px").replace("px", "").trim();
        int taille;
        try {
            taille = (int) Double.parseDouble(taillePx);
        } catch (NumberFormatException e) {
            taille = 16;
        }
        int styleFonte = Font.PLAIN;
        if (style.obtenir("font-weight", "normal").equals("bold")) styleFonte |= Font.BOLD;
        if (style.obtenir("font-style", "normal").equals("italic")) styleFonte |= Font.ITALIC;
        return new Font("SansSerif", styleFonte, taille);
    }
}