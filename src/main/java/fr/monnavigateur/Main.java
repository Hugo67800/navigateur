package fr.monnavigateur;

import fr.monnavigateur.css.Cascade;
import fr.monnavigateur.css.ParseurCss;
import fr.monnavigateur.css.Regle;
import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.ParseurHtml;
import fr.monnavigateur.layout.Boite;
import fr.monnavigateur.layout.MiseEnPage;
import fr.monnavigateur.net.ClientHttp;
import fr.monnavigateur.net.ReponseHttp;
import fr.monnavigateur.net.Url;
import fr.monnavigateur.ui.Fenetre;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Url url = new Url("https://example.com");
        ReponseHttp reponse = ClientHttp.recuperer(url);

        System.out.println("----- HTML BRUT -----");
        System.out.println(reponse.corps);
        System.out.println("----- FIN HTML -----");

        Element racine = ParseurHtml.analyser(reponse.corps);

        String css = """
            p { color: #333333; font-size: 18px; }
            h1 { color: darkblue; font-size: 28px; font-weight: bold; }
            """;
        List<Regle> regles = ParseurCss.analyser(css);
        Map<Element, StyleCalcule> styles = new Cascade(regles).calculer(racine);

        int largeurFenetre = 800;
        Boite boite = MiseEnPage.disposer(racine, largeurFenetre, styles);

        afficherBoite(boite, 0);

        Fenetre.afficher("Mon navigateur - " + url, boite, largeurFenetre);
    }

    private static void afficherBoite(Boite b, int profondeur) {
        String indentation = "  ".repeat(profondeur);
        System.out.println(indentation + b.element.nomBalise + " [" + b.x + "," + b.y + " "
                + b.largeur + "x" + b.hauteur + "] style=" + b.style + " lignes=" + b.lignes);
        for (Boite enfant : b.enfants) {
            afficherBoite(enfant, profondeur + 1);
        }
    }
}