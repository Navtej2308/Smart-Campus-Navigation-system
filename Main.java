package smartcampus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import smartcampus.dao.LocationDao;
import smartcampus.model.Location;

public class Main {

    public static void main(String[] args) throws Exception {
        // Set up the server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Define the context for '/LocationServlet' and associate it with the LocationHandler
        server.createContext("/LocationServlet", new LocationHandler());

        // Start the server
        server.start();
        System.out.println("Server is running on http://localhost:8080");
    }

    // Handler for the '/LocationServlet' endpoint
    static class LocationHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle CORS - Allow cross-origin requests from the frontend
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");  // Allows all origins
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            // If it's a preflight OPTIONS request, send an empty response
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);  // Empty response
                return;
            }

            // Parse the query parameters (this is just for illustration, you can pass filters via POST or GET)
            String query = exchange.getRequestURI().getQuery();
            boolean showBuildings = query.contains("showBuildings=true");
            boolean showCafes = query.contains("showCafes=true");
            boolean showLibraries = query.contains("showLibraries=true");
            boolean showSports = query.contains("showSports=true");
            boolean showParking = query.contains("showParking=true");

            // Fetch filtered locations from the database using LocationDao
            LocationDao dao = new LocationDao();
            List<Location> locations = dao.getFilteredLocations(
                    showBuildings,
                    showCafes,
                    showLibraries,
                    showSports,
                    showParking
            );

            // Convert the list of locations to JSON
            Gson gson = new Gson();
            String json = gson.toJson(locations);

            // Set the response headers and send the JSON data
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        }
    }
}
