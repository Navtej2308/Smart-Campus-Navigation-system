package smartcampus.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import smartcampus.dao.StudentDao;
import smartcampus.model.StudentProfile;
import java.io.File;
import smartcampus.model.Notification;
import smartcampus.dao.NotificationDao;
import java.util.List;
import smartcampus.Database;
import smartcampus.dao.FacultyDao;
import smartcampus.model.Event;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import smartcampus.ui.CampusTour;
import smartcampus.model.Document;
import smartcampus.dao.DocumentDao;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swing.BrowserView;


public class Dashboard extends JFrame {

    private JPanel contentArea;
    private String currentStudentPRN;
    private int currentUserId;


    public Dashboard(String role,int userId) {
        setTitle(role + " Dashboard");
        setSize(1200, 800);  // Increased size for better map viewing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.currentUserId = userId;
        
        // Top Gradient Title Bar
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(58, 123, 213), getWidth(), 0, new Color(58, 213, 171));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(getWidth(), 70));
        JLabel welcomeLabel = new JLabel("Welcome, " + role + "!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(welcomeLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setLayout(new GridLayout(6, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        // In the Dashboard constructor, modify the navOptions array:
            String[] navOptions = {
                  // New map option
                "🏠 Home",
                "Updates",
                "📅 All Events",
                "📄 Documents",
                "👤 Profile",
                "🚪 Logout"
            };

        for (String nav : navOptions) {
            JButton btn = new JButton(nav);
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener((ActionEvent e) -> {
                if (nav.contains("Logout")) {
                    dispose();
                    new LoginPage();
                } else {
                    updateContent(nav);
                }
            });

            sidebar.add(btn);
        }

        // Main Content Panel
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

        updateContent("🏠 Home"); // default view
        setVisible(true);
    }

    private void updateContent(String section) {
        contentArea.removeAll();
    
        if (section.contains("👤 Profile")) {
            StudentProfile profile = StudentDao.getStudentProfileByUserId(currentUserId);
    
            if (profile == null || profile.getStudentPrn() == null || profile.getStudentPrn().isEmpty()) {
                createProfileForm(currentUserId); // Show form to create profile
            } else {
                displayStudentProfile(profile); // Show profile details
            }
    
        } 
        else if (section.contains("📄 Documents")) {
            StudentProfile profile = StudentDao.getStudentProfileByUserId(currentUserId);
            if (profile != null) {
                contentArea.add(createDocumentListPanel(profile.getDegree(), profile.getPanel()), BorderLayout.CENTER);
            }
        }
        
        else if (section.contains("Updates")) {
            
    
            JLabel titleLabel = new JLabel("Recent Notifications");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
            JPanel notificationPanel = new JPanel();
            notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
            notificationPanel.setBackground(Color.WHITE);

            StudentProfile profile = StudentDao.getStudentProfileByUserId(currentUserId);

            if (profile != null) {
                updateNotificationList(notificationPanel, profile.getDegree(), profile.getPanel());
            }
        
            JScrollPane scrollPane = new JScrollPane(notificationPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
            JButton refreshBtn = new JButton("🔄 Refresh");
            refreshBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            refreshBtn.setFocusPainted(false);
            refreshBtn.setBackground(new Color(240, 240, 240));
            refreshBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
            refreshBtn.addActionListener(e -> {
                StudentProfile p = StudentDao.getStudentProfileByUserId(currentUserId);
                if (p != null) {
                    updateNotificationList(notificationPanel, p.getDegree(), p.getPanel());
                }
            });
        
            // First load
            updateNotificationList(notificationPanel, profile.getDegree(), profile.getPanel());

        
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            topPanel.add(titleLabel, BorderLayout.CENTER);
            topPanel.add(refreshBtn, BorderLayout.EAST);
        
            contentArea.add(topPanel, BorderLayout.NORTH);
            contentArea.add(scrollPane, BorderLayout.CENTER);
        }
        else if (section.contains("📅 All Events")) {
            contentArea.removeAll();

            JLabel titleLabel = new JLabel("📅 All Events");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

            JPanel allEventsPanel = createShowAllEventsPanel(); // your UI-enhanced method

            contentArea.add(titleLabel, BorderLayout.NORTH);
            contentArea.add(allEventsPanel, BorderLayout.CENTER);

            contentArea.revalidate();
            contentArea.repaint();
        }
        else if (section.contains("🏠 Home")) {
            // Create main panel with BorderLayout
            JPanel homePanel = new JPanel(new BorderLayout());
            
            // Create a tabbed pane for the home section
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Tab 1: Campus Map
            JPanel mapPanel = new JPanel(new BorderLayout());
            loadCampusMap(mapPanel);  // We'll create this method next
            tabbedPane.addTab("🗺️ Campus Map", mapPanel);
            
            // Tab 2: Quick Actions
            JPanel quickActionsPanel = createQuickActionsPanel();
            tabbedPane.addTab("⚡ Quick Access", quickActionsPanel);
            
            // Tab 3: Recent Updates
            JPanel updatesPanel = new JPanel(new BorderLayout());
            StudentProfile profile = StudentDao.getStudentProfileByUserId(currentUserId);
            if (profile != null) {
                updateNotificationList(updatesPanel, profile.getDegree(), profile.getPanel());
            }
            tabbedPane.addTab("🔔 Recent Updates", updatesPanel);
            
            homePanel.add(tabbedPane, BorderLayout.CENTER);
            contentArea.add(homePanel, BorderLayout.CENTER);
        }

        else {
            JLabel titleLabel = new JLabel(section);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    
            JTextArea area = new JTextArea("Content for " + section + " will be displayed here.");
            area.setFont(new Font("SansSerif", Font.PLAIN, 16));
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setBackground(new Color(245, 245, 245));
            area.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
            area.setMargin(new Insets(15, 15, 15, 15));
    
            contentArea.add(titleLabel, BorderLayout.NORTH);
            contentArea.add(area, BorderLayout.CENTER);
        }
    
        contentArea.revalidate();
        contentArea.repaint();
    }


    private void loadCampusMap(JPanel container) {
        try {
            // Create and configure the CampusTour frame
            CampusTour campusTour = new CampusTour();
            campusTour.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // Get the BrowserView from CampusTour
            Component[] components = campusTour.getContentPane().getComponents();
            BrowserView browserView = null;
            
            for (Component comp : components) {
                if (comp instanceof BrowserView) {
                    browserView = (BrowserView) comp;
                    break;
                }
            }
            
            if (browserView != null) {
                // Remove the back button panel
                campusTour.remove(browserView.getParent());
                
                // Add the browser view to our container
                container.add(browserView, BorderLayout.CENTER);
                
                // Add a refresh button at the bottom
                JButton refreshBtn = new JButton("🔄 Refresh Map");
                refreshBtn.addActionListener(e -> {
                    container.removeAll();
                    loadCampusMap(container);
                    container.revalidate();
                    container.repaint();
                });
                
                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                bottomPanel.add(refreshBtn);
                container.add(bottomPanel, BorderLayout.SOUTH);
            } else {
                throw new Exception("Browser view not found");
            }
            
            // Hide the original CampusTour frame
            campusTour.setVisible(false);
            
        } catch (Exception e) {
            e.printStackTrace();
            container.removeAll();
            container.setLayout(new BorderLayout());
            
            JLabel errorLabel = new JLabel(
                "<html><center>Error loading campus map<br>" + 
                e.getMessage() + "</center></html>", 
                SwingConstants.CENTER
            );
            errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            
            JButton retryBtn = new JButton("Retry");
            retryBtn.addActionListener(evt -> {
                container.removeAll();
                loadCampusMap(container);
                container.revalidate();
                container.repaint();
            });
            
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(errorLabel, BorderLayout.CENTER);
            errorPanel.add(retryBtn, BorderLayout.SOUTH);
            
            container.add(errorPanel, BorderLayout.CENTER);
        }
    }



    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] quickActions = {
            "📅 Today's Events", "📄 My Documents", "👤 My Profile",
            "🏛️ Buildings", "📝 Submit Request", "🛠️ Settings"
        };
        
        for (String action : quickActions) {
            JButton btn = new JButton(action);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(240, 240, 255));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255)),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
            ));
            
            btn.addActionListener(e -> {
                if (action.contains("Events")) {
                    updateContent("📅 All Events");
                } else if (action.contains("Documents")) {
                    updateContent("📄 Documents");
                } else if (action.contains("Profile")) {
                    updateContent("👤 Profile");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        action + " feature coming soon!", 
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
            panel.add(btn);
        }
        
        return panel;
    }
    
//notification
private void updateNotificationList(JPanel panel, String degree, String panelValue) {
    System.out.println("updateNotificationList called with degree: " + degree + ", panel: " + panelValue);

    panel.removeAll();

    NotificationDao notificationDao = new NotificationDao(Database.getConnection());

    List<Notification> notifications = notificationDao.getNotificationsForStudent(degree, panelValue);
    System.out.println("Notifications fetched: " + (notifications != null ? notifications.size() : "null"));

    if (notifications == null || notifications.isEmpty()) {
        System.out.println("No notifications available for this student.");

        JLabel emptyLabel = new JLabel("No notifications available.");
        emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emptyLabel);
    } else {
        for (Notification notification : notifications) {
            System.out.println("Displaying Notification: " + notification.getTitle() + " | " + notification.getMessage());

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            card.setBackground(new Color(250, 250, 250));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            //notification body

            JLabel messageLabel = new JLabel("<html><b>" + notification.getMessage() + "</b></html>");
            messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

            JLabel createdByLabel = new JLabel("By: " + notification.getCreatedBy());
            createdByLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
            createdByLabel.setForeground(Color.DARK_GRAY);

            JLabel timeLabel = new JLabel("At: " + notification.getCreatedAt());
            timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            timeLabel.setForeground(Color.GRAY);

            card.add(messageLabel);
            card.add(Box.createVerticalStrut(5));
            card.add(createdByLabel);
            card.add(timeLabel);

            panel.add(Box.createVerticalStrut(10));
            panel.add(card);
        }
    }

    panel.revalidate();
    panel.repaint();
    System.out.println("Notification panel updated successfully.\n");
}

    

