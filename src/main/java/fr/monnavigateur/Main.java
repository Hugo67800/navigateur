package fr.monnavigateur;

import fr.monnavigateur.net.Url;

public class Main {
    public static void main(String[] args) {
        String[] tests = {
            "https://exemple.com/page?x=1",
            "http://exemple.com:8080/a/b",
            "exemple.com",
            "https://exemple.com?q=test#haut"
        };
        for (String t : tests) {
            Url u = new Url(t);
            System.out.println(t + "  ->  " + u);
        }
    }
}