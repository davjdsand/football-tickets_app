import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    // ==========================================
    // CONFIGURATION
    // ==========================================
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; // <--- CHECK THIS!

    // ==========================================
    // SECTION 1: USERS
    // ==========================================
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

    // ==========================================
    // SECTION 2: MATCHES (THE UPGRADED PART)
    // ==========================================

    // READ: We read from the VIEW (match_details) so it looks like a simple table
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
        String sql = "INSERT INTO matches (home_team_id, away_team_id, stadium_id, match_date, price) VALUES (?, ?, ?, ?, ?)";

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
                pstmt.executeUpdate();
                System.out.println("✅ Match Added (Academic Structure)");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // HELPER: Finds the ID of a Team/Stadium, or creates it if it doesn't exist
    private static int getOrInsertId(Connection conn, String table, String name, String extraInfo) throws SQLException {
        // Search first
        String query = "SELECT id FROM " + table + " WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }

        // If not found, Insert
        String insert = "INSERT INTO " + table + " (name" + (table.equals("teams") ? ", logo_url" : ", location") + ") VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, extraInfo == null ? "" : extraInfo);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
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

    // Note: Update is complex in 3NF, removing it for simplicity or you can update just Date/Price
    public static boolean updateMatch(int id, String home, String away, String date, String location, double price) {
        String sql = "UPDATE matches SET match_date=?, price=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // SECTION 3: TRANSACTIONS
    // ==========================================
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
}