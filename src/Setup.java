import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Setup {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    public static void main(String[] args) {

        String sql = """
            -- =============================================================
            -- FINAL PROJECT: "MATCH-ONLY" IMAGES VERSION (RESTORED)
            -- =============================================================

            -- 1. CLEANUP
            DROP VIEW IF EXISTS match_details CASCADE;
            DROP TABLE IF EXISTS transactions CASCADE;
            DROP TABLE IF EXISTS matches CASCADE;
            DROP TABLE IF EXISTS teams CASCADE;
            DROP TABLE IF EXISTS stadiums CASCADE;
            DROP TABLE IF EXISTS users CASCADE;

            -- 2. CREATE TABLE STRUCTURE
            CREATE TABLE users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE,
                password VARCHAR(50),
                role VARCHAR(20) DEFAULT 'USER'
            );

            CREATE TABLE teams (
                id SERIAL PRIMARY KEY,
                name VARCHAR(50) UNIQUE
            );

            CREATE TABLE stadiums (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) UNIQUE,
                location VARCHAR(100)
            );

            CREATE TABLE matches (
                id SERIAL PRIMARY KEY,
                home_team_id INT REFERENCES teams(id),
                away_team_id INT REFERENCES teams(id),
                stadium_id INT REFERENCES stadiums(id),
                match_date VARCHAR(20),
                price DECIMAL(10, 2),
                image_url VARCHAR(500) 
            );

            CREATE TABLE transactions (
                id SERIAL PRIMARY KEY,
                username VARCHAR(100),
                match_id INT REFERENCES matches(id) ON DELETE CASCADE,
                zone_name VARCHAR(50),
                seat_nr INT,
                price DECIMAL(10, 2)
            );

            -- 3. CREATE THE VIEW
            CREATE VIEW match_details AS
            SELECT 
                m.id,
                t1.name AS team_home,
                t2.name AS team_away,
                s.name AS stadium,
                s.location,
                m.match_date,
                m.price,
                m.image_url 
            FROM matches m
            JOIN teams t1 ON m.home_team_id = t1.id
            JOIN teams t2 ON m.away_team_id = t2.id
            JOIN stadiums s ON m.stadium_id = s.id;

            -- 4. INSERT USERS
            INSERT INTO users (username, password, role) VALUES 
            ('Andrei_Adminu', 'Observator1', 'ADMIN'),
            ('Donald_Trump', 'maga2025', 'USER'),
            ('John_Cena', 'cantseeme', 'USER'),
            ('Elon_Musk', 'mars_mission', 'USER'),
            ('Lionel_Messi', 'goat_10', 'USER'),
            ('Cristiano_Ronaldo', 'siuuu_7', 'USER');

            -- 5. INSERT TEAMS
            INSERT INTO teams (name) VALUES 
            ('Real Madrid'), ('Barcelona'), ('Liverpool'), ('Chelsea'),
            ('CS Iernut'), ('ACS Muresul Ludus'), ('FCSB'), ('Dinamo Bucuresti'),
            ('Romania'), ('Hungary'), ('Argentina'), ('France'), ('Brazil'), ('Germany'), ('Italy'), ('Spain'),
            ('Manchester City'), ('Arsenal'), ('Bayern Munich'), ('Dortmund'), ('AC Milan'), ('Inter Milan'),
            ('PSG'), ('Marseille'), ('Juventus'), ('Napoli'),
            ('Manchester United'), ('Leeds United'), ('Boca Juniors'), ('River Plate'), ('Inter Miami'), ('LA Galaxy');

            -- 6. INSERT STADIUMS
            INSERT INTO stadiums (name, location) VALUES 
            ('Santiago Bernabeu', 'Madrid, Spain'),
            ('Anfield', 'Liverpool, UK'),
            ('Stadionul Comunal', 'Iernut, Romania'),
            ('Arena Nationala', 'Bucharest, Romania'),
            ('Puskas Arena', 'Budapest, Hungary'),
            ('Lusail Stadium', 'Lusail, Qatar'),
            ('Wembley', 'London, UK'),
            ('Allianz Arena', 'Munich, Germany'),
            ('San Siro', 'Milan, Italy'),
            ('Parc des Princes', 'Paris, France'),
            ('Allianz Stadium', 'Turin, Italy'),
            ('Old Trafford', 'Manchester, UK'),
            ('La Bombonera', 'Buenos Aires, Argentina'),
            ('DRV PNK Stadium', 'Florida, USA');

            -- 7. INSERT MATCHES (The full list)
            INSERT INTO matches (home_team_id, away_team_id, stadium_id, match_date, price, image_url) VALUES 
            (1, 2, 1, '2025-06-01', 150.00, 'https://upload.wikimedia.org/wikipedia/en/5/56/Real_Madrid_CF.svg'),
            (3, 4, 2, '2025-06-05', 120.00, 'https://upload.wikimedia.org/wikipedia/en/0/0c/Liverpool_FC.svg'),
            (5, 6, 3, '2025-06-10', 20.00, 'https://upload.wikimedia.org/wikipedia/commons/8/87/CS_Iernut_logo.png'),
            (7, 8, 4, '2025-06-15', 50.00, 'https://upload.wikimedia.org/wikipedia/en/9/97/FCSB_logo.svg'),
            (9, 10, 5, '2025-07-01', 80.00, 'https://upload.wikimedia.org/wikipedia/en/2/23/Romania_national_football_team_logo.svg'),
            (11, 12, 6, '2025-07-04', 300.00, 'https://upload.wikimedia.org/wikipedia/en/f/f7/Argentine_Football_Association_logo.svg'),
            (13, 14, 7, '2025-07-08', 250.00, 'https://upload.wikimedia.org/wikipedia/en/9/99/Brazilian_Football_Confederation_logo.svg'),
            (15, 16, 9, '2025-07-12', 200.00, 'https://upload.wikimedia.org/wikipedia/en/7/77/Italian_Football_Federation_logo.svg'),
            (17, 18, 7, '2025-08-01', 180.00, 'https://upload.wikimedia.org/wikipedia/en/e/eb/Manchester_City_FC_badge.svg'),
            (4, 18, 2, '2025-08-05', 160.00, 'https://upload.wikimedia.org/wikipedia/en/c/cc/Chelsea_FC.svg'),
            (27, 28, 12, '2025-08-10', 95.00, 'https://upload.wikimedia.org/wikipedia/en/7/7a/Manchester_United_FC_crest.svg'),
            (19, 20, 8, '2025-08-15', 140.00, 'https://upload.wikimedia.org/wikipedia/en/1/1b/FC_Bayern_München_logo_%282017%29.svg'),
            (21, 22, 9, '2025-08-20', 130.00, 'https://upload.wikimedia.org/wikipedia/commons/d/d0/Logo_of_AC_Milan.svg'),
            (25, 26, 11, '2025-08-25', 100.00, 'https://upload.wikimedia.org/wikipedia/commons/b/bc/Juventus_FC_2017_icon_%28black%29.svg'),
            (23, 24, 10, '2025-09-01', 110.00, 'https://upload.wikimedia.org/wikipedia/en/a/a7/Paris_Saint-Germain_F.C..svg'),
            (29, 30, 13, '2025-09-05', 100.00, 'https://upload.wikimedia.org/wikipedia/commons/4/41/Boca_Juniors_logo18.svg'),
            (31, 32, 14, '2025-09-10', 250.00, 'https://upload.wikimedia.org/wikipedia/en/5/5c/Inter_Miami_CF_logo.svg'),
            (1, 19, 1, '2025-09-15', 180.00, 'https://upload.wikimedia.org/wikipedia/en/5/56/Real_Madrid_CF.svg'),
            (2, 23, 1, '2025-09-20', 170.00, 'https://upload.wikimedia.org/wikipedia/en/4/47/FC_Barcelona_%28crest%29.svg'),
            (3, 17, 2, '2025-09-25', 160.00, 'https://upload.wikimedia.org/wikipedia/en/0/0c/Liverpool_FC.svg'),
            (9, 15, 4, '2025-10-01', 70.00, 'https://upload.wikimedia.org/wikipedia/en/2/23/Romania_national_football_team_logo.svg'),
            (11, 13, 6, '2025-10-05', 280.00, 'https://upload.wikimedia.org/wikipedia/en/f/f7/Argentine_Football_Association_logo.svg'),
            (12, 14, 8, '2025-10-10', 240.00, 'https://upload.wikimedia.org/wikipedia/en/1/18/French_Football_Federation_logo.svg'),
            (10, 16, 5, '2025-10-15', 60.00, 'https://upload.wikimedia.org/wikipedia/en/6/64/Hungarian_Football_Federation_logo.svg'),
            (31, 1, 14, '2025-10-20', 350.00, 'https://upload.wikimedia.org/wikipedia/en/5/5c/Inter_Miami_CF_logo.svg'),
            (27, 3, 12, '2025-10-25', 125.00, 'https://upload.wikimedia.org/wikipedia/en/7/7a/Manchester_United_FC_crest.svg');
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("✅ DATABASE RESTORED! All matches are back.");

        } catch (Exception e) {
            System.out.println("❌ Setup Failed. Check password/server.");
            e.printStackTrace();
        }
    }
}