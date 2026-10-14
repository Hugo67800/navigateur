package fr.monnavigateur;

import fr.monnavigateur.css.Cascade;
import fr.monnavigateur.css.ParseurCss;
import fr.monnavigateur.css.Regle;
import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.ParseurHtml;
import fr.monnavigateur.layout.Boite;
import fr.monnavigateur.layout.MiseEnPage;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String html = "<html><body><p>Ceci est un paragraphe assez long pour tester le retour a la ligne automatique dans notre moteur de mise en page.</p><p>Un deuxieme paragraphe, plus court.</p></body></html>";
        String css = "p { font-size: 18px; }";

        Element racine = ParseurHtml.analyser(html);
        List<Regle> regles = ParseurCss.analyser(css);
        Map<Element, StyleCalcule> styles = new Cascade(regles).calculer(racine);

        Boite boite = MiseEnPage.disposer(racine, 400, styles);
        afficherBoite(boite, 0);
    }

    private static void afficherBoite(Boite b, int profondeur) {
        String indentation = "  ".repeat(profondeur);
        System.out.println(indentation + b.element.nomBalise + " [" + b.x + "," + b.y + " "
                + b.largeur + "x" + b.hauteur + "] lignes=" + b.lignes);
        for (Boite enfant : b.enfants) {
            afficherBoite(enfant, profondeur + 1);
        }
    }
}