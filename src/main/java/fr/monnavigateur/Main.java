package fr.monnavigateur;

import fr.monnavigateur.net.ClientHttp;
import fr.monnavigateur.net.ReponseHttp;
import fr.monnavigateur.net.Url;

public class Main {
    public static void main(String[] args) throws Exception {
        Url url = new Url("http://github.com");
        ReponseHttp reponse = ClientHttp.recuperer(url);

        System.out.println("Statut final : " + reponse.statut);
        System.out.println("Debut du corps :");
        System.out.println(reponse.corps.substring(0, Math.min(300, reponse.corps.length())));
    }
}