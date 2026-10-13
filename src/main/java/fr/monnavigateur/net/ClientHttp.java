package fr.monnavigateur.net;

import java.io.*;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class ClientHttp {

    public static ReponseHttp recuperer(Url url) throws IOException {
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

        return ReponseHttp.analyser(brut.toByteArray());
    }
}