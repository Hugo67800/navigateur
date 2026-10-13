package fr.monnavigateur.html;

import java.util.HashMap;
import java.util.Map;

public abstract class Jeton {

    public static class BaliseOuvrante extends Jeton {
        public final String nom;
        public final Map<String, String> attributs = new HashMap<>();
        public boolean autoFermante;

        public BaliseOuvrante(String nom) {
            this.nom = nom.toLowerCase();
        }

        @Override
        public String toString() {
            return "<" + nom + attributs + (autoFermante ? "/" : "") + ">";
        }
    }

    public static class BaliseFermante extends Jeton {
        public final String nom;

        public BaliseFermante(String nom) {
            this.nom = nom.toLowerCase();
        }

        @Override
        public String toString() {
            return "</" + nom + ">";
        }
    }

    public static class Texte extends Jeton {
        public final String contenu;

        public Texte(String contenu) {
            this.contenu = contenu;
        }

        @Override
        public String toString() {
            return "\"" + contenu + "\"";
        }
    }
}