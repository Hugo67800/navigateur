package fr.monnavigateur.css;

import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.Noeud;
import fr.monnavigateur.html.NoeudTexte;
import fr.monnavigateur.net.ClientHttp;
import fr.monnavigateur.net.ReponseHttp;
import fr.monnavigateur.net.Url;

public class ExtracteurCss {

    public static String extraireTout(Element racine, Url urlPage) {
        StringBuilder css = new StringBuilder();
        collecter(racine, urlPage, css);
        return css.toString();
    }

    private static void collecter(Noeud noeud, Url urlPage, StringBuilder css) {
        if (noeud instanceof Element e) {
            if (e.nomBalise.equals("style")) {
                css.append(texteDirect(e)).append("\n");
            } else if (e.nomBalise.equals("link")
                    && "stylesheet".equalsIgnoreCase(e.attributs.getOrDefault("rel", ""))) {
                String href = e.attributs.get("href");
                if (href != null) {
                    try {
                        Url urlCss = resoudre(urlPage, href);
                        ReponseHttp reponse = ClientHttp.recuperer(urlCss);
                        css.append(reponse.corps).append("\n");
                    } catch (Exception ex) {
                        System.out.println("Impossible de charger le CSS : " + href + " (" + ex.getMessage() + ")");
                    }
                }
            }
        }
        for (Noeud enfant : noeud.enfants) {
            collecter(enfant, urlPage, css);
        }
    }

    private static String texteDirect(Element e) {
        StringBuilder sb = new StringBuilder();
        for (Noeud enfant : e.enfants) {
            if (enfant instanceof NoeudTexte t) {
                sb.append(t.texte);
            }
        }
        return sb.toString();
    }

    private static Url resoudre(Url base, String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return new Url(href);
        }
        if (href.startsWith("//")) {
            return new Url(base.schema + ":" + href);
        }
        if (href.startsWith("/")) {
            return new Url(base.schema + "://" + base.hote + ":" + base.port + href);
        }
        // chemin relatif simple (pas de gestion de ../ pour l'instant)
        String chemin = base.chemin;
        int dernierSlash = chemin.lastIndexOf('/');
        String dossier = dernierSlash >= 0 ? chemin.substring(0, dernierSlash + 1) : "/";
        return new Url(base.schema + "://" + base.hote + ":" + base.port + dossier + href);
    }
}