package smartcampus.ui;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import smartcampus.dao.FacultyDao;
import smartcampus.model.Event;
import smartcampus.dao.ClassroomDao;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.*;
import java.sql.*; // for Connection, PreparedStatement, ResultSet, etc.
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import smartcampus.model.Document;
import smartcampus.dao.DocumentDao;





public class FacultyDashboard extends JFrame {

    
    

    

    private JTextField eventNameField, classNoField, buildingField, dateField, timeField, prnField;
    private JTextArea descriptionArea;
    private JComboBox<String> toWhomDropdown, degreeDropdown, panelDropdown;
    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JPanel mainContentPanel;
    private JPanel dynamicPanel;
    public String loggedInFacultyEmail; // Set this based on login
    
    

    public FacultyDashboard(String facultyEmail) {
        FacultyDao facultyDao = new FacultyDao();
        facultyDao.deleteExpiredEvents();

        this.loggedInFacultyEmail = facultyEmail;
        setTitle("Faculty Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 220));
        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        navPanel.setBackground(new Color(230, 230, 200));
        JLabel title = new JLabel("Faculty Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        navPanel.add(title);
        add(navPanel, BorderLayout.NORTH);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(230, 230, 250));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] sidebarLabels = {
                "Add Event", "Show All Events", "Update Event", "Upload Document",
                "Check ClassRoom Availability", "Logout"
        };

        for (String label : sidebarLabels) {
            JButton button = new JButton(label);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(button);

            button.addActionListener(e -> handleSidebarAction(label));
        }

        add(sidebar, BorderLayout.WEST);

        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(245, 245, 220));
        add(mainContentPanel, BorderLayout.CENTER);

