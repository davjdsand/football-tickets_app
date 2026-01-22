package Match;

import Database.Database;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MatchHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. CORS Headers (Allows your browser JS to talk to Java)
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // 2. Handle OPTIONS (Pre-flight check)
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // 3. GET: Retrieve all matches
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                List<Match> matches = Database.getMatches();

                // Build JSON manually
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < matches.size(); i++) {
                    json.append(matches.get(i).toString());
                    if (i < matches.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                String finalJson = json.toString();

                byte[] responseBytes = finalJson.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();

            } catch (Exception e) {
                System.out.println("❌ CRASH inside Match.MatchHandler (GET):");
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.close();
            }

            // 4. DELETE: Remove a match by ID
        } else if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                String path = exchange.getRequestURI().getPath();
                String[] segments = path.split("/");
                String string_id = segments[segments.length - 1];
                int id = Integer.parseInt(string_id);

                Database.removeMatch(id);

                exchange.sendResponseHeaders(200, -1);
                exchange.close();
            } catch (NumberFormatException e) {
                System.out.println("INVALID ID FORMAT");
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }

            // 5. PUT: Update an existing match
        } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                String json = readBody(exchange);

                // Parse Data (Including 'stadium'!)
                int id = Integer.parseInt(parseJsonValue(json, "id"));
                String home = parseJsonValue(json, "teamHome");
                String away = parseJsonValue(json, "teamAway");
                String stadium = parseJsonValue(json, "stadium"); // ✅ Correctly reading stadium
                String date = parseJsonValue(json, "matchDate");
                String location = parseJsonValue(json, "location");
                String priceStr = parseJsonValue(json, "price");
                double price = Double.parseDouble(priceStr);
                String imageUrl = parseJsonValue(json, "image_url");

                // Call Database.Database (Void method, no boolean return)
                Database.updateMatch(id, home, away, stadium, date, location, price, imageUrl);

                exchange.sendResponseHeaders(200, -1);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();

            // 6. POST: Add a new match
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                String json = readBody(exchange);

                // Parse Data
                String home = parseJsonValue(json, "teamHome");
                String away = parseJsonValue(json, "teamAway");
                String stadium = parseJsonValue(json, "stadium");
                String date = parseJsonValue(json, "matchDate");
                String location = parseJsonValue(json, "location");
                String priceStr = parseJsonValue(json, "price");
                String imageUrl = parseJsonValue(json, "image_url");

                double price = 0.0;
                if (!priceStr.isEmpty()) {
                    price = Double.parseDouble(priceStr);
                }

                // Call Database.Database
                Database.addMatch(home, away, stadium, date, location, price, imageUrl);

                exchange.sendResponseHeaders(200, -1);
            } catch(Exception e) {
                System.out.println("❌ Error in POST:");
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();
        }
    }

    // --- HELPER METHODS ---

    // Reads the Request Body (JSON string)
    private String readBody(HttpExchange exchange) throws IOException {
        java.io.InputStreamReader isr = new java.io.InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        java.io.BufferedReader br = new java.io.BufferedReader(isr);
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) body.append(line);
        return body.toString();
    }

    // Manually parses a simple JSON string
    private String parseJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";

        start += search.length();

        // Check if value is string or number
        char firstChar = json.charAt(start);
        if (firstChar == '"') {
            start++; // skip opening quote
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } else {
            // It's a number
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).trim();
        }
    }
}