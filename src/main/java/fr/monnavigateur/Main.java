package fr.monnavigateur;

import fr.monnavigateur.net.Url;
import fr.monnavigateur.ui.Fenetre;

public class Main {
    public static void main(String[] args) {
        Fenetre.demarrer(new Url("http://info.cern.ch/hypertext/WWW/TheProject.html"));
    }
}