package fr.monnavigateur;

import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.ParseurHtml;
import fr.monnavigateur.net.ClientHttp;
import fr.monnavigateur.net.ReponseHttp;
import fr.monnavigateur.net.Url;
import fr.monnavigateur.ui.Fenetre;

public class Main {
    public static void main(String[] args) throws Exception {
        Url url = new Url("https://example.com");
        ReponseHttp reponse = ClientHttp.recuperer(url);
        Element racine = ParseurHtml.analyser(reponse.corps);

        String texte = racine.texteVisible();
        Fenetre.afficher("Mon navigateur - " + url, texte);
    }
}