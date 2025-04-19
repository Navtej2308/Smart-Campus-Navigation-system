package smartcampus.dao;

import java.sql.*;
import smartcampus.Database;
import smartcampus.model.StudentProfile;

public class StudentDao {

    public static StudentProfile getStudentProfileByUserId(int userId) {
        StudentProfile profile = null;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM student_profiles WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                profile = new StudentProfile();
                profile.setUserId(userId);
                profile.setStudentPrn(rs.getString("student_prn"));
                profile.setRollNo(rs.getString("roll_no"));
                profile.setDegree(rs.getString("degree"));
                profile.setPanel(rs.getString("panel"));
                profile.setPhotoPath(rs.getString("photo_path"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }

    public static boolean insertStudentProfile(StudentProfile profile) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO student_profiles (user_id, student_prn, roll_no, degree, panel, photo_path) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getStudentPrn());
            stmt.setString(3, profile.getRollNo());
            stmt.setString(4, profile.getDegree());
            stmt.setString(5, profile.getPanel());
            stmt.setString(6, profile.getPhotoPath());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

 


    public static int getUserIdByEmail(String email) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    //update student profile

    public static boolean updateStudentProfile(StudentProfile profile) {
        String sql = "UPDATE student_profiles SET student_prn = ?, roll_no = ?, degree = ?, panel = ?, photo_path = ? WHERE user_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getStudentPrn());
            ps.setString(2, profile.getRollNo());
            ps.setString(3, profile.getDegree());
            ps.setString(4, profile.getPanel());
            ps.setString(5, profile.getPhotoPath());
            ps.setInt(6, profile.getUserId());
    
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
}
