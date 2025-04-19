package smartcampus.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import smartcampus.Database;
import smartcampus.model.Event;

import smartcampus.model.Notification;
import smartcampus.dao.NotificationDao;
import java.time.LocalDateTime;



public class FacultyDao {

    //add event 
    public static boolean addEvent(Event event) {
        String sql = "INSERT INTO events (id, event_name, class_no, building, description, event_date, event_time, to_whom, degree, panel, prn, faculty_email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, event.getId());
            stmt.setString(2, event.getName());
            stmt.setString(3, event.getClassNo());
            stmt.setString(4, event.getBuilding());
            stmt.setString(5, event.getDescription());
            stmt.setString(6, event.getDate());
            stmt.setString(7, event.getTime());
            stmt.setString(8, event.getForWhomType());
            stmt.setString(9, event.getDegree());
            stmt.setString(10, event.getPanel());
            stmt.setString(11, event.getPrn());
            stmt.setString(12, event.getFacultyEmail());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // ✅ Send notification
                Notification notification = new Notification();
                notification.setTitle("New Event: " + event.getName());
                notification.setMessage("A new event has been added: " + event.getName());
                notification.setCreatedAt(LocalDateTime.now());
                notification.setCreatedBy(event.getFacultyEmail());
                notification.setPanel(event.getPanel());
                notification.setDegree(event.getDegree());
    
            System.out.println("Preparing to add notification: " + notification.getTitle()); // Debugging statement

            NotificationDao notificationDao = new NotificationDao(conn);
            boolean notificationAdded = notificationDao.addNotification(notification);
            if (notificationAdded) {
                System.out.println("Notification added successfully.");
            } else {
                System.out.println("Failed to add notification.");
            }
    
                return true;
            }
    
           return false;
            

        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
  //display all the events
    public static List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM events";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Event event = new Event(
                    rs.getInt("id"),
                    rs.getString("event_name"),
                    rs.getString("class_no"),
                    rs.getString("building"),
                    rs.getString("description"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("to_whom"),
                    rs.getString("degree"),
                    rs.getString("panel"),
                    rs.getString("prn"),
                    rs.getString("faculty_email")
                );
                eventList.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventList;
    }
  //filtering event by faculty
    public static List<Event> getEventsByFaculty(String facultyEmail) {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE faculty_email = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, facultyEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                    rs.getInt("id"),
                    rs.getString("event_name"),
                    rs.getString("class_no"),
                    rs.getString("building"),
                    rs.getString("description"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("to_whom"),
                    rs.getString("degree"),
                    rs.getString("panel"),
                    rs.getString("prn"),
                    rs.getString("faculty_email")
                );
                list.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

   // update event 
   public static boolean updateEvent(Event event) {
    String sql = "UPDATE events SET event_name=?, class_no=?, building=?, description=?, event_date=?, event_time=?, degree=?, panel=? WHERE id=?";

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, event.getName());
        stmt.setString(2, event.getClassNo());
        stmt.setString(3, event.getBuilding());
        stmt.setString(4, event.getDescription());
        stmt.setString(5, event.getDate());
        stmt.setString(6, event.getTime());
        stmt.setString(7, event.getDegree());
        stmt.setString(8, event.getPanel());
        stmt.setInt(9, event.getId());

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            // ✅ Send notification
            Notification notification = new Notification();
            notification.setTitle("Event Updated: " + event.getName());
            notification.setMessage("An event has been updated: " + event.getName());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setCreatedBy(event.getFacultyEmail());
            notification.setPanel(event.getPanel());
            notification.setDegree(event.getDegree());

            NotificationDao notificationDao = new NotificationDao(conn);
            boolean notificationAdded = notificationDao.addNotification(notification);
            if (notificationAdded) {
                System.out.println("Notification added successfully.");
            } else {
                System.out.println("Failed to add notification.");
            }
            return true;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}


    //delete 
    public static boolean deleteEvent(int id) {
        String selectSql = "SELECT * FROM events WHERE id = ?";
        String deleteSql = "DELETE FROM events WHERE id = ?";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
    
            // Fetch event details
            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();
    
            if (rs.next()) {
                String eventName = rs.getString("event_name");
                String description = rs.getString("description");
                String panel = rs.getString("panel");
                String facultyEmail = rs.getString("faculty_email");
                String degree = rs.getString("degree");
    
                // Delete event
                deleteStmt.setInt(1, id);
                int rowsAffected = deleteStmt.executeUpdate();
    
                if (rowsAffected > 0) {
                    // ✅ Send notification
                    Notification notification = new Notification();
                    notification.setTitle("Event Deleted: " + eventName);
                    notification.setMessage("An event has been removed: " + eventName);
                    notification.setCreatedAt(LocalDateTime.now());
                    notification.setCreatedBy(facultyEmail);
                    notification.setPanel(panel);
                    notification.setDegree(degree);

                    
                    
                    
    
                    NotificationDao notificationDao = new NotificationDao(conn);
                    notificationDao.addNotification(notification);
                    return true;
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    
//delete expired events


public static void deleteExpiredEvents() {
    System.out.println("🧹 Auto-delete process started...");

    List<Event> allEvents = getAllEvents();

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    for (Event event : allEvents) {
        try {
            String dateStr = event.getDate().trim();     // "2025-04-13"
            String timeStr = event.getTime().trim().replaceAll("\\s+", " "); // ensure one space only

            System.out.println("🔍 Parsing Date: " + dateStr + " | Time: " + timeStr);

            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            LocalTime time = LocalTime.parse(timeStr, timeFormatter);

            LocalDateTime eventDateTime = LocalDateTime.of(date, time);
            LocalDateTime now = LocalDateTime.now();

            System.out.println("⏳ Event ID: " + event.getId() + " | Event Time: " + eventDateTime + " | Now: " + now);

            if (now.isAfter(eventDateTime.plusHours(1))) {
                boolean deleted = deleteEvent(event.getId());
                if (deleted) {
                    System.out.println("🗑 Deleted event ID: " + event.getId());
                } else {
                    System.out.println("❌ Failed to delete event ID: " + event.getId());
                }
            } else {
                System.out.println("✅ Event ID " + event.getId() + " is still valid.");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Failed to parse/delete event ID " + event.getId());
            e.printStackTrace();
        }
    }
}


    





    
   
}
