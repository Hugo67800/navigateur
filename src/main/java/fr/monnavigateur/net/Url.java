package fr.monnavigateur.net;

public class Url {
    public final String schema;
    public final String hote;
    public final int port;
    public final String chemin;

    public Url(String texte) {
        String reste = texte.trim();

        // 1. Schéma (http ou https), par défaut https
        String s;
        int sep = reste.indexOf("://");
        if (sep >= 0) {
            s = reste.substring(0, sep).toLowerCase();
            reste = reste.substring(sep + 3);
        } else {
            s = "https";
        }
        if (!s.equals("http") && !s.equals("https")) {
            throw new IllegalArgumentException("Schema non supporte : " + s);
        }

        // 2. On ignore le fragment (#...), il ne part pas au serveur
        int diese = reste.indexOf('#');
        if (diese >= 0) {
            reste = reste.substring(0, diese);
        }

        // 3. Séparer "hôte:port" du chemin
        int fin = -1;
        for (int i = 0; i < reste.length(); i++) {
            char c = reste.charAt(i);
            if (c == '/' || c == '?') {
                fin = i;
                break;
            }
        }
        String autorite;
        String c;
        if (fin < 0) {
            autorite = reste;
            c = "/";
        } else {
            autorite = reste.substring(0, fin);
            c = reste.substring(fin);
            if (c.startsWith("?")) {
                c = "/" + c;
            }
        }

        // 4. Hôte et port
        String h;
        int p = s.equals("https") ? 443 : 80;
        int deuxPoints = autorite.lastIndexOf(':');
        if (deuxPoints >= 0) {
            h = autorite.substring(0, deuxPoints);
            p = Integer.parseInt(autorite.substring(deuxPoints + 1));
        } else {
            h = autorite;
        }
        if (h.isEmpty()) {
            throw new IllegalArgumentException("Hote manquant");
        }

        this.schema = s;
        this.hote = h;
        this.port = p;
        this.chemin = c;
    }

    @Override
    public String toString() {
        return schema + "://" + hote + ":" + port + chemin;
    }
}