package fr.monnavigateur.paint;

import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.layout.Boite;

import javax.swing.*;
import java.awt.*;

public class Rendu extends JPanel {

    private final Boite boiteRacine;

    public Rendu(Boite boiteRacine, int largeur, int hauteur) {
        this.boiteRacine = boiteRacine;
        setPreferredSize(new Dimension(largeur, hauteur));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        dessinerBoite(boiteRacine, g2d);
    }

    private void dessinerBoite(Boite boite, Graphics2D g2d) {
        if (!boite.lignes.isEmpty()) {
            Font police = policeDepuisStyle(boite.style);
            g2d.setFont(police);
            g2d.setColor(couleurDepuisStyle(boite.style));

            FontMetrics fm = g2d.getFontMetrics();
            int yLigne = boite.y + fm.getAscent();
            for (String ligne : boite.lignes) {
                g2d.drawString(ligne, boite.x, yLigne);
                yLigne += fm.getHeight();
            }
        }

        for (Boite enfant : boite.enfants) {
            dessinerBoite(enfant, g2d);
        }
    }

    private Font policeDepuisStyle(StyleCalcule style) {
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

    private Color couleurDepuisStyle(StyleCalcule style) {
        String valeur = style.obtenir("color", "black").trim();
        return convertirCouleur(valeur);
    }

        private Color convertirCouleur(String valeur) {
        try {
            if (valeur.startsWith("#")) {
                return Color.decode(valeur);
            }
            return switch (valeur.toLowerCase()) {
                case "black" -> Color.BLACK;
                case "white" -> Color.WHITE;
                case "red" -> Color.RED;
                case "green" -> new Color(0, 128, 0);
                case "blue" -> Color.BLUE;
                case "darkblue" -> new Color(0, 0, 139);
                case "gray", "grey" -> Color.GRAY;
                case "darkgray", "darkgrey" -> Color.DARK_GRAY;
                case "orange" -> Color.ORANGE;
                case "yellow" -> Color.YELLOW;
                case "purple" -> new Color(128, 0, 128);
                case "pink" -> Color.PINK;
                case "brown" -> new Color(165, 42, 42);
                case "cyan" -> Color.CYAN;
                case "magenta" -> Color.MAGENTA;
                default -> Color.BLACK;
            };
        } catch (Exception e) {
            return Color.BLACK;
        }
    }
}