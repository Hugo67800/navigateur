package fr.monnavigateur.layout;

import fr.monnavigateur.css.StyleCalcule;
import fr.monnavigateur.html.Element;
import fr.monnavigateur.html.Noeud;
import fr.monnavigateur.html.NoeudTexte;
import fr.monnavigateur.net.ClientHttp;
import fr.monnavigateur.net.ReponseBinaire;
import fr.monnavigateur.net.Url;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.List;

public class MiseEnPage {

    private static final Set<String> BALISES_BLOC = Set.of(
            "html", "body", "document", "div", "p",
            "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "li", "header", "footer",
            "section", "article", "nav", "main"
    );

    private static final int MARGE_VERTICALE = 8;
    private static final int INDENT_LISTE = 28;
    private static final int LARGEUR_IMAGE_MAX = 400;

    public static Boite disposer(Element racine, int largeur, Map<Element, StyleCalcule> styles, Url baseUrl) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        Boite boite = disposerBloc(racine, 0, 0, largeur, styles, g2d, baseUrl);
        g2d.dispose();
        return boite;
    }

    private static Boite disposerBloc(Element element, int x, int y, int largeur,
                                       Map<Element, StyleCalcule> styles, Graphics2D g2d, Url baseUrl) {
        Boite boite = new Boite();
        boite.element = element;
        boite.style = styles.getOrDefault(element, new StyleCalcule());
        boite.x = x;
        boite.y = y;
        boite.largeur = largeur;

        List<Element> enfantsBlocs = enfantsBlocs(element);

        if (!enfantsBlocs.isEmpty()) {
            boolean estListe = element.nomBalise.equals("ul") || element.nomBalise.equals("ol");
            int compteur = 1;
            int yCourant = y;
            for (Element enfant : enfantsBlocs) {
                int xEnfant = x;
                int largeurEnfant = largeur;
                String puce = null;
                if (estListe && enfant.nomBalise.equals("li")) {
                    xEnfant = x + INDENT_LISTE;
                    largeurEnfant = Math.max(10, largeur - INDENT_LISTE);
                    puce = element.nomBalise.equals("ol") ? (compteur++ + ".") : "\u2022";
                }
                Boite boiteEnfant = disposerBloc(enfant, xEnfant, yCourant, largeurEnfant, styles, g2d, baseUrl);
                boiteEnfant.puce = puce;
                boite.enfants.add(boiteEnfant);
                yCourant += boiteEnfant.hauteur + MARGE_VERTICALE;
            }
            boite.hauteur = yCourant - y;
        } else {
            List<ElementInline> elements = new ArrayList<>();
            for (Noeud enfant : element.enfants) {
                elements.addAll(extraireElements(enfant, false, false, null, baseUrl));
            }
            disposerElementsEnLigne(elements, boite, x, y, largeur, g2d);
        }

        return boite;
    }

    private static void disposerElementsEnLigne(List<ElementInline> elements, Boite boite,
                                                  int x, int y, int largeur, Graphics2D g2d) {
        if (elements.isEmpty()) {
            boite.hauteur = 0;
            return;
        }

        int xCourant = x;
        int yCourant = y;
        int ligneHauteurMax = 0;

        for (ElementInline e : elements) {
            int largeurToken, hauteurToken, largeurEspace;
            if (e.type == ElementInline.Type.IMAGE) {
                largeurToken = e.largeur;
                hauteurToken = e.hauteur;
                largeurEspace = 6;
            } else {
                Font police = policeDepuisStyle(boite.style, e.gras, e.italique);
                g2d.setFont(police);
                FontMetrics fm = g2d.getFontMetrics();
                largeurToken = fm.stringWidth(e.texte);
                hauteurToken = fm.getHeight();
                largeurEspace = fm.stringWidth(" ");
            }

            int espacePrefixe = (xCourant > x) ? largeurEspace : 0;
            if (xCourant + espacePrefixe + largeurToken > x + largeur && xCourant > x) {
                yCourant += ligneHauteurMax;
                xCourant = x;
                ligneHauteurMax = 0;
                espacePrefixe = 0;
            }

            e.x = xCourant + espacePrefixe;
            e.y = yCourant;
            e.largeur = largeurToken;
            e.hauteur = hauteurToken;

            xCourant = e.x + largeurToken;
            ligneHauteurMax = Math.max(ligneHauteurMax, hauteurToken);
            boite.elementsLigne.add(e);
        }

        yCourant += ligneHauteurMax;
        boite.hauteur = yCourant - y;
    }

    private static List<ElementInline> extraireElements(Noeud noeud, boolean gras, boolean italique,
                                                          String href, Url baseUrl) {
        List<ElementInline> resultat = new ArrayList<>();

        if (noeud instanceof NoeudTexte t) {
            String texte = t.texte.trim();
            if (!texte.isEmpty()) {
                for (String mot : texte.split("\\s+")) {
                    if (mot.isEmpty()) continue;
                    ElementInline e = new ElementInline();
                    e.type = ElementInline.Type.TEXTE;
                    e.texte = mot;
                    e.gras = gras;
                    e.italique = italique;
                    e.href = href;
                    resultat.add(e);
                }
            }
            return resultat;
        }

        if (noeud instanceof Element el) {
            if (el.nomBalise.equals("script") || el.nomBalise.equals("style")) return resultat;

            if (el.nomBalise.equals("img")) {
                BufferedImage img = chargerImage(el.attributs.get("src"), baseUrl);
                if (img != null) {
                    ElementInline e = new ElementInline();
                    e.type = ElementInline.Type.IMAGE;
                    e.image = img;
                    int largeurImg = img.getWidth();
                    int hauteurImg = img.getHeight();
                    if (largeurImg > LARGEUR_IMAGE_MAX) {
                        double ratio = (double) LARGEUR_IMAGE_MAX / largeurImg;
                        largeurImg = LARGEUR_IMAGE_MAX;
                        hauteurImg = (int) (hauteurImg * ratio);
                    }
                    e.largeur = largeurImg;
                    e.hauteur = hauteurImg;
                    resultat.add(e);
                }
                return resultat;
            }

            boolean nouveauGras = gras || el.nomBalise.equals("b") || el.nomBalise.equals("strong");
            boolean nouvelItalique = italique || el.nomBalise.equals("i") || el.nomBalise.equals("em");
            String nouveauHref = el.nomBalise.equals("a") ? el.attributs.get("href") : href;

            for (Noeud enfant : el.enfants) {
                resultat.addAll(extraireElements(enfant, nouveauGras, nouvelItalique, nouveauHref, baseUrl));
            }
        }

        return resultat;
    }

    private static BufferedImage chargerImage(String src, Url baseUrl) {
        if (src == null || src.isBlank() || baseUrl == null) return null;
        try {
            Url urlImage = resoudreUrl(baseUrl, src);
            ReponseBinaire reponse = ClientHttp.recupererBinaire(urlImage);
            return ImageIO.read(new ByteArrayInputStream(reponse.corps));
        } catch (Exception e) {
            return null;
        }
    }

    private static Url resoudreUrl(Url base, String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) return new Url(href);
        if (href.startsWith("//")) return new Url(base.schema + ":" + href);
        if (href.startsWith("/")) return new Url(base.schema + "://" + base.hote + ":" + base.port + href);
        String chemin = base.chemin;
        int dernierSlash = chemin.lastIndexOf('/');
        String dossier = dernierSlash >= 0 ? chemin.substring(0, dernierSlash + 1) : "/";
        return new Url(base.schema + "://" + base.hote + ":" + base.port + dossier + href);
    }

    private static List<Element> enfantsBlocs(Element element) {
        List<Element> resultat = new ArrayList<>();
        for (Noeud enfant : element.enfants) {
            if (enfant instanceof Element e && BALISES_BLOC.contains(e.nomBalise)) resultat.add(e);
        }
        return resultat;
    }

    private static Font policeDepuisStyle(StyleCalcule style, boolean grasToken, boolean italiqueToken) {
        String taillePx = style.obtenir("font-size", "16px").replace("px", "").trim();
        int taille;
        try { taille = (int) Double.parseDouble(taillePx); } catch (NumberFormatException e) { taille = 16; }
        boolean gras = grasToken || style.obtenir("font-weight", "normal").equals("bold");
        boolean italique = italiqueToken || style.obtenir("font-style", "normal").equals("italic");
        int styleFonte = Font.PLAIN;
        if (gras) styleFonte |= Font.BOLD;
        if (italique) styleFonte |= Font.ITALIC;
        return new Font("SansSerif", styleFonte, taille);
    }
}