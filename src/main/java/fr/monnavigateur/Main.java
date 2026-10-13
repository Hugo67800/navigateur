package fr.monnavigateur;

import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.ParseurHtml;

public class Main {
    public static void main(String[] args) {
        String html = "<html><body><p class=\"intro\">Bonjour <b>monde</b> !</p><br><img src=\"x.png\"></body></html>";
        Element racine = ParseurHtml.analyser(html);
        racine.afficher(0);
    }
}