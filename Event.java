// UPDATED Event.java
package smartcampus.model;

import java.util.Random;

public class Event {
    private int id; // New ID field
    private String name;
    private String classNo;
    private String building;
    private String description;
    private String date;
    private String time;
    private String forWhomType;
    private String degree;
    private String panel;
    private String prn;
    private String facultyEmail;

    // Constructor for adding new event (ID will be randomly generated)
    public Event(String name, String classNo, String building, String description, String date, String time,
                 String forWhomType, String degree, String panel, String prn, String facultyEmail) {
        this.id = generateRandomId();
        this.name = name;
        this.classNo = classNo;
        this.building = building;
        this.description = description;
        this.date = date;
        this.time = time;
        this.forWhomType = forWhomType;
        this.degree = degree;
        this.panel = panel;
        this.prn = prn;
        this.facultyEmail = facultyEmail;
    }

    // Constructor with ID (used when reading from database)
    public Event(int id, String name, String classNo, String building, String description, String date, String time,
                 String forWhomType, String degree, String panel, String prn, String facultyEmail) {
        this.id = id;
        this.name = name;
        this.classNo = classNo;
        this.building = building;
        this.description = description;
        this.date = date;
        this.time = time;
        this.forWhomType = forWhomType;
        this.degree = degree;
        this.panel = panel;
        this.prn = prn;
        this.facultyEmail = facultyEmail;
    }

    private int generateRandomId() {
        return new Random().nextInt(900) + 100; // generates a number between 100–999
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getClassNo() { return classNo; }
    public String getBuilding() { return building; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getForWhomType() { return forWhomType; }
    public String getDegree() { return degree; }
    public String getPanel() { return panel; }
    public String getPrn() { return prn; }
    public String getFacultyEmail() { return facultyEmail; }


    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setClassNo(String classNo) { this.classNo = classNo; }
    public void setBuilding(String building) { this.building = building; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setForWhomType(String forWhomType) { this.forWhomType = forWhomType; }
    public void setDegree(String degree) { this.degree = degree; }
    public void setPanel(String panel) { this.panel = panel; }
    public void setPrn(String prn) { this.prn = prn; }
    public void setFacultyEmail(String facultyEmail) { this.facultyEmail = facultyEmail;}
}