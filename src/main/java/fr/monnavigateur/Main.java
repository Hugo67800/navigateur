package fr.monnavigateur;

import fr.monnavigateur.css.Cascade;
import fr.monnavigateur.css.ExtracteurCss;
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
        Element racine = ParseurHtml.analyser(reponse.corps);

        String css = ExtracteurCss.extraireTout(racine, url);
        System.out.println("----- CSS EXTRAIT -----");
        System.out.println(css);
        System.out.println("----- FIN CSS -----");

        List<Regle> regles = ParseurCss.analyser(css);
        Map<Element, StyleCalcule> styles = new Cascade(regles).calculer(racine);

        int largeurFenetre = 800;
        Boite boite = MiseEnPage.disposer(racine, largeurFenetre, styles);

        Fenetre.afficher("Mon navigateur - " + url, boite, largeurFenetre);
    }
}