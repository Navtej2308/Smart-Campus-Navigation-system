package main.ui;

import main.dao.UserDAO;
import main.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPage extends JFrame {
    
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton registerButton;

    public RegisterPage() {
        setTitle("Register");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"faculty", "student"});
        add(roleBox);

        registerButton = new JButton("Register");
        add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String role = (String) roleBox.getSelectedItem();

                User newUser = new User(name, email, password, role);
                if (UserDAO.registerUser(newUser)) {
                    JOptionPane.showMessageDialog(null, "✅ Registration Successful!");
                    dispose();
                    new LoginPage();
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Registration Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}
