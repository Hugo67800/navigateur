package fr.monnavigateur.css;

public class Declaration {
    public final String propriete;
    public final String valeur;

    public Declaration(String propriete, String valeur) {
        this.propriete = propriete;
        this.valeur = valeur;
    }

    @Override
    public String toString() {
        return propriete + ": " + valeur;
    }
}