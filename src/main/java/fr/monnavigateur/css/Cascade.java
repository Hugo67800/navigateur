package fr.monnavigateur.css;

import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.Noeud;

import java.util.*;

public class Cascade {

    private static final Set<String> PROPRIETES_HERITEES = Set.of(
            "color", "font-size", "font-family", "font-weight",
            "font-style", "text-align", "line-height"
    );

    private final List<Regle> regles;
    private final Map<Element, StyleCalcule> styles = new IdentityHashMap<>();

    public Cascade(List<Regle> regles) {
        this.regles = regles;
    }

    public Map<Element, StyleCalcule> calculer(Element racine) {
        calculerRecursif(racine, null);
        return styles;
    }

    private void calculerRecursif(Noeud noeud, StyleCalcule styleParent) {
        if (!(noeud instanceof Element element)) {
            return;
        }

        StyleCalcule style = new StyleCalcule();

        // 1. Héritage : on part des propriétés héritables du parent
        if (styleParent != null) {
            for (String prop : PROPRIETES_HERITEES) {
                if (styleParent.proprietes.containsKey(prop)) {
                    style.proprietes.put(prop, styleParent.proprietes.get(prop));
                }
            }
        }

        // 2. On trouve les règles qui matchent, triées par spécificité croissante
        //    (comme ça, les plus specifiques ecrasent les moins specifiques en etant appliquees apres)
        List<Regle> reglesApplicables = new ArrayList<>();
        for (Regle regle : regles) {
            if (correspond(regle.selecteur, element)) {
                reglesApplicables.add(regle);
            }
        }
        reglesApplicables.sort(Comparator.comparingInt(r -> r.selecteur.specificite()));

        for (Regle regle : reglesApplicables) {
            for (Declaration d : regle.declarations) {
                style.proprietes.put(d.propriete, d.valeur);
            }
        }

        // 3. Style en ligne (attribut style="...") : priorité maximale
        String styleEnLigne = element.attributs.get("style");
        if (styleEnLigne != null) {
            for (Declaration d : ParseurCss.analyserDeclarationsPubliques(styleEnLigne)) {
                style.proprietes.put(d.propriete, d.valeur);
            }
        }

        styles.put(element, style);

        for (Noeud enfant : element.enfants) {
            calculerRecursif(enfant, style);
        }
    }

    private boolean correspond(Selecteur s, Element e) {
        return switch (s.type) {
            case UNIVERSEL -> true;
            case BALISE -> e.nomBalise.equals(s.valeur);
            case CLASSE -> {
                String classes = e.attributs.getOrDefault("class", "");
                yield Arrays.asList(classes.split("\\s+")).contains(s.valeur);
            }
            case ID -> s.valeur.equals(e.attributs.get("id"));
        };
    }
}