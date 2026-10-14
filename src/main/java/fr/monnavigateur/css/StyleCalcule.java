package fr.monnavigateur.css;

import java.util.HashMap;
import java.util.Map;

public class StyleCalcule {
    public final Map<String, String> proprietes = new HashMap<>();

    public String obtenir(String propriete, String parDefaut) {
        return proprietes.getOrDefault(propriete, parDefaut);
    }

    @Override
    public String toString() {
        return proprietes.toString();
    }
}