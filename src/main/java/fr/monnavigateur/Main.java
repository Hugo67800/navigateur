package fr.monnavigateur;

import fr.monnavigateur.css.Cascade;
import fr.monnavigateur.css.ParseurCss;
import fr.monnavigateur.css.Regle;
import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.ParseurHtml;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String html = "<html><body><p class=\"intro\">Bonjour <b>monde</b></p><p id=\"special\">Autre</p></body></html>";
        String css = """
            p { color: black; font-size: 16px; }
            .intro { color: red; }
            #special { color: green; }
            """;

        Element racine = ParseurHtml.analyser(html);
        List<Regle> regles = ParseurCss.analyser(css);

        Cascade cascade = new Cascade(regles);
        Map<Element, StyleCalcule> styles = cascade.calculer(racine);

        for (Map.Entry<Element, StyleCalcule> entree : styles.entrySet()) {
            System.out.println(entree.getKey() + " -> " + entree.getValue());
        }
    }
}