package fr.monnavigateur;

import fr.monnavigateur.html.Jeton;
import fr.monnavigateur.html.Tokenizer;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String html = "<html><body><p class=\"intro\">Bonjour <b>monde</b> !</p></body></html>";
        List<Jeton> jetons = Tokenizer.tokeniser(html);
        for (Jeton j : jetons) {
            System.out.println(j);
        }
    }
}