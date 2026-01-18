import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;




public class MatchHandler implements HttpHandler {

    @Override
    public void handle (HttpExchange exchange) throws IOException {
        //cors headrs
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");


        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {

                // get the matches from the database
                List<Match> matches = Database.getMatches();


                // 3. Build JSON manually
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < matches.size(); i++) {
                    json.append(matches.get(i).toString());
                    if (i < matches.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                String finalJson = json.toString();


                // 4. Send Response
                byte[] responseBytes = finalJson.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();

            } catch (Exception e) {
                System.out.println("❌ CRASH inside MatchHandler:");
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0); // Send error to browser
                exchange.close();
            }

            // i want to delete by id so i have to split
            // tyhe url string by every / and the id is the last
            // string from the path
        } else if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // get url path
                String path = exchange.getRequestURI().getPath();
                // split the string by the /
                String[] segments = path.split("/");
                String string_id = segments[segments.length - 1];
                int id = Integer.parseInt(string_id); // cast to integer
                Database.removeMatch(id);

                // 200-- succes;;; -1 send back nothing
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
            } catch (NumberFormatException e) {
                System.out.println("INVALID FORMAT SENT(last string not a number)");
                exchange.sendResponseHeaders(400, -1);// 400-- bad request
                exchange.close();
            } catch (Exception e) {
                System.out.println("ERROR DELETING THE MATCH");
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);// 500-- server error
                exchange.close();
            }

        }


        if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // read json sent from javascript
                java.io.InputStreamReader isr = new java.io.InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                java.io.BufferedReader br = new java.io.BufferedReader(isr);
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) body.append(line);

                String json = body.toString();

                // parse datas
                // need a helper function for numbers, or we parse them as strings first
                int id = Integer.parseInt(parseJsonValue(json, "id"));
                String home = parseJsonValue(json, "teamHome");
                String away = parseJsonValue(json, "teamAway");
                String date = parseJsonValue(json, "matchDate");
                String location = parseJsonValue(json, "location");
                String priceStr = parseJsonValue(json, "price");
                double price = Double.parseDouble(priceStr); // castez din string in double

                String imageUrl = parseJsonValue(json, "image_url"); // parse the image url

                // call the database
                boolean succes = Database.updateMatch(id, home, away, date, location, price, imageUrl);

                // pass image url to the database
                boolean success = Database.updateMatch(id, home, away, date, location, price, imageUrl);
                if (succes) {
                    exchange.sendResponseHeaders(200, -1);
                } else {
                    exchange.sendResponseHeaders(404, -1);// not found
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
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
                String home = parseJsonValue(json, "teamHome");
                String away = parseJsonValue(json, "teamAway");
                String stadium = parseJsonValue(json, "stadium");
                String date = parseJsonValue(json, "matchDate");
                String location = parseJsonValue(json, "location");
                String imageUrl = parseJsonValue(json, "image_url"); // The new photo field

                // handle price
                double price = 0.0;
                String price_str = parseJsonValue(json, "price");
                if (!price_str.isEmpty()) {
                    price = Double.parseDouble(price_str);
                }

                // call database
                Database.addMatch(home, away, stadium, date, location, price, imageUrl);
                exchange.sendResponseHeaders(200, -1);// succes

            } catch(Exception e) {
                System.out.println("❌ Error in POST:");
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
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
