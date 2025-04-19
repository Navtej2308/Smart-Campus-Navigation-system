package smartcampus.dao;


import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import smartcampus.Database;
import smartcampus.model.Document;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentDao {
    public static boolean insertDocument(Document doc) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO documents (title, description, filename, degree, panel, uploaded_by) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doc.getTitle());
            stmt.setString(2, doc.getDescription());
            stmt.setString(3, doc.getFilename());
            stmt.setString(4, doc.getDegree());
            stmt.setString(5, doc.getPanel());
            stmt.setString(6, doc.getUploadedBy());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //student dashboard
    public static List<Document> getDocumentsForStudent(String degree, String panel) {
        List<Document> docs = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM documents WHERE degree = ? AND panel = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, degree);
            stmt.setString(2, panel);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                Document doc = new Document();
                doc.setId(rs.getInt("id"));
                doc.setTitle(rs.getString("title"));
                doc.setDescription(rs.getString("description"));
                doc.setFilename(rs.getString("filename"));
                doc.setDegree(rs.getString("degree"));
                doc.setPanel(rs.getString("panel"));
                doc.setUploadedBy(rs.getString("uploaded_by"));
                doc.setUploadedAt(rs.getString("uploaded_at"));
                docs.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs;
    }
}
