package fr.monnavigateur.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class BarreAdresse extends JPanel {

    private final JTextField champ;
    private final JButton boutonPrecedent;
    private final JButton boutonSuivant;

    public BarreAdresse() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        boutonPrecedent = new JButton("<");
        boutonSuivant = new JButton(">");
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        boutons.add(boutonPrecedent);
        boutons.add(boutonSuivant);

        champ = new JTextField();

        add(boutons, BorderLayout.WEST);
        add(champ, BorderLayout.CENTER);
    }

    public void afficherAdresse(String url) {
        champ.setText(url);
    }

    public void surValidationAdresse(Consumer<String> gestionnaire) {
        champ.addActionListener(e -> gestionnaire.accept(champ.getText().trim()));
    }

    public void surClicPrecedent(Runnable gestionnaire) {
        boutonPrecedent.addActionListener(e -> gestionnaire.run());
    }

    public void surClicSuivant(Runnable gestionnaire) {
        boutonSuivant.addActionListener(e -> gestionnaire.run());
    }

    public void activerBoutons(boolean precedent, boolean suivant) {
        boutonPrecedent.setEnabled(precedent);
        boutonSuivant.setEnabled(suivant);
    }
}