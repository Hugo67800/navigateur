package fr.monnavigateur.html;

import java.util.HashMap;
import java.util.Map;

public class Element extends Noeud {
    public final String nomBalise;
    public final Map<String, String> attributs = new HashMap<>();

    public Element(String nomBalise) {
        this.nomBalise = nomBalise.toLowerCase();
    }

    @Override
    public String toString() {
        return "<" + nomBalise + ">";
    }
}