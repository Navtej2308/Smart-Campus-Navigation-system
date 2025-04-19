package smartcampus.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;

    // ✅ Constructor with ID
    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ✅ Constructor without ID (for new users before database assignment)
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ✅ Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // ✅ Add getUsername() to fix FacultyDashboard issue
    public String getUsername() {
        return email;  // Change to `name` if facultyUsername refers to `name`
    }

    private String prn;

public String getPrn() {
    return prn;
}

public void setPrn(String prn) {
    this.prn = prn;
}

}
