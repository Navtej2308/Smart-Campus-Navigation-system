package smartcampus.ui;
import java.awt.*;
import javax.swing.*;
import smartcampus.dao.UserDAO;
import smartcampus.model.User;
import smartcampus.model.StudentProfile;
import smartcampus.dao.StudentDao;



public class LoginPage extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox rememberMe;
    private JButton btnLogin, btnRegister, btnGuest;

    public LoginPage() {
        setTitle("User Login");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 220)); // Beige Background
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblTitle = new JLabel("User Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(102, 51, 0)); // Brownish Text

        JLabel lblEmail = new JLabel("Email:");
        JLabel lblPassword = new JLabel("Password:");

        txtEmail = new JTextField(15);
        txtPassword = new JPasswordField(15);
        rememberMe = new JCheckBox("Remember Me");
        rememberMe.setBackground(new Color(245, 245, 220));

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");
        btnGuest = new JButton("Guest Mode (Campus Tour)");

        btnLogin.setBackground(new Color(200, 0, 0));  // Red Button
        btnLogin.setForeground(Color.black);

        btnRegister.setBackground(new Color(52, 152, 219)); // Blue Button
        btnRegister.setForeground(Color.black);

        btnGuest.setBackground(new Color(46, 204, 113)); // Green Button
        btnGuest.setForeground(Color.black);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        mainPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(lblPassword, gbc);
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(rememberMe, gbc);

        gbc.gridy = 4;
        mainPanel.add(btnLogin, gbc);

        gbc.gridy = 5;
        mainPanel.add(btnRegister, gbc);
        
        gbc.gridy = 6;
        mainPanel.add(btnGuest, gbc);

        btnLogin.addActionListener(e -> loginAction());
        btnRegister.addActionListener(e -> {
            dispose();
            new RegisterPage();
        });

        btnGuest.addActionListener(e -> {
            dispose();
            new CampusTour(); 
            // Redirect to Campus Tour Page
        });

        add(mainPanel);
        setVisible(true);
    }

    private void loginAction() {
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());

        

        User user = UserDAO.loginUser(email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "✅ Login Successful! Welcome " + user.getName());
            dispose();

            if ("faculty".equalsIgnoreCase(user.getRole())) {
                new FacultyDashboard(email);
                
            } else if ("student".equalsIgnoreCase(user.getRole())) {
                int userId = StudentDao.getUserIdByEmail(email);
                new Dashboard(user.getRole(), userId);
                
            } else {
                JOptionPane.showMessageDialog(this, "❌ Unknown Role! Contact Admin.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "❌ Login Failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String loggedInFacultyEmail;

    // Example: Set this when login is successful
  

    public static void main(String[] args) {
        new LoginPage();
    }
}
