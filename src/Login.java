
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Login implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. Allow the frontend to talk to us (CORS)
        // Cross-Origin Resource Sharing
        // dau allow la browser sa se conecteze la server
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // OPTIONS is a http request
        // 204 code means "Succes, No content"
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }


        // these lines of code translate from machine code to characters(utf_8)
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            // 2. Read the data sent from JavaScript
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr); // bufferu asta citeste linii intregi de cod machina
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line);
            }

            String request_data = body.toString();
            String username = parseJsonValue(request_data, "username");
            String password = parseJsonValue(request_data, "password");


            User user = Database.checkLogin(username, password);

            String response;
            int status_code;

            if (user != null) {
                status_code = 200;
                response = user.toString();
            } else {
                status_code = 401;
                response = "{\"error\": \"Wrong usernamme or password\"}";
            }


            byte[] response_bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status_code, response_bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response_bytes);
            os.close();

        }
    }

    private String parseJsonValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) {
            return "";
        }
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);

    }

}
