package chess.ui;

import javax.swing.*;
import java.awt.*;

public class GlobalSettingsDialog extends JDialog {

    public GlobalSettingsDialog(JFrame parent) {

        super(
                parent,
                "Application Settings",
                true
        );

        SettingsPanel settings =
                new SettingsPanel(null);

        add(
                settings,
                BorderLayout.CENTER
        );

        JPanel buttons =
                new JPanel();

        JButton apply =
                new JButton("Apply");

        JButton cancel =
                new JButton("Cancel");

        buttons.add(apply);
        buttons.add(cancel);

        add(
                buttons,
                BorderLayout.SOUTH
        );


        apply.addActionListener(e -> {

            settings.applySettings();

            dispose();
        });


        cancel.addActionListener(e ->
                dispose()
        );


        pack();

        setLocationRelativeTo(parent);
    }
}