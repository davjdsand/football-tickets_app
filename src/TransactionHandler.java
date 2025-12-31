import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jdk.jfr.DataAmount;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TransactionHandler implements HttpHandler {

    public void handle (HttpExchange exchange) throws IOException {
        //cors headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");


        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }


        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // read json body
                java.io.InputStreamReader isr = new java.io.InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                java.io.BufferedReader br = new java.io.BufferedReader(isr);
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) body.append(line);
                String json = body.toString();

                // parse Data
                // we DO NOT parse 'id' because the database generates it automatically
                String zone = parseJsonValue(json, "zone_name");
                String username = parseJsonValue(json, "username");

                // parseing integers
                int match_id = Integer.parseInt(parseJsonValue(json, "match_id"));
                int seat = Integer.parseInt(parseJsonValue(json, "seat_nr"));
                double price = Double.parseDouble(parseJsonValue(json, "price"));

                // save to db
                Database.addTransaction(zone, username, match_id, seat, price);

                // succes response
                exchange.sendResponseHeaders(200, -1);

            } catch(Exception e) {
                System.out.println("❌ Error in POST:");
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private String parseJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";

        start += search.length();

        // Check if the value is a string (starts with quote) or number
        char firstChar = json.charAt(start);
        if (firstChar == '"') {
            start++; // skip opening quote
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } else {
            // It's a number (like price or id)
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start); // case where it's the last item
            return json.substring(start, end).trim();
        }
    }
}
