package smartcampus.dao;

import java.sql.*;
import smartcampus.Database;
import smartcampus.model.User;

public class UserDAO {
    
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());  // In real projects, hash password before storing
            stmt.setString(4, user.getRole());
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            return false;
        }
    }

    public static User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getString("role"));
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }
        return null;
    }
}
