package smartcampus.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import smartcampus.Database;
import smartcampus.model.Location;

public class LocationDao {

    public List<Location> getFilteredLocations(boolean showBuildings, boolean showCafes, boolean showLibraries, boolean showSports, boolean showParking) {
        List<Location> locations = new ArrayList<>();
        
        // Use Database.java for connection
        Connection conn = Database.getConnection();
        if (conn == null) {
            System.out.println("Database connection failed");
            return locations;
        }
        
        String sql = "SELECT * FROM locations WHERE ";
        
        // Build dynamic query based on filters
        List<String> filters = new ArrayList<>();
        
        if (showBuildings) filters.add("location_type = 'academic_building'");
        if (showCafes) filters.add("location_type = 'cafe'");
        if (showLibraries) filters.add("location_type = 'library'");
        if (showSports) filters.add("location_type = 'sports'");
        if (showParking) filters.add("location_type = 'parking'"); // optional if you add events

        if (filters.isEmpty()) {
            sql = "SELECT * FROM locations";  // No filters
        } else {
            sql += String.join(" OR ", filters);
        }
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Location location = new Location();
                location.setId(rs.getInt("location_id"));
                location.setName(rs.getString("name"));
                location.setType(rs.getString("location_type"));
                location.setLatitude(rs.getDouble("latitude"));
                location.setLongitude(rs.getDouble("longitude"));
                location.setDescription(rs.getString("description"));
                
                locations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
