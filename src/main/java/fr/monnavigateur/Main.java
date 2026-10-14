package fr.monnavigateur;

import fr.monnavigateur.css.Cascade;
import fr.monnavigateur.css.ParseurCss;
import fr.monnavigateur.css.Regle;
import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.ParseurHtml;
import fr.monnavigateur.layout.Boite;
import fr.monnavigateur.layout.MiseEnPage;
import fr.monnavigateur.ui.Fenetre;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        String html = "<html><body><h1>Mon titre</h1><p>Un paragraphe de test avec un peu plus de texte pour voir le retour a la ligne fonctionner correctement.</p></body></html>";
        Element racine = ParseurHtml.analyser(html);

        String css = """
            p { color: #333333; font-size: 18px; }
            h1 { color: darkblue; font-size: 28px; font-weight: bold; }
            """;
        List<Regle> regles = ParseurCss.analyser(css);
        Map<Element, StyleCalcule> styles = new Cascade(regles).calculer(racine);

        int largeurFenetre = 800;
        Boite boite = MiseEnPage.disposer(racine, largeurFenetre, styles);

        Fenetre.afficher("Mon navigateur", boite, largeurFenetre);
    }
}