public static void main(String[] args) {
        new Dashboard("Student", 1);

    }

    //student profile
   //student profile
private void createProfileForm(int userId) {
    contentArea.removeAll();
    contentArea.setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Complete Your Profile", SwingConstants.CENTER);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;

    JLabel[] labels = {
        new JLabel("Student PRN:"),
        new JLabel("Roll No:"),
        new JLabel("Degree:"),
        new JLabel("Panel:")
    };

    JTextField[] fields = {
        new JTextField(20),
        new JTextField(20),
        new JTextField(20),
        new JTextField(20)
    };

    for (int i = 0; i < labels.length; i++) {
        labels[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = i;
        formPanel.add(labels[i], gbc);

        gbc.gridx = 1;
        fields[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
        fields[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(fields[i], gbc);
    }

    // Image chooser
    JButton choosePhotoBtn = new JButton("Choose Photo");
    JLabel photoLabel = new JLabel("No Image Selected");
    photoLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));

    gbc.gridx = 0;
    gbc.gridy++;
    formPanel.add(choosePhotoBtn, gbc);

    gbc.gridx = 1;
    formPanel.add(photoLabel, gbc);

    // Save button
    JButton saveBtn = new JButton("Save Profile");
    gbc.gridx = 1;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    formPanel.add(saveBtn, gbc);

    // Image logic
    final File[] selectedFile = {null};
    choosePhotoBtn.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile[0] = fileChooser.getSelectedFile();
            photoLabel.setText(selectedFile[0].getName());
        }
    });

    // Save logic
    saveBtn.addActionListener(e -> {
        // ✅ Replace with actual email from login session or passed variable
       
    
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "User not found in system.");
            return;
        }
    
        String studentPrn = fields[0].getText();
        String roll = fields[1].getText();
        String degree = fields[2].getText();
        String panel = fields[3].getText();
        String photoPath = selectedFile[0] != null ? selectedFile[0].getAbsolutePath() : null;
    
        StudentProfile newProfile = new StudentProfile();
        newProfile.setUserId(userId); // ✅ Now this userId is correct
        newProfile.setStudentPrn(studentPrn);
        newProfile.setRollNo(roll);
        newProfile.setDegree(degree);
        newProfile.setPanel(panel);
        newProfile.setPhotoPath(photoPath);
    
        boolean isSaved = StudentDao.insertStudentProfile(newProfile);
        if (isSaved) {
            JOptionPane.showMessageDialog(this, "Profile saved successfully.");
            displayStudentProfile(newProfile);
        } else {
            JOptionPane.showMessageDialog(this, "Error saving profile.");
        }
    });
    
    contentArea.add(titleLabel, BorderLayout.NORTH);
    contentArea.add(formPanel, BorderLayout.CENTER);
    contentArea.revalidate();
    contentArea.repaint();
}
private void displayStudentProfile(StudentProfile profile) {
    contentArea.removeAll();
    contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
    contentArea.setBackground(Color.WHITE);

    JLabel titleLabel = new JLabel("Your Profile", SwingConstants.CENTER);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    contentArea.add(titleLabel);

    String[] labels = {
        "Student PRN: " + profile.getStudentPrn(),
        "Roll No: " + profile.getRollNo(),
        "Degree: " + profile.getDegree(),
        "Panel: " + profile.getPanel()
    };

    for (String labelText : labels) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        contentArea.add(label);
    }

    if (profile.getPhotoPath() != null && !profile.getPhotoPath().isEmpty()) {
        ImageIcon icon = new ImageIcon(profile.getPhotoPath());
        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel photo = new JLabel(new ImageIcon(img));
        photo.setAlignmentX(Component.CENTER_ALIGNMENT);
        photo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        contentArea.add(photo);
    }

     // ⬇️ ADD THE BUTTON PROPERLY
     JButton updateBtn = new JButton("Edit Profile");
     updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
     updateBtn.setFocusPainted(false);
     updateBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
     updateBtn.setBackground(Color.WHITE);
     updateBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
     updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
     updateBtn.addActionListener(e -> updateProfileForm(profile));
 
     // Add some vertical spacing
     contentArea.add(Box.createRigidArea(new Dimension(0, 20)));
     contentArea.add(updateBtn);
 
     contentArea.revalidate();
     contentArea.repaint();


}

