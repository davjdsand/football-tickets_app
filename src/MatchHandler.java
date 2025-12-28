import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;




public class MatchHandler implements HttpHandler {

    @Override
    public void handle (HttpExchange exchange) throws IOException {
        //cors headrs
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
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
    }
}
