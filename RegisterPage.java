package smartcampus.ui;
import java.awt.*;
import javax.swing.*;
import smartcampus.dao.UserDAO;
import smartcampus.model.User;

public class RegisterPage extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleBox;
    private JButton registerButton, loginButton;

    public RegisterPage() {
        setTitle("User Registration");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with Beige Background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 220)); 
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels
        JLabel lblTitle = new JLabel("Register");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(102, 51, 0)); // Brownish Text

        JLabel lblName = new JLabel("Name:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        JLabel lblRole = new JLabel("Role:");

        // Input Fields
        nameField = new JTextField(15);
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        roleBox = new JComboBox<>(new String[]{"Faculty", "Student"});

        // Buttons
        registerButton = new JButton("Register");
        loginButton = new JButton("Back to Login");

        // Styling Buttons
        registerButton.setBackground(new Color(200, 0, 0)); // Red Button
        registerButton.setForeground(Color.black);

        loginButton.setBackground(new Color(52, 152, 219)); // Blue Button
        loginButton.setForeground(Color.black);

        // Adding Components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(lblName, gbc);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(lblPassword, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(lblConfirmPassword, gbc);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(lblRole, gbc);
        gbc.gridx = 1;
        mainPanel.add(roleBox, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(registerButton, gbc);

        gbc.gridy = 7;
        mainPanel.add(loginButton, gbc);

        // Button Actions
        registerButton.addActionListener(e -> registerUser());
        loginButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        add(mainPanel);
        setVisible(true);
    }

    private void registerUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "❌ Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(name, email, password, role);
        if (UserDAO.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this, "✅ Registration Successful!");
            dispose();
            new LoginPage();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Registration Failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
