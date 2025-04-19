package smartcampus.dao;


import java.util.Map;
import java.util.HashMap;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import smartcampus.Database;
import smartcampus.model.Event;


public class ClassroomDao {

 
    
public static List<Map<String, String>> getAllBookedRooms() {
    List<Map<String, String>> list = new ArrayList<>();
    String sql = "SELECT * FROM classroom_availability WHERE is_available = false";

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, String> room = new HashMap<>();
            room.put("classNo", rs.getString("class_no"));
            room.put("building", rs.getString("building"));
            room.put("date", rs.getString("event_date"));
            room.put("time", rs.getString("event_time"));
            room.put("faculty", rs.getString("faculty_email"));
            room.put("event", rs.getString("event_name"));
            room.put("status", "Booked");
            list.add(room);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}


public static List<Map<String, String>> getAllFreeRoomsOnDate(String date, String time) {
    List<Map<String, String>> list = new ArrayList<>();

    String sql = "SELECT DISTINCT class_no, building FROM events " +
                 "WHERE CONCAT(class_no, building) NOT IN " +
                 "(SELECT CONCAT(class_no, building) FROM classroom_availability WHERE event_date = ? AND event_time = ?)";

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDate(1, Date.valueOf(date));
        stmt.setString(2, time);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Map<String, String> room = new HashMap<>();
            room.put("classNo", rs.getString("class_no"));
            room.put("building", rs.getString("building"));
            room.put("date", date);
            room.put("time", time);
            room.put("faculty", "-");
            room.put("event", "-");
            room.put("status", "Free");
            list.add(room);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}




    
   
}
