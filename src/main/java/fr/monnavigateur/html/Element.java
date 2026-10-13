package fr.monnavigateur.html;

import java.util.HashMap;
import java.util.Map;

public class Element extends Noeud {
    public final String nomBalise;
    public final Map<String, String> attributs = new HashMap<>();

    public Element(String nomBalise) {
        this.nomBalise = nomBalise.toLowerCase();
    }

    public String texteVisible() {
        StringBuilder sb = new StringBuilder();
        collecterTexte(this, sb);
        return sb.toString();
    }

    private static void collecterTexte(Noeud noeud, StringBuilder sb) {
        if (noeud instanceof Element e) {
            if (e.nomBalise.equals("script") || e.nomBalise.equals("style")) {
                return; // on ignore le JS et le CSS
            }
        }
        if (noeud instanceof NoeudTexte t) {
            sb.append(t.texte);
        }
        for (Noeud enfant : noeud.enfants) {
            collecterTexte(enfant, sb);
        }
    }

    @Override
    public String toString() {
        return "<" + nomBalise + ">";
    }
}