package smartcampus.model;

public class StudentProfile {
    private int userId;
    private String studentPrn;
    private String rollNo;
    private String degree;
    private String panel;
    private String photoPath;

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStudentPrn() { return studentPrn; }
    public void setStudentPrn(String studentPrn) { this.studentPrn = studentPrn; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getPanel() { return panel; }
    public void setPanel(String panel) { this.panel = panel; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
