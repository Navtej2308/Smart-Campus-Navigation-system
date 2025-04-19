package smartcampus.model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private String createdBy; // this is faculty_email
    private String panel;
    private String degree;

    // Constructors
    public Notification() {}

    public Notification(String title, String message, String createdBy, String panel) {
        this.title = title;
        this.message = message;
        this.createdBy = createdBy;
        this.panel = panel;
        this.createdAt = LocalDateTime.now();
    }
    private String facultyEmail;
    

    // Add the getter for facultyEmail
    public String getFacultyEmail() {
        return facultyEmail;
    }
    public void setFacultyEmail(String facultyEmail) {
        this.facultyEmail = facultyEmail;
    }
    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }

    

public String getDegree() {
    return degree;
}

public void setDegree(String degree) {
    this.degree = degree;
}
}
