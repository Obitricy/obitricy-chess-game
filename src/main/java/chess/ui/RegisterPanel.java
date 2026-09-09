package chess.ui;

import javax.swing.*;
import java.awt.*;
import chess.auth.LoginManager;

public class RegisterPanel extends JPanel {

    public RegisterPanel(MainFrame frame) {

        setLayout(new GridBagLayout());

        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);

        JButton create = new JButton("Create Account");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);

        gbc.gridx=0;
        gbc.gridy=0;
        add(new JLabel("Username"), gbc);

        gbc.gridx=1;
        add(username, gbc);

        gbc.gridx=0;
        gbc.gridy++;
        add(new JLabel("Password"), gbc);

        gbc.gridx=1;
        add(password, gbc);

        gbc.gridy++;
        gbc.gridx=0;
        gbc.gridwidth=2;

        add(create, gbc);

        create.addActionListener(e->{

            LoginManager.register(
                    username.getText(),
                    new String(password.getPassword()));

            JOptionPane.showMessageDialog(this,
                    "Account created.");

            frame.showLogin();

        });
    }
}