package fr.monnavigateur.css;

import java.util.ArrayList;
import java.util.List;

public class ParseurCss {

    public static List<Regle> analyser(String css) {
        List<Regle> regles = new ArrayList<>();
        css = retirerCommentaires(css);

        int i = 0;
        int n = css.length();

        while (i < n) {
            while (i < n && Character.isWhitespace(css.charAt(i))) i++;
            if (i >= n) break;

            int debutSelecteur = i;
            int accoladeOuvrante = css.indexOf('{', i);
            if (accoladeOuvrante < 0) break;

            String texteSelecteurs = css.substring(debutSelecteur, accoladeOuvrante).trim();

            int accoladeFermante = css.indexOf('}', accoladeOuvrante);
            if (accoladeFermante < 0) break;

            String texteDeclarations = css.substring(accoladeOuvrante + 1, accoladeFermante);
            List<Declaration> declarations = analyserDeclarationsPubliques(texteDeclarations);

            // Un bloc peut avoir plusieurs sélecteurs séparés par des virgules : p, b { ... }
            for (String s : texteSelecteurs.split(",")) {
                s = s.trim();
                if (s.isEmpty()) continue;
                Regle regle = new Regle(Selecteur.analyser(s));
                regle.declarations.addAll(declarations);
                regles.add(regle);
            }

            i = accoladeFermante + 1;
        }

        return regles;
    }

    public static List<Declaration> analyserDeclarationsPubliques(String texte) {
        List<Declaration> declarations = new ArrayList<>();
        for (String partie : texte.split(";")) {
            int deuxPoints = partie.indexOf(':');
            if (deuxPoints < 0) continue;
            String propriete = partie.substring(0, deuxPoints).trim().toLowerCase();
            String valeur = partie.substring(deuxPoints + 1).trim();
            if (!propriete.isEmpty() && !valeur.isEmpty()) {
                declarations.add(new Declaration(propriete, valeur));
            }
        }
        return declarations;
    }

    private static String retirerCommentaires(String css) {
        StringBuilder resultat = new StringBuilder();
        int i = 0;
        int n = css.length();
        while (i < n) {
            if (i + 1 < n && css.charAt(i) == '/' && css.charAt(i + 1) == '*') {
                int fin = css.indexOf("*/", i + 2);
                i = (fin < 0) ? n : fin + 2;
            } else {
                resultat.append(css.charAt(i));
                i++;
            }
        }
        return resultat.toString();
    }
}