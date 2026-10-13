package fr.monnavigateur.html;

public class NoeudTexte extends Noeud {
    public final String texte;

    public NoeudTexte(String texte) {
        this.texte = texte;
    }

    @Override
    public String toString() {
        return "\"" + texte + "\"";
    }
}