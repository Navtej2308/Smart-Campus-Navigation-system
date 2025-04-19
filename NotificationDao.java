package smartcampus.dao;

import smartcampus.Database;  // Import your Database class
import smartcampus.model.Notification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDao {
    private Connection conn;

    public NotificationDao(Connection conn) {
        this.conn = conn;
    }

    // Add a new notification
    public boolean addNotification(Notification notification) {
        String sql = "INSERT INTO notifications (title, message, created_at, created_by, panel,degree) VALUES (?, ?, ?, ?, ?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notification.getTitle());
            ps.setString(2, notification.getMessage());
            ps.setTimestamp(3, Timestamp.valueOf(notification.getCreatedAt())); // ensure createdAt is a LocalDateTime
            ps.setString(4, notification.getCreatedBy());
            ps.setString(5, notification.getPanel());
            ps.setString(6, notification.getDegree());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all notifications for a specific panel
    public List<Notification> getNotificationsForStudent(String degree, String panel) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE degree = ? AND panel = ? ORDER BY created_at DESC";
    
        System.out.println("Executing getNotificationsForStudent with degree: " + degree + ", panel: " + panel);
    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, degree);
            ps.setString(2, panel);
    
            System.out.println("SQL Query: " + ps.toString());
    
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setTitle(rs.getString("title"));
                n.setMessage(rs.getString("message"));
    
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    n.setCreatedAt(timestamp.toLocalDateTime());
                }
    
                n.setCreatedBy(rs.getString("created_by"));
                n.setPanel(rs.getString("panel"));
                n.setDegree(rs.getString("degree"));
    
                System.out.println("Fetched Notification: " + n.getTitle() + " | " + n.getMessage());
    
                list.add(n);
            }
    
            System.out.println("Total notifications fetched: " + list.size());
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return list;
    }
    

    // Delete a notification by ID (optional)
    public boolean deleteNotification(int id) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all notifications (use Database connection)
    public static List<Notification> getAllNotifications() {
        List<Notification> list = new ArrayList<>();
        try (Connection con = Database.getConnection()) {  // Use Database class to get connection
            String sql = "SELECT * FROM notifications ORDER BY created_at DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setMessage(rs.getString("message"));
                n.setCreatedBy(rs.getString("created_by"));  // Correct field name
                n.setPanel(rs.getString("panel"));

                // Handling Timestamp to LocalDateTime conversion
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    n.setCreatedAt(timestamp.toLocalDateTime());  // Convert Timestamp to LocalDateTime
                }

                list.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
