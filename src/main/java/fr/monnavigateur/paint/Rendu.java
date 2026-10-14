package fr.monnavigateur.paint;

import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.layout.Boite;
import fr.monnavigateur.layout.ElementInline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Rendu extends JPanel {

    private final Boite boiteRacine;
    private Consumer<String> surClicLien = href -> {};

    public Rendu(Boite boiteRacine, int largeur, int hauteur) {
        this.boiteRacine = boiteRacine;
        setPreferredSize(new Dimension(largeur, hauteur));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String href = trouverLien(boiteRacine, e.getX(), e.getY());
                if (href != null) surClicLien.accept(href);
            }
        });
    }

    public void surLienClique(Consumer<String> gestionnaire) {
        this.surClicLien = gestionnaire;
    }

    private String trouverLien(Boite b, int x, int y) {
        for (ElementInline el : b.elementsLigne) {
            if (el.href != null && x >= el.x && x <= el.x + el.largeur && y >= el.y && y <= el.y + el.hauteur) {
                return el.href;
            }
        }
        for (Boite enfant : b.enfants) {
            String resultat = trouverLien(enfant, x, y);
            if (resultat != null) return resultat;
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        dessinerBoite(boiteRacine, g2d);
    }

    private void dessinerBoite(Boite boite, Graphics2D g2d) {
        if (boite.puce != null && !boite.elementsLigne.isEmpty()) {
            Font police = policeDepuisStyle(boite.style, false, false);
            g2d.setFont(police);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(boite.puce, boite.x - 20, boite.y + fm.getAscent());
        }

        for (ElementInline el : boite.elementsLigne) {
            if (el.type == ElementInline.Type.IMAGE) {
                g2d.drawImage(el.image, el.x, el.y, el.largeur, el.hauteur, null);
            } else {
                Font police = policeDepuisStyle(boite.style, el.gras, el.italique);
                g2d.setFont(police);
                FontMetrics fm = g2d.getFontMetrics();
                boolean lien = el.href != null;
                g2d.setColor(lien ? new Color(0, 0, 238) : couleurDepuisStyle(boite.style));
                int baseline = el.y + fm.getAscent();
                g2d.drawString(el.texte, el.x, baseline);
                if (lien) g2d.drawLine(el.x, baseline + 2, el.x + el.largeur, baseline + 2);
            }
        }

        for (Boite enfant : boite.enfants) dessinerBoite(enfant, g2d);
    }

    private Font policeDepuisStyle(StyleCalcule style, boolean grasToken, boolean italiqueToken) {
        String taillePx = style.obtenir("font-size", "16px").replace("px", "").trim();
        int taille;
        try { taille = (int) Double.parseDouble(taillePx); } catch (NumberFormatException e) { taille = 16; }
        boolean gras = grasToken || style.obtenir("font-weight", "normal").equals("bold");
        boolean italique = italiqueToken || style.obtenir("font-style", "normal").equals("italic");
        int styleFonte = Font.PLAIN;
        if (gras) styleFonte |= Font.BOLD;
        if (italique) styleFonte |= Font.ITALIC;
        return new Font("SansSerif", styleFonte, taille);
    }

    private Color couleurDepuisStyle(StyleCalcule style) {
        return convertirCouleur(style.obtenir("color", "black").trim());
    }

    private Color convertirCouleur(String valeur) {
        try {
            if (valeur.startsWith("#")) return Color.decode(valeur);
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
                default -> Color.BLACK;
            };
        } catch (Exception e) {
            return Color.BLACK;
        }
    }
}