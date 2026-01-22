package Tests;
import Match.*;

public class TestMatch {

    public static void main(String[] args) {
        System.out.println("🧪 STARTING UNIT TEST: Match.Match Class...");

        // 1. Database.Setup Test Data
        int id = 100;
        String home = "Test FC";
        String away = "Debug United";
        String stadium = "Localhost Arena";
        String date = "2025-01-01";
        double price = 50.00;
        String location = "Cyber Space";
        String image = "http://test.com/img.png";

        // 2. Create Object
        Match match = new Match(id, home, away, stadium, date, price, location, image);

        // 3. Run Assertions (Checks)
        boolean passed = true;

        if (!match.getHomeTeam().equals(home)) {
            System.out.println("❌ Failed: Home Team name mismatch.");
            passed = false;
        }

        if (match.getPrice() != 50.00) {
            System.out.println("❌ Failed: Price mismatch.");
            passed = false;
        }

        // Check JSON format (Crucial for frontend!)
        String json = match.toString();
        if (!json.contains("\"teamHome\": \"Test FC\"")) {
            System.out.println("❌ Failed: JSON string is missing home team.");
            passed = false;
        }

        // 4. Final Result
        if (passed) {
            System.out.println("✅ MATCH CLASS TEST PASSED!");
        } else {
            System.out.println("❌ SOME TESTS FAILED.");
        }
    }
}