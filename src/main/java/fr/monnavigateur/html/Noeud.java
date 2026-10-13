package fr.monnavigateur.html;

import java.util.ArrayList;
import java.util.List;

public abstract class Noeud {
    public Noeud parent;
    public final List<Noeud> enfants = new ArrayList<>();

    public void ajouterEnfant(Noeud enfant) {
        enfant.parent = this;
        enfants.add(enfant);
    }

    public void afficher(int profondeur) {
        String indentation = "  ".repeat(profondeur);
        System.out.println(indentation + this);
        for (Noeud enfant : enfants) {
            enfant.afficher(profondeur + 1);
        }
    }
}