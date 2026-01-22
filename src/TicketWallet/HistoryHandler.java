package TicketWallet;

import Database.Database;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HistoryHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. CORS Headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                String query = exchange.getRequestURI().getQuery();
                String username = "";

                // 2. Parse parameters
                if (query != null) {
                    String[] pairs = query.split("&");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=");
                        if (keyValue.length == 2 && keyValue[0].equals("username")) {
                            // Decode handles spaces (e.g., "Donald%20Trump" -> "Donald Trump")
                            username = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        }
                    }
                }

                if (username.isEmpty()) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                // 3. Get History from Database.Database
                String response = Database.getUserHistory(username);

                // 4. Send Response (Fixes the character length bug)
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
}