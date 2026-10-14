package fr.monnavigateur.layout;

import java.awt.image.BufferedImage;

public class ElementInline {
    public enum Type { TEXTE, IMAGE }

    public Type type;
    public String texte;
    public String href;
    public boolean gras;
    public boolean italique;
    public BufferedImage image;
    public int x, y, largeur, hauteur;
}