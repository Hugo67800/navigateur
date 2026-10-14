package fr.monnavigateur.layout;

public class Lien {
    public final String href;
    public final String texte;
    public final int x, y, largeur, hauteur;

    public Lien(String href, String texte, int x, int y, int largeur, int hauteur) {
        this.href = href;
        this.texte = texte;
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
}