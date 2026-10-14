package fr.monnavigateur.layout;

import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;

import java.util.ArrayList;
import java.util.List;

public class Boite {
    public Element element;
    public StyleCalcule style;
    public int x, y, largeur, hauteur;
    public List<String> lignes = new ArrayList<>();
    public List<Lien> liens = new ArrayList<>();
    public List<Boite> enfants = new ArrayList<>();
}