        loadEvents();
        setVisible(true);
    }

    private void handleSidebarAction(String action) {
        mainContentPanel.removeAll();

        switch (action) {
            case "Add Event":
                mainContentPanel.add(createAddEventForm(), BorderLayout.NORTH);
                break;
            case "Show All Events":
                mainContentPanel.add(createShowAllEventsPanel(), BorderLayout.CENTER);
                break;
            case "Update Event":
                mainContentPanel.add(createManageEventsPanel(), BorderLayout.CENTER);
                break;
            case "Upload Document":
                mainContentPanel.removeAll();
                mainContentPanel.add(createUploadDocumentForm(), BorderLayout.CENTER);
                mainContentPanel.revalidate();
                mainContentPanel.repaint();
                break;
            
            
            case "Check ClassRoom Availability":
                 mainContentPanel.add(createRoomAvailabilityPanel(), BorderLayout.CENTER);
                 break;

            case "Logout":
                dispose();
                new LoginPage();
                break;
            default:
                JOptionPane.showMessageDialog(this, action + " clicked");
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }


//add event 


    private JPanel createAddEventForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Event Name:", "Class No:", "Building:", "Date (YYYY-MM-DD):", "Time (HH:MM (24-hour)):", "Description:"};
        JTextField[] fields = {
                eventNameField = new JTextField(),
                classNoField = new JTextField(),
                buildingField = new JTextField(),
                dateField = new JTextField(),
                timeField = new JTextField()
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            if (i == 5) {
                descriptionArea = new JTextArea(3, 20);
                formPanel.add(new JScrollPane(descriptionArea), gbc);
            } else {
                formPanel.add(fields[i], gbc);
            }
        }

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("To Whom:"), gbc);

        gbc.gridx = 1;
        String[] options = {"Degree", "PRN"};
        toWhomDropdown = new JComboBox<>(options);
        formPanel.add(toWhomDropdown, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        dynamicPanel = new JPanel(new GridBagLayout());
        dynamicPanel.setBackground(new Color(255, 255, 255));
        updateDynamicInputs();
        formPanel.add(dynamicPanel, gbc);
        gbc.gridwidth = 1;

        toWhomDropdown.addActionListener(e -> updateDynamicInputs());

        JButton submitBtn = new JButton("Submit Event");
        submitBtn.setBackground(new Color(210, 180, 140));
        submitBtn.addActionListener(e -> addEvent());

        gbc.gridy++;
        gbc.gridx = 1;
        formPanel.add(submitBtn, gbc);

        return formPanel;
    }

    private void updateDynamicInputs() {
        dynamicPanel.removeAll();
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets = new Insets(4, 4, 4, 4);
        dgbc.fill = GridBagConstraints.HORIZONTAL;
        dgbc.gridx = 0;
        dgbc.gridy = 0;

        if (toWhomDropdown.getSelectedItem().toString().equals("Degree")) {
            dynamicPanel.add(new JLabel("Degree:"), dgbc);
            dgbc.gridx = 1;
            degreeDropdown = new JComboBox<>(new String[]{"BTech", "MTech", "MBA"});
            dynamicPanel.add(degreeDropdown, dgbc);

            dgbc.gridy++;
            dgbc.gridx = 0;
            dynamicPanel.add(new JLabel("Panel:"), dgbc);
            dgbc.gridx = 1;
            panelDropdown = new JComboBox<>(new String[]{"A", "B", "C", "D"});
            dynamicPanel.add(panelDropdown, dgbc);
        } else {
            dgbc.gridx = 0;
            dynamicPanel.add(new JLabel("PRN:"), dgbc);
            dgbc.gridx = 1;
            prnField = new JTextField(15);
            dynamicPanel.add(prnField, dgbc);
        }

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    private void addEvent() {


        
        String name = eventNameField.getText();
        String classNo = classNoField.getText();
        String building = buildingField.getText();
        String description = descriptionArea.getText();
        String date = dateField.getText();
        String time = timeField.getText();
        String toWhom = toWhomDropdown.getSelectedItem().toString();
        String degree = "";
        String panel = "";
        String prn = "";

        if (toWhom.equals("Degree")) {
            degree = degreeDropdown.getSelectedItem().toString();
            panel = panelDropdown.getSelectedItem().toString();
        } else {
            prn = prnField.getText();
        }

        Event event = new Event(name, classNo, building, description, date, time, toWhom, degree, panel, prn, loggedInFacultyEmail);
        boolean success = FacultyDao.addEvent(event);

        if (success) {
            JOptionPane.showMessageDialog(this, "Event added successfully!");
            loadEvents();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add event.");
        }
    }

    private void loadEvents() {
        String[] columnNames = {"ID", "Name", "Class No", "Building", "Date", "Time", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
    
        List<Event> events = FacultyDao.getEventsByFaculty(loggedInFacultyEmail);
        for (Event event : events) {
            tableModel.addRow(new Object[]{
                    event.getId(), event.getName(), event.getClassNo(), event.getBuilding(),
                    event.getDate(), event.getTime(), event.getDescription()
            });
        }
    }
    


    public static void main(String[] args) {
        new FacultyDashboard("Student");
    }




    private JPanel createShowAllEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 240));
    
        // 🔹 Top filter panel
        JPanel filterPanel = new JPanel(new FlowLayout());
        filterPanel.add(new JLabel("Degree:"));
        JComboBox<String> degreeFilter = new JComboBox<>(new String[]{"All", "BTech", "MTech", "MBA"});
        filterPanel.add(degreeFilter);
    
        filterPanel.add(new JLabel("Panel:"));
        JComboBox<String> panelFilter = new JComboBox<>(new String[]{"All", "A", "B", "C", "D"});
        filterPanel.add(panelFilter);
    
        JButton filterBtn = new JButton("Apply Filter");
        filterPanel.add(filterBtn);
    
        panel.add(filterPanel, BorderLayout.NORTH);


        //refresh button 
        // JButton refreshButton = new JButton("🧹 Refresh / Clean Expired Events");
        //     refreshButton.setBackground(new Color(220, 220, 220));
        //     refreshButton.setForeground(Color.BLACK);
        //     refreshButton.setFocusPainted(false);
        //     refreshButton.setFont(new Font("Tahoma", Font.PLAIN, 14));

        //     refreshButton.addActionListener(new ActionListener() {
        //         public void actionPerformed(ActionEvent e) {
        //             FacultyDao facultyDao1 = new FacultyDao();
        //             facultyDao1.deleteExpiredEvents(); // 🔁 Calls your delete method
            
        //             // Optional: Reload events in table after cleanup
        //             loadEvents(); // <-- assuming you already have this method
        //             JOptionPane.showMessageDialog(null, "Expired events removed successfully!");
        //         }
        //     });
            

    
        // 🔹 Table for events
        String[] columns = {"ID", "Name", "Class No", "Building", "Date", "Time", "Description", "Degree", "Panel"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
    
        // 🔹 Load all events initially
        FacultyDao.getAllEvents().forEach(event -> {
            model.addRow(new Object[]{
                event.getId(), event.getName(), event.getClassNo(), event.getBuilding(),
                event.getDate(), event.getTime(), event.getDescription(),
                event.getDegree(), event.getPanel()
            });
        });
    
        // 🔹 Filter logic
        filterBtn.addActionListener(e -> {
            String selectedDegree = degreeFilter.getSelectedItem().toString();
            String selectedPanel = panelFilter.getSelectedItem().toString();
    
            model.setRowCount(0); // clear table
    
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
    

    
    
    //update
    private JPanel createManageEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 240));
    
        String[] columns = {"ID", "Name", "Class No", "Building", "Date", "Time", "Degree", "Panel", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only "Action" column editable
            }
        };
    
        List<Event> myEvents = FacultyDao.getEventsByFaculty(loggedInFacultyEmail);
        for (Event event : myEvents) {
            model.addRow(new Object[]{
                event.getId(), event.getName(), event.getClassNo(), event.getBuilding(),
                event.getDate(), event.getTime(), event.getDegree(), event.getPanel(), "Edit/Delete"
            });
        }
    
        // Action dropdown
        table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Edit", "Delete"})) {
            @Override
            public boolean stopCellEditing() {
                String action = (String) getCellEditorValue();
                int row = table.getSelectedRow();
                Event event = myEvents.get(row);
    
                if ("Edit".equals(action)) {
                    JPanel updateForm = createUpdateEventForm(event);
                    mainContentPanel.removeAll();
                    mainContentPanel.add(updateForm, BorderLayout.NORTH);
                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                } else {
                    int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = FacultyDao.deleteEvent(event.getId());
                        if (deleted) {
                            model.removeRow(row);
                            JOptionPane.showMessageDialog(panel, "✅ Event deleted.");
                        } else {
                            JOptionPane.showMessageDialog(panel, "❌ Deletion failed.");
                        }
                    }
                }
    
                return super.stopCellEditing();
            }
        });
    
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    

    
   // 🔁 Update Event Form Dialog
   private JPanel createUpdateEventForm(Event event) {
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(new Color(255, 255, 240));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JTextField nameField = new JTextField(event.getName());
    JTextField classNoField = new JTextField(event.getClassNo());
    JTextField buildingField = new JTextField(event.getBuilding());
    JTextField dateField = new JTextField(event.getDate());
    JTextField timeField = new JTextField(event.getTime());
    JTextArea descriptionField = new JTextArea(event.getDescription());

    JComboBox<String> degreeDropdown = new JComboBox<>(new String[]{"BTech", "MTech", "MBA"});
    degreeDropdown.setSelectedItem(event.getDegree());
    JComboBox<String> panelDropdown = new JComboBox<>(new String[]{"A", "B", "C", "D"});
    panelDropdown.setSelectedItem(event.getPanel());

    int row = 0;
    formPanel.add(new JLabel("Name:"), gbc); gbc.gridx = 1; formPanel.add(nameField, gbc); gbc.gridx = 0; gbc.gridy = ++row;
    formPanel.add(new JLabel("Class No:"), gbc); gbc.gridx = 1; formPanel.add(classNoField, gbc); gbc.gridx = 0; gbc.gridy = ++row;
    formPanel.add(new JLabel("Building:"), gbc); gbc.gridx = 1; formPanel.add(buildingField, gbc); gbc.gridx = 0; gbc.gridy = ++row;
    formPanel.add(new JLabel("Date:"), gbc); gbc.gridx = 1; formPanel.add(dateField, gbc); gbc.gridx = 0; gbc.gridy = ++row;
    formPanel.add(new JLabel("Time:"), gbc); gbc.gridx = 1; formPanel.add(timeField, gbc); gbc.gridx = 0; gbc.gridy = ++row;
    formPanel.add(new JLabel("Degree:"), gbc); gbc.gridx = 1; formPanel.add(degreeDropdown, gbc); gbc.gridx = 0; gbc.gridy = ++row;
    formPanel.add(new JLabel("Panel:"), gbc); gbc.gridx = 1; formPanel.add(panelDropdown, gbc); gbc.gridx = 0; gbc.gridy = ++row;
    formPanel.add(new JLabel("Description:"), gbc); gbc.gridx = 1; formPanel.add(new JScrollPane(descriptionField), gbc); gbc.gridx = 0; gbc.gridy = ++row;

    JButton updateBtn = new JButton("Update");
    gbc.gridx = 1;
    formPanel.add(updateBtn, gbc);

    updateBtn.addActionListener(e -> {
        event.setName(nameField.getText());
        event.setClassNo(classNoField.getText());
        event.setBuilding(buildingField.getText());
        event.setDate(dateField.getText());
        event.setTime(timeField.getText());
        event.setDescription(descriptionField.getText());
        event.setDegree((String) degreeDropdown.getSelectedItem());
        event.setPanel((String) panelDropdown.getSelectedItem());

        boolean updated = FacultyDao.updateEvent(event);
        if (updated) {
            JOptionPane.showMessageDialog(formPanel, "✅ Event updated!");
            // Optionally reload the table
            handleSidebarAction("Update Event");
        } else {
            JOptionPane.showMessageDialog(formPanel, "❌ Update failed!");
        }
    });

    return formPanel;
}

