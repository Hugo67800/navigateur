package fr.monnavigateur.net;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ReponseHttp {
    public final int statut;
    public final Map<String, String> entetes;
    public final String corps;

    private ReponseHttp(int statut, Map<String, String> entetes, String corps) {
        this.statut = statut;
        this.entetes = entetes;
        this.corps = corps;
    }

    public static ReponseHttp analyser(byte[] brut) throws UnsupportedEncodingException {
        String texte = new String(brut, "ISO-8859-1");
        int sepEntetesCorps = texte.indexOf("\r\n\r\n");
        String partieEntetes = sepEntetesCorps >= 0 ? texte.substring(0, sepEntetesCorps) : texte;
        String corpsBrut = sepEntetesCorps >= 0 ? texte.substring(sepEntetesCorps + 4) : "";

        String[] lignes = partieEntetes.split("\r\n");
        String ligneStatut = lignes[0];
        int statut = Integer.parseInt(ligneStatut.split(" ")[1]);

        Map<String, String> entetes = new HashMap<>();
        for (int i = 1; i < lignes.length; i++) {
            int deuxPoints = lignes[i].indexOf(':');
            if (deuxPoints > 0) {
                String cle = lignes[i].substring(0, deuxPoints).trim().toLowerCase();
                String valeur = lignes[i].substring(deuxPoints + 1).trim();
                entetes.put(cle, valeur);
            }
        }

        String corps;
        if ("chunked".equalsIgnoreCase(entetes.get("transfer-encoding"))) {
            corps = decoderChunked(corpsBrut);
        } else {
            corps = corpsBrut;
        }

        return new ReponseHttp(statut, entetes, corps);
    }

    private static String decoderChunked(String corpsBrut) {
        StringBuilder resultat = new StringBuilder();
        int pos = 0;
        while (pos < corpsBrut.length()) {
            int finLigne = corpsBrut.indexOf("\r\n", pos);
            if (finLigne < 0) break;

            String ligneTaille = corpsBrut.substring(pos, finLigne);
            int tailleHex = ligneTaille.indexOf(';');
            if (tailleHex >= 0) {
                ligneTaille = ligneTaille.substring(0, tailleHex);
            }
            int taille;
            try {
                taille = Integer.parseInt(ligneTaille.trim(), 16);
            } catch (NumberFormatException e) {
                break;
            }
            if (taille == 0) break;

            int debutMorceau = finLigne + 2;
            int finMorceau = debutMorceau + taille;
            if (finMorceau > corpsBrut.length()) {
                finMorceau = corpsBrut.length();
            }
            resultat.append(corpsBrut, debutMorceau, finMorceau);

            pos = finMorceau + 2; // on saute le \r\n qui suit le morceau
        }
        return resultat.toString();
    }
}