//update student profile

private void updateProfileForm(StudentProfile profile) {
    contentArea.removeAll();
    contentArea.setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Update Your Profile", SwingConstants.CENTER);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;

    JLabel[] labels = {
        new JLabel("Student PRN:"),
        new JLabel("Roll No:"),
        new JLabel("Degree:"),
        new JLabel("Panel:")
    };

    JTextField[] fields = {
        new JTextField(profile.getStudentPrn(), 20),
        new JTextField(profile.getRollNo(), 20),
        new JTextField(profile.getDegree(), 20),
        new JTextField(profile.getPanel(), 20)
    };

    for (int i = 0; i < labels.length; i++) {
        labels[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = i;
        formPanel.add(labels[i], gbc);

        gbc.gridx = 1;
        fields[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
        fields[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(fields[i], gbc);
    }

    // Image chooser
    JButton choosePhotoBtn = new JButton("Change Photo");
    JLabel photoLabel = new JLabel(profile.getPhotoPath() != null ? profile.getPhotoPath() : "No Image Selected");
    photoLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));

    gbc.gridx = 0;
    gbc.gridy++;
    formPanel.add(choosePhotoBtn, gbc);

    gbc.gridx = 1;
    formPanel.add(photoLabel, gbc);

    final File[] selectedFile = {null};
    choosePhotoBtn.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile[0] = fileChooser.getSelectedFile();
            photoLabel.setText(selectedFile[0].getName());
        }
    });

    // Update button
    JButton updateBtn = new JButton("Update Profile");
    gbc.gridx = 1;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    formPanel.add(updateBtn, gbc);

    updateBtn.addActionListener(e -> {
        profile.setStudentPrn(fields[0].getText());
        profile.setRollNo(fields[1].getText());
        profile.setDegree(fields[2].getText());
        profile.setPanel(fields[3].getText());
        if (selectedFile[0] != null) {
            profile.setPhotoPath(selectedFile[0].getAbsolutePath());
        }

        boolean isUpdated = StudentDao.updateStudentProfile(profile);
        if (isUpdated) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");
            displayStudentProfile(profile);
        } else {
            JOptionPane.showMessageDialog(this, "Error updating profile.");
        }
    });

    contentArea.add(titleLabel, BorderLayout.NORTH);
    contentArea.add(formPanel, BorderLayout.CENTER);
    contentArea.revalidate();
    contentArea.repaint();
}


