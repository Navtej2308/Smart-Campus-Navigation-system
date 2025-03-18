package main.ui;

import main.dao.UserDAO;
import main.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginPage() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);

        registerButton = new JButton("Register");
        add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                User user = UserDAO.loginUser(email, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(null, "✅ Login Successful! Welcome " + user.getName());
                    dispose();
                    new Dashboard(user.getRole()); // Open Dashboard based on Role
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Login Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterPage();
        });

        setVisible(true);
    }
}
