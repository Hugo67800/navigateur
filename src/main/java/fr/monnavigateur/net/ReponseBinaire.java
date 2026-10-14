package fr.monnavigateur.net;

import java.util.Map;

public class ReponseBinaire {
    public final int statut;
    public final Map<String, String> entetes;
    public final byte[] corps;

    public ReponseBinaire(int statut, Map<String, String> entetes, byte[] corps) {
        this.statut = statut;
        this.entetes = entetes;
        this.corps = corps;
    }
}