private JPanel createShowAllEventsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(250, 250, 255)); // Light lavender background

    // 🔹 Title
    JLabel titleLabel = new JLabel("📅 All Events");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
    panel.add(titleLabel, BorderLayout.NORTH);

    // 🔹 Filter Panel
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    filterPanel.setBackground(new Color(240, 240, 255));
    filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    JLabel degreeLabel = new JLabel("Degree:");
    degreeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
    JComboBox<String> degreeFilter = new JComboBox<>(new String[]{"All", "BTech", "MTech", "MBA"});
    degreeFilter.setFont(new Font("SansSerif", Font.PLAIN, 14));

    JLabel panelLabel = new JLabel("Panel:");
    panelLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
    JComboBox<String> panelFilter = new JComboBox<>(new String[]{"All", "A", "B", "C", "D"});
    panelFilter.setFont(new Font("SansSerif", Font.PLAIN, 14));

    JButton filterBtn = new JButton("Apply Filter");
    filterBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
    filterBtn.setFocusPainted(false);
    filterBtn.setBackground(new Color(100, 149, 237));
    filterBtn.setForeground(Color.WHITE);

    filterPanel.add(degreeLabel);
    filterPanel.add(degreeFilter);
    filterPanel.add(Box.createHorizontalStrut(20));
    filterPanel.add(panelLabel);
    filterPanel.add(panelFilter);
    filterPanel.add(Box.createHorizontalStrut(20));
    filterPanel.add(filterBtn);

    panel.add(filterPanel, BorderLayout.PAGE_START);

    // 🔹 Event Table Setup
    String[] columns = {"ID", "Name", "Class No", "Building", "Date", "Time", "Description", "Degree", "Panel"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);
    table.setRowHeight(28);
    table.setFont(new Font("SansSerif", Font.PLAIN, 14));
    table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
    table.setGridColor(Color.LIGHT_GRAY);
    table.setShowGrid(true);
    table.setFillsViewportHeight(true);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    panel.add(scrollPane, BorderLayout.CENTER);

    // 🔹 Load all events initially
    FacultyDao.getAllEvents().forEach(event -> {
        model.addRow(new Object[]{
            event.getId(), event.getName(), event.getClassNo(), event.getBuilding(),
            event.getDate(), event.getTime(), event.getDescription(),
            event.getDegree(), event.getPanel()
        });
    });

    // 🔹 Filter Logic
    filterBtn.addActionListener(e -> {
        String selectedDegree = degreeFilter.getSelectedItem().toString();
        String selectedPanel = panelFilter.getSelectedItem().toString();

        model.setRowCount(0); // Clear table

        FacultyDao.getAllEvents().forEach(event -> {
            boolean matches = true;
            if (!selectedDegree.equals("All") && !event.getDegree().equalsIgnoreCase(selectedDegree)) matches = false;
            if (!selectedPanel.equals("All") && !event.getPanel().equalsIgnoreCase(selectedPanel)) matches = false;

            if (matches) {
                model.addRow(new Object[]{
                    event.getId(), event.getName(), event.getClassNo(), event.getBuilding(),
                    event.getDate(), event.getTime(), event.getDescription(),
                    event.getDegree(), event.getPanel()
                });
            }
        });
    });

    return panel;
}