//classroom avl
private JPanel createRoomAvailabilityPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(255, 255, 240));

    // 🔘 Filter panel
    JPanel topPanel = new JPanel(new FlowLayout());
    JTextField dateField = new JTextField(10); // Format: yyyy-MM-dd
    JTextField timeField = new JTextField(10); // Format: hh:mm AM/PM
    JButton checkBtn = new JButton("Check");

    topPanel.add(new JLabel("Date:")); topPanel.add(dateField);
    topPanel.add(new JLabel("Time:")); topPanel.add(timeField);
    topPanel.add(checkBtn);

    // 🔲 Table to show result
    String[] columns = {"Class No", "Building", "Date", "Time", "Faculty", "Event", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);

    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    checkBtn.addActionListener(e -> {
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        if (date.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both date and time.");
            return;
        }

        model.setRowCount(0); // clear table

        List<Map<String, String>> booked = ClassroomDao.getAllBookedRooms();
        List<Map<String, String>> free = ClassroomDao.getAllFreeRoomsOnDate(date, time);

        // Combine both
        for (Map<String, String> room : booked) {
            if (room.get("date").equals(date) && room.get("time").equalsIgnoreCase(time)) {
                model.addRow(new Object[]{
                    room.get("classNo"), room.get("building"), room.get("date"),
                    room.get("time"), room.get("faculty"), room.get("event"), room.get("status")
                });
            }
        }

        for (Map<String, String> room : free) {
            model.addRow(new Object[]{
                room.get("classNo"), room.get("building"), room.get("date"),
                room.get("time"), "-", "-", "Free"
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data found for given slot.");
        }
    });

    return panel;
}



private JPanel createUploadDocumentForm() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Fields
    JTextField titleField = new JTextField(20);
    JTextField descField = new JTextField(20);

    String[] degrees = {"BTech", "MTech", "MBA"};
    String[] panels = {"A", "B", "C", "D"};

    JComboBox<String> degreeDropdown = new JComboBox<>(degrees);
    JComboBox<String> panelDropdown = new JComboBox<>(panels);

    JLabel fileLabel = new JLabel("No file selected");
    JButton chooseFileBtn = new JButton("Choose File");
    JButton uploadBtn = new JButton("Upload Document");

    final File[] selectedFile = {null};

    // Add components to panel
    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(new JLabel("Title:"), gbc);
    gbc.gridx = 1;
    panel.add(titleField, gbc);

    gbc.gridx = 0; gbc.gridy = 1;
    panel.add(new JLabel("Description:"), gbc);
    gbc.gridx = 1;
    panel.add(descField, gbc);

    gbc.gridx = 0; gbc.gridy = 2;
    panel.add(new JLabel("Degree:"), gbc);
    gbc.gridx = 1;
    panel.add(degreeDropdown, gbc);

    gbc.gridx = 0; gbc.gridy = 3;
    panel.add(new JLabel("Panel:"), gbc);
    gbc.gridx = 1;
    panel.add(panelDropdown, gbc);

    gbc.gridx = 0; gbc.gridy = 4;
    panel.add(chooseFileBtn, gbc);
    gbc.gridx = 1;
    panel.add(fileLabel, gbc);

    gbc.gridx = 1; gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(uploadBtn, gbc);

    // File chooser logic
    chooseFileBtn.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile[0] = fileChooser.getSelectedFile();
            fileLabel.setText(selectedFile[0].getName());
        }
    });

    // Upload logic
    uploadBtn.addActionListener(e -> {
        if (selectedFile[0] == null) {
            JOptionPane.showMessageDialog(this, "Please choose a file to upload.");
            return;
        }

        try {
            String destFolder = "shared_docs";
            new File(destFolder).mkdirs(); // Ensure folder exists
            File destFile = new File(destFolder, selectedFile[0].getName());

            Files.copy(selectedFile[0].toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Document doc = new Document();
            doc.setTitle(titleField.getText());
            doc.setDescription(descField.getText());
            doc.setDegree((String) degreeDropdown.getSelectedItem());
            doc.setPanel((String) panelDropdown.getSelectedItem());
            doc.setFilename(destFile.getAbsolutePath());
            doc.setUploadedBy(loggedInFacultyEmail); // Replace with actual faculty email from session/login

            boolean success = DocumentDao.insertDocument(doc);
            if (success) {
                JOptionPane.showMessageDialog(this, "Document uploaded successfully.");
                titleField.setText("");
                descField.setText("");
                fileLabel.setText("No file selected");
                selectedFile[0] = null;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to upload document.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving file.");
        }
    });

    return panel;
}


}
