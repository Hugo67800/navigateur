package fr.monnavigateur.css;

public class Selecteur {
    public enum Type { BALISE, CLASSE, ID, UNIVERSEL }

    public final Type type;
    public final String valeur;

    public Selecteur(Type type, String valeur) {
        this.type = type;
        this.valeur = valeur;
    }

    public int specificite() {
        return switch (type) {
            case ID -> 100;
            case CLASSE -> 10;
            case BALISE -> 1;
            case UNIVERSEL -> 0;
        };
    }

    public static Selecteur analyser(String texte) {
        texte = texte.trim();
        if (texte.equals("*")) {
            return new Selecteur(Type.UNIVERSEL, "*");
        } else if (texte.startsWith(".")) {
            return new Selecteur(Type.CLASSE, texte.substring(1));
        } else if (texte.startsWith("#")) {
            return new Selecteur(Type.ID, texte.substring(1));
        } else {
            return new Selecteur(Type.BALISE, texte.toLowerCase());
        }
    }

    @Override
    public String toString() {
        return valeur;
    }
}