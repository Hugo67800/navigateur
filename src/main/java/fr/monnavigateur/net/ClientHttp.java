package fr.monnavigateur.net;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.net.ssl.SSLSocketFactory;

public class ClientHttp {

    private static final int MAX_REDIRECTIONS = 5;

    public static ReponseHttp recuperer(Url url) throws IOException {
        return recuperer(url, 0);
    }

    private static ReponseHttp recuperer(Url url, int nbRedirections) throws IOException {
        if (nbRedirections > MAX_REDIRECTIONS) throw new IOException("Trop de redirections");
        byte[] brut = requeteBrute(url);
        ReponseHttp reponse = ReponseHttp.analyser(brut);
        if (estRedirection(reponse.statut) && reponse.entetes.containsKey("location")) {
            return recuperer(resoudre(url, reponse.entetes.get("location")), nbRedirections + 1);
        }
        return reponse;
    }

    public static ReponseBinaire recupererBinaire(Url url) throws IOException {
        return recupererBinaire(url, 0);
    }

    private static ReponseBinaire recupererBinaire(Url url, int nbRedirections) throws IOException {
        if (nbRedirections > MAX_REDIRECTIONS) throw new IOException("Trop de redirections");
        byte[] brut = requeteBrute(url);
        ReponseBinaire reponse = analyserBinaire(brut);
        if (estRedirection(reponse.statut) && reponse.entetes.containsKey("location")) {
            return recupererBinaire(resoudre(url, reponse.entetes.get("location")), nbRedirections + 1);
        }
        return reponse;
    }

    private static byte[] requeteBrute(Url url) throws IOException {
        Socket socket;
        if (url.schema.equals("https")) {
            socket = SSLSocketFactory.getDefault().createSocket(url.hote, url.port);
        } else {
            socket = new Socket(url.hote, url.port);
        }

        OutputStream out = socket.getOutputStream();
        String requete = "GET " + url.chemin + " HTTP/1.1\r\n"
                + "Host: " + url.hote + "\r\n"
                + "Connection: close\r\n"
                + "User-Agent: MonNavigateur/0.1\r\n"
                + "\r\n";
        out.write(requete.getBytes("UTF-8"));
        out.flush();

        InputStream in = socket.getInputStream();
        ByteArrayOutputStream brut = new ByteArrayOutputStream();
        byte[] tampon = new byte[4096];
        int n;
        while ((n = in.read(tampon)) != -1) {
            brut.write(tampon, 0, n);
        }
        socket.close();
        return brut.toByteArray();
    }

    private static ReponseBinaire analyserBinaire(byte[] brut) {
        int sep = indexOfSeparateur(brut);
        String entetesTexte;
        try {
            entetesTexte = new String(brut, 0, sep >= 0 ? sep : brut.length, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            entetesTexte = "";
        }
        byte[] corpsBrut = sep >= 0 ? Arrays.copyOfRange(brut, sep + 4, brut.length) : new byte[0];

        String[] lignes = entetesTexte.split("\r\n");
        int statut = 0;
        Map<String, String> entetes = new HashMap<>();
        if (lignes.length > 0) {
            String[] parties = lignes[0].split(" ");
            if (parties.length > 1) {
                try { statut = Integer.parseInt(parties[1]); } catch (NumberFormatException ignore) {}
            }
        }
        for (int i = 1; i < lignes.length; i++) {
            int dp = lignes[i].indexOf(':');
            if (dp > 0) {
                entetes.put(lignes[i].substring(0, dp).trim().toLowerCase(), lignes[i].substring(dp + 1).trim());
            }
        }

        byte[] corps = "chunked".equalsIgnoreCase(entetes.get("transfer-encoding"))
                ? dechunkerOctets(corpsBrut)
                : corpsBrut;

        return new ReponseBinaire(statut, entetes, corps);
    }

    private static int indexOfSeparateur(byte[] data) {
        for (int i = 0; i + 3 < data.length; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n' && data[i + 2] == '\r' && data[i + 3] == '\n') return i;
        }
        return -1;
    }

    private static byte[] dechunkerOctets(byte[] data) {
        ByteArrayOutputStream resultat = new ByteArrayOutputStream();
        int pos = 0;
        while (pos < data.length) {
            int finLigne = indexOfCRLF(data, pos);
            if (finLigne < 0) break;
            String ligneTaille = new String(data, pos, finLigne - pos, StandardCharsets.ISO_8859_1);
            int pv = ligneTaille.indexOf(';');
            if (pv >= 0) ligneTaille = ligneTaille.substring(0, pv);
            int taille;
            try { taille = Integer.parseInt(ligneTaille.trim(), 16); } catch (Exception e) { break; }
            if (taille == 0) break;
            int debut = finLigne + 2;
            int fin = Math.min(debut + taille, data.length);
            resultat.write(data, debut, fin - debut);
            pos = fin + 2;
        }
        return resultat.toByteArray();
    }

    private static int indexOfCRLF(byte[] data, int depart) {
        for (int i = depart; i + 1 < data.length; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n') return i;
        }
        return -1;
    }

    private static boolean estRedirection(int statut) {
        return statut == 301 || statut == 302 || statut == 303 || statut == 307 || statut == 308;
    }

    private static Url resoudre(Url base, String nouvelleAdresse) {
        if (nouvelleAdresse.startsWith("http://") || nouvelleAdresse.startsWith("https://")) {
            return new Url(nouvelleAdresse);
        }
        return new Url(base.schema + "://" + base.hote + ":" + base.port + nouvelleAdresse);
    }
}