private JPanel createDocumentListPanel(String degree, String panel) {
    JPanel panelMain = new JPanel();
    panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
    panelMain.setBackground(new Color(250, 250, 255)); // Light lavender background

    // 🔹 Title
    JLabel titleLabel = new JLabel("📄 Available Documents");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
    panelMain.add(titleLabel);

    List<Document> documents = DocumentDao.getDocumentsForStudent(degree, panel);

    if (documents.isEmpty()) {
        JLabel label = new JLabel("No documents available.");
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelMain.add(label);
    } else {
        for (Document doc : documents) {
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout(10, 10));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15),
                BorderFactory.createLineBorder(new Color(200, 200, 255), 1)
            ));
            card.setBackground(Color.WHITE);

            // 🔹 Title
            JLabel title = new JLabel(doc.getTitle());
            title.setFont(new Font("SansSerif", Font.BOLD, 16));
            title.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            // 🔹 Description
            JTextArea desc = new JTextArea(doc.getDescription());
            desc.setFont(new Font("SansSerif", Font.PLAIN, 14));
            desc.setEditable(false);
            desc.setWrapStyleWord(true);
            desc.setLineWrap(true);
            desc.setBackground(Color.WHITE);
            desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            // 🔹 Download Button
            JButton downloadBtn = new JButton("⬇ Download");
            downloadBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
            downloadBtn.setFocusPainted(false);
            downloadBtn.setBackground(new Color(100, 149, 237));
            downloadBtn.setForeground(Color.WHITE);
            downloadBtn.setPreferredSize(new Dimension(120, 35));

            downloadBtn.addActionListener(e -> {
                try {
                    Desktop.getDesktop().open(new File(doc.getFilename()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panelMain, "Unable to open file.");
                }
            });

            // 🔹 Content Panel
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout(10, 10));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.add(title, BorderLayout.NORTH);
            contentPanel.add(desc, BorderLayout.CENTER);

            card.add(contentPanel, BorderLayout.CENTER);
            card.add(downloadBtn, BorderLayout.EAST);

            panelMain.add(card);
            panelMain.add(Box.createVerticalStrut(10));
        }
    }

    // Optional scroll support for large document lists
    JScrollPane scrollPane = new JScrollPane(panelMain);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    JPanel container = new JPanel(new BorderLayout());
    container.add(scrollPane, BorderLayout.CENTER);
    return container;
}



}
