package fr.monnavigateur.html;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static List<Jeton> tokeniser(String html) {
        List<Jeton> jetons = new ArrayList<>();
        int i = 0;
        int n = html.length();

        while (i < n) {
            if (html.charAt(i) == '<') {
                int finBalise = html.indexOf('>', i);
                if (finBalise < 0) {
                    // balise jamais fermée : on arrête
                    break;
                }
                String contenuBalise = html.substring(i + 1, finBalise);

                if (contenuBalise.startsWith("!") || contenuBalise.startsWith("?")) {
                    // commentaire ou directive (<!-- -->, <!DOCTYPE>) : on ignore pour l'instant
                    i = finBalise + 1;
                    continue;
                }

                if (contenuBalise.startsWith("/")) {
                    String nom = contenuBalise.substring(1).trim();
                    jetons.add(new Jeton.BaliseFermante(nom));
                } else {
                    jetons.add(analyserBaliseOuvrante(contenuBalise));
                }

                i = finBalise + 1;
            } else {
                int prochainChevron = html.indexOf('<', i);
                if (prochainChevron < 0) {
                    prochainChevron = n;
                }
                String texte = html.substring(i, prochainChevron);
                if (!texte.isBlank()) {
                    jetons.add(new Jeton.Texte(texte));
                }
                i = prochainChevron;
            }
        }

        return jetons;
    }

    private static Jeton.BaliseOuvrante analyserBaliseOuvrante(String contenu) {
        boolean autoFermante = contenu.endsWith("/");
        if (autoFermante) {
            contenu = contenu.substring(0, contenu.length() - 1).trim();
        }

        int i = 0;
        int n = contenu.length();
        while (i < n && !Character.isWhitespace(contenu.charAt(i))) {
            i++;
        }
        String nom = contenu.substring(0, i);
        Jeton.BaliseOuvrante jeton = new Jeton.BaliseOuvrante(nom);
        jeton.autoFermante = autoFermante;

        String resteAttributs = contenu.substring(i).trim();
        analyserAttributs(resteAttributs, jeton);

        return jeton;
    }

    private static void analyserAttributs(String texte, Jeton.BaliseOuvrante jeton) {
        int i = 0;
        int n = texte.length();

        while (i < n) {
            while (i < n && Character.isWhitespace(texte.charAt(i))) i++;
            if (i >= n) break;

            int debutNom = i;
            while (i < n && texte.charAt(i) != '=' && !Character.isWhitespace(texte.charAt(i))) {
                i++;
            }
            String nomAttr = texte.substring(debutNom, i).toLowerCase();
            if (nomAttr.isEmpty()) break;

            while (i < n && Character.isWhitespace(texte.charAt(i))) i++;

            String valeur = "";
            if (i < n && texte.charAt(i) == '=') {
                i++;
                while (i < n && Character.isWhitespace(texte.charAt(i))) i++;
                if (i < n && (texte.charAt(i) == '"' || texte.charAt(i) == '\'')) {
                    char guillemet = texte.charAt(i);
                    i++;
                    int debutVal = i;
                    while (i < n && texte.charAt(i) != guillemet) i++;
                    valeur = texte.substring(debutVal, i);
                    if (i < n) i++;
                } else {
                    int debutVal = i;
                    while (i < n && !Character.isWhitespace(texte.charAt(i))) i++;
                    valeur = texte.substring(debutVal, i);
                }
            }

            jeton.attributs.put(nomAttr, valeur);
        }
    }
}