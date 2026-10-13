package fr.monnavigateur.html;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;

public class ParseurHtml {

    private static final Set<String> BALISES_AUTO_FERMANTES = Set.of(
            "br", "img", "input", "meta", "link", "hr", "area", "base",
            "col", "embed", "source", "track", "wbr"
    );

    public static Element analyser(String html) {
        List<Jeton> jetons = Tokenizer.tokeniser(html);

        Element racine = new Element("document");
        Deque<Element> pile = new ArrayDeque<>();
        pile.push(racine);

        for (Jeton jeton : jetons) {
            if (jeton instanceof Jeton.BaliseOuvrante bo) {
                Element element = new Element(bo.nom);
                element.attributs.putAll(bo.attributs);
                pile.peek().ajouterEnfant(element);

                if (!bo.autoFermante && !BALISES_AUTO_FERMANTES.contains(bo.nom)) {
                    pile.push(element);
                }

            } else if (jeton instanceof Jeton.BaliseFermante bf) {
                // On dépile jusqu'à trouver la balise correspondante,
                // pour rattraper le HTML mal formé (balises mal fermées)
                if (contientBalise(pile, bf.nom)) {
                    while (!pile.peek().nomBalise.equals(bf.nom)) {
                        pile.pop();
                    }
                    pile.pop();
                }
                // Si la balise fermante n'a pas de correspondance, on l'ignore

            } else if (jeton instanceof Jeton.Texte texte) {
                pile.peek().ajouterEnfant(new NoeudTexte(texte.contenu));
            }
        }

        return racine;
    }

    private static boolean contientBalise(Deque<Element> pile, String nom) {
        for (Element e : pile) {
            if (e.nomBalise.equals(nom)) return true;
        }
        return false;
    }
}