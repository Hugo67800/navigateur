package fr.monnavigateur.css;

import java.util.ArrayList;
import java.util.List;

public class Regle {
    public final Selecteur selecteur;
    public final List<Declaration> declarations = new ArrayList<>();

    public Regle(Selecteur selecteur) {
        this.selecteur = selecteur;
    }

    @Override
    public String toString() {
        return selecteur + " " + declarations;
    }
}