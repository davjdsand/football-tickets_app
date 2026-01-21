import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    // database configuration
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; // <--- CHECK THIS!

    // users registration
    public static User signUp(String username, String password) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, "USER");
            int rows = pstmt.executeUpdate();
            if (rows > 0) return new User(0, username, password, "USER");
        } catch (SQLException e) {
            System.out.println("❌ SignUp Error: " + e.getMessage());
        }
        return null;
    }

    public static User checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // we read from the VIEW (match_details) so it looks like a simple table
    public static List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM match_details ORDER BY id ASC"; // <--- READING FROM VIEW

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Match m = new Match(
                        rs.getInt("id"),
                        rs.getString("team_home"), // The View gives us Strings!
                        rs.getString("team_away"),
                        rs.getString("stadium"),
                        rs.getString("match_date"),
                        rs.getDouble("price"),
                        rs.getString("location"),
                        rs.getString("image_url")
                );
                matches.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return matches;
    }

    // WRITE: We must look up IDs first, then insert into matches
    public static void addMatch(String home, String away, String stadium, String date, String location, double price, String imageUrl) {
        String sql = "INSERT INTO matches (home_team_id, away_team_id, stadium_id, match_date, price, image_url) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // 1. Get IDs for the names (Create them if they don't exist)
            int homeId = getOrInsertId(conn, "teams", home, imageUrl);
            int awayId = getOrInsertId(conn, "teams", away, null);
            int stadiumId = getOrInsertId(conn, "stadiums", stadium, location);

            // 2. Insert the match using IDs
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, homeId);
                pstmt.setInt(2, awayId);
                pstmt.setInt(3, stadiumId);
                pstmt.setString(4, date);
                pstmt.setDouble(5, price);
                pstmt.setString(6, imageUrl);
                pstmt.executeUpdate();
                System.out.println("✅ Match Added ");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static int getOrInsertId(Connection conn, String table, String name, String extraInfo) throws SQLException {
        // 1. Search Logic
        String query;
        if (table.equals("stadiums")) {
            // For stadiums, we must match Name AND Location
            query = "SELECT id FROM stadiums WHERE name = ? AND location = ?";
        } else {
            // For teams, we only check Name
            query = "SELECT id FROM teams WHERE name = ?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            if (table.equals("stadiums")) {
                pstmt.setString(2, extraInfo); // Check location too
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id"); // Found existing one!
        }

        // 2. Insert Logic (If not found)
        String insert;
        if (table.equals("teams")) {
            insert = "INSERT INTO teams (name) VALUES (?)";
        } else {
            insert = "INSERT INTO stadiums (name, location) VALUES (?, ?)";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            if (!table.equals("teams")) {
                pstmt.setString(2, extraInfo == null ? "" : extraInfo);
            }
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1); // Return the NEW ID
        }
        return -1;
    }


    public static void removeMatch(int id) {
        // DELETE CASCADE in SQL handles the transactions automatically
        String sql = "DELETE FROM matches WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateMatch(int matchId, String home, String away, String stadium, String date, String location, double price, String imageUrl) {
        // update Match Details
        String sqlMatch = "UPDATE matches SET home_team_id=?, away_team_id=?, stadium_id=?, match_date=?, price=?, image_url=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // 1. Get (or create) IDs for the new names
            // (Since teams/stadiums only have names now, we pass null for extra info)
            int homeId = getOrInsertId(conn, "teams", home, null);
            int awayId = getOrInsertId(conn, "teams", away, null);
            int stadiumId = getOrInsertId(conn, "stadiums", stadium, location);

            // 2. Execute Update
            try (PreparedStatement pstmt = conn.prepareStatement(sqlMatch)) {
                pstmt.setInt(1, homeId);
                pstmt.setInt(2, awayId);
                pstmt.setInt(3, stadiumId);
                pstmt.setString(4, date);
                pstmt.setDouble(5, price);
                pstmt.setString(6, imageUrl); // This saves the banner to the match!
                pstmt.setInt(7, matchId);          // Identifies which match to update

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ Match Updated Successfully!");
                } else {
                    System.out.println("❌ Error: Match ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Transactions
    public static void addTransaction(String zone, String username, int matchId, int seatNr, double price) {
        String sql = "INSERT INTO transactions (zone_name, username, match_id, seat_nr, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, zone);
            pstmt.setString(2, username);
            pstmt.setInt(3, matchId);
            pstmt.setInt(4, seatNr);
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Integer> getTakenSeats(int targetMatchId, String targetZone) {
        List<Integer> takenSeats = new ArrayList<>();
        String sql = "SELECT seat_nr FROM transactions WHERE match_id = ? AND zone_name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, targetMatchId);
            pstmt.setString(2, targetZone);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) takenSeats.add(rs.getInt("seat_nr"));
        } catch (SQLException e) { e.printStackTrace(); }
        return takenSeats;
    }

    // Add this inside Database.java

    public static String getUserHistory(String username) {
        StringBuilder json = new StringBuilder("[");

        // ✅ Added "t.zone_name" to the SELECT list
        String sql = "SELECT t.seat_nr, t.zone_name, t.price, m.match_date, " +
                "t1.name AS home, t2.name AS away, s.name AS stadium " +
                "FROM transactions t " +
                "JOIN matches m ON t.match_id = m.id " +
                "JOIN teams t1 ON m.home_team_id = t1.id " +
                "JOIN teams t2 ON m.away_team_id = t2.id " +
                "JOIN stadiums s ON m.stadium_id = s.id " +
                "WHERE t.username = ?";

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "admin");
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            java.sql.ResultSet rs = pstmt.executeQuery();

            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"match\": \"").append(rs.getString("home")).append(" vs ").append(rs.getString("away")).append("\",");
                json.append("\"date\": \"").append(rs.getString("match_date")).append("\",");
                json.append("\"stadium\": \"").append(rs.getString("stadium")).append("\",");
                // ✅ Added Zone to the JSON response
                json.append("\"zone\": \"").append(rs.getString("zone_name")).append("\",");
                json.append("\"seat\": ").append(rs.getInt("seat_nr")).append(",");
                json.append("\"price\": ").append(rs.getDouble("price"));
                json.append("}");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        json.append("]");
        return json.toString();
    }
}

