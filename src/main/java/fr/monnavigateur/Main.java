package fr.monnavigateur;

import fr.monnavigateur.css.ParseurCss;
import fr.monnavigateur.css.Regle;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String css = """
            p { color: red; font-size: 20px; }
            .intro, b { font-weight: bold; }
            #titre { color: blue; }
            """;

        List<Regle> regles = ParseurCss.analyser(css);
        for (Regle r : regles) {
            System.out.println(r);
        }
    }
}