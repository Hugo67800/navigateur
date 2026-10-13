package fr.monnavigateur.net;

import java.io.*;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class ClientHttp {

    private static final int MAX_REDIRECTIONS = 5;

    public static ReponseHttp recuperer(Url url) throws IOException {
        return recuperer(url, 0);
    }

    private static ReponseHttp recuperer(Url url, int nbRedirections) throws IOException {
        if (nbRedirections > MAX_REDIRECTIONS) {
            throw new IOException("Trop de redirections");
        }

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

        ReponseHttp reponse = ReponseHttp.analyser(brut.toByteArray());

        if (estRedirection(reponse.statut) && reponse.entetes.containsKey("location")) {
            String nouvelleAdresse = reponse.entetes.get("location");
            Url nouvelleUrl = resoudre(url, nouvelleAdresse);
            System.out.println("Redirection vers : " + nouvelleUrl);
            return recuperer(nouvelleUrl, nbRedirections + 1);
        }

        return reponse;
    }

    private static boolean estRedirection(int statut) {
        return statut == 301 || statut == 302 || statut == 303
                || statut == 307 || statut == 308;
    }

    private static Url resoudre(Url base, String nouvelleAdresse) {
        if (nouvelleAdresse.startsWith("http://") || nouvelleAdresse.startsWith("https://")) {
            return new Url(nouvelleAdresse);
        }
        // Adresse relative : on garde le schéma, hôte et port de la page d'origine
        String texte = base.schema + "://" + base.hote + ":" + base.port + nouvelleAdresse;
        return new Url(texte);
    }
}