package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Setup {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    public static void main(String[] args) {

        String sql = """
            -- 1. CLEANUP (Delete old data)
            DROP VIEW IF EXISTS match_details CASCADE;
            DROP TABLE IF EXISTS transactions CASCADE;
            DROP TABLE IF EXISTS matches CASCADE;
            DROP TABLE IF EXISTS teams CASCADE;
            DROP TABLE IF EXISTS stadiums CASCADE;
            DROP TABLE IF EXISTS users CASCADE;

            -- 2. CREATE TABLES
            CREATE TABLE users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE,
                password VARCHAR(50),
                role VARCHAR(20) DEFAULT 'USER'
            );

            CREATE TABLE teams (
                id SERIAL PRIMARY KEY,
                name VARCHAR(50) UNIQUE,
                logo_url VARCHAR(255)
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

            -- 3. CREATE VIEW
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

            -- 4. INSERT 25+ PERSONALITIES (Users)
            INSERT INTO users (username, password, role) VALUES 
            ('Andrei_Adminu', 'Observator1', 'ADMIN'),
            ('Donald_Trump', 'maga2025', 'USER'),
            ('Elon_Musk', 'mars_mission', 'USER'),
            ('Taylor_Swift', 'eras_tour', 'USER'),
            ('Dwayne_TheRock', 'cooking', 'USER'),
            ('Gordon_Ramsay', 'lamb_sauce', 'USER'),
            ('Kim_Kardashian', 'reality', 'USER'),
            ('Snoop_Dogg', 'smoke', 'USER'),
            ('Tom_Cruise', 'top_gun', 'USER'),
            ('Beyonce', 'queen_b', 'USER'),
            ('Mike_Tyson', 'iron_mike', 'USER'),
            ('Vladimir_Putin', 'kremlin', 'USER'),
            ('Joe_Biden', 'ice_cream', 'USER'),
            ('Margot_Robbie', 'barbie', 'USER'),
            ('Keanu_Reeves', 'wick', 'USER'),
            ('Ryan_Reynolds', 'deadpool', 'USER'),
            ('Conor_McGregor', 'notorious', 'USER'),
            ('Shakira', 'hips', 'USER'),
            ('Will_Smith', 'slap', 'USER'),
            ('Johnny_Depp', 'pirate', 'USER'),
            ('Leonardo_DiCaprio', 'titanic', 'USER'),
            ('Barack_Obama', 'yes_we_can', 'USER'),
            ('Kevin_Hart', 'funny', 'USER'),
            ('Rihanna', 'umbrella', 'USER'),
            ('LeBron_James', 'king', 'USER'),
            ('Eminem', 'slim_shady', 'USER');

            -- 5. INSERT 25 STADIUMS
            INSERT INTO stadiums (name, location) VALUES 
            ('Santiago Bernabeu', 'Madrid, Spain'),
            ('Camp Nou', 'Barcelona, Spain'),
            ('Anfield', 'Liverpool, UK'),
            ('Old Trafford', 'Manchester, UK'),
            ('San Siro', 'Milan, Italy'),
            ('Allianz Arena', 'Munich, Germany'),
            ('Wembley', 'London, UK'),
            ('Parc des Princes', 'Paris, France'),
            ('Stamford Bridge', 'London, UK'),
            ('Emirates Stadium', 'London, UK'),
            ('Signal Iduna Park', 'Dortmund, Germany'),
            ('Johan Cruyff Arena', 'Amsterdam, Netherlands'),
            ('Estadio da Luz', 'Lisbon, Portugal'),
            ('Wanda Metropolitano', 'Madrid, Spain'),
            ('Juventus Stadium', 'Turin, Italy'),
            ('Stadio Olimpico', 'Rome, Italy'),
            ('Velodrome', 'Marseille, France'),
            ('Etihad Stadium', 'Manchester, UK'),
            ('Tottenham Hotspur Stadium', 'London, UK'),
            ('Villa Park', 'Birmingham, UK'),
            ('St James Park', 'Newcastle, UK'),
            ('Celtic Park', 'Glasgow, Scotland'),
            ('Ibrox Stadium', 'Glasgow, Scotland'),
            ('Maracana', 'Rio, Brazil'),
            ('La Bombonera', 'Buenos Aires, Argentina');

            -- 6. INSERT TEAMS
            INSERT INTO teams (name) VALUES 
            ('Real Madrid'), ('Barcelona'), ('Liverpool'), ('Man United'), 
            ('AC Milan'), ('Bayern Munich'), ('England'), ('PSG'), 
            ('Chelsea'), ('Arsenal'), ('Dortmund'), ('Ajax'), 
            ('Benfica'), ('Atletico Madrid'), ('Juventus'), ('Roma'), 
            ('Marseille'), ('Man City'), ('Tottenham'), ('Aston Villa'),
            ('Newcastle'), ('Celtic'), ('Rangers'), ('Flamengo'), ('Boca Juniors');

            -- 7. INSERT 25 MATCHES
            INSERT INTO matches (home_team_id, away_team_id, stadium_id, match_date, price, image_url) VALUES 
            (1, 2, 1, '2025-06-01', 250.00, 'https://upload.wikimedia.org/wikipedia/en/5/56/Real_Madrid_CF.svg'),
            (2, 1, 2, '2025-06-05', 240.00, 'https://upload.wikimedia.org/wikipedia/en/4/47/FC_Barcelona_%28crest%29.svg'),
            (3, 4, 3, '2025-06-10', 180.00, 'https://upload.wikimedia.org/wikipedia/en/0/0c/Liverpool_FC.svg'),
            (4, 3, 4, '2025-06-15', 190.00, 'https://upload.wikimedia.org/wikipedia/en/7/7a/Manchester_United_FC_crest.svg'),
            (5, 15, 5, '2025-06-20', 120.00, 'https://upload.wikimedia.org/wikipedia/commons/d/d0/Logo_of_AC_Milan.svg'),
            (6, 11, 6, '2025-06-25', 150.00, 'https://upload.wikimedia.org/wikipedia/en/1/1b/FC_Bayern_München_logo_%282017%29.svg'),
            (7, 2, 7, '2025-07-01', 300.00, 'https://upload.wikimedia.org/wikipedia/en/be/England_Football_Association_crest.svg'),
            (8, 17, 8, '2025-07-05', 200.00, 'https://upload.wikimedia.org/wikipedia/en/a/a7/Paris_Saint-Germain_F.C..svg'),
            (9, 10, 9, '2025-07-10', 160.00, 'https://upload.wikimedia.org/wikipedia/en/c/cc/Chelsea_FC.svg'),
            (10, 9, 10, '2025-07-15', 165.00, 'https://upload.wikimedia.org/wikipedia/en/5/53/Arsenal_FC.svg'),
            (11, 6, 11, '2025-07-20', 140.00, 'https://upload.wikimedia.org/wikipedia/commons/6/67/Borussia_Dortmund_logo.svg'),
            (12, 13, 12, '2025-07-25', 90.00, 'https://upload.wikimedia.org/wikipedia/en/7/79/Ajax_Amsterdam.svg'),
            (13, 12, 13, '2025-08-01', 85.00, 'https://upload.wikimedia.org/wikipedia/en/a/a2/SL_Benfica_logo.svg'),
            (14, 1, 14, '2025-08-05', 170.00, 'https://upload.wikimedia.org/wikipedia/en/f/f4/Atletico_Madrid_2017_logo.svg'),
            (15, 5, 15, '2025-08-10', 130.00, 'https://upload.wikimedia.org/wikipedia/commons/b/bc/Juventus_FC_2017_icon_%28black%29.svg'),
            (16, 5, 16, '2025-08-15', 110.00, 'https://upload.wikimedia.org/wikipedia/en/f/f7/AS_Roma_logo_%282017%29.svg'),
            (17, 8, 17, '2025-08-20', 95.00, 'https://upload.wikimedia.org/wikipedia/en/8/86/Olympique_Marseille_logo.svg'),
            (18, 4, 18, '2025-08-25', 210.00, 'https://upload.wikimedia.org/wikipedia/en/e/eb/Manchester_City_FC_badge.svg'),
            (19, 10, 19, '2025-09-01', 175.00, 'https://upload.wikimedia.org/wikipedia/en/b/b4/Tottenham_Hotspur.svg'),
            (20, 21, 20, '2025-09-05', 80.00, 'https://upload.wikimedia.org/wikipedia/en/f/f9/Aston_Villa_FC_crest_%282016%29.svg'),
            (21, 20, 21, '2025-09-10', 85.00, 'https://upload.wikimedia.org/wikipedia/en/5/56/Newcastle_United_Logo.svg'),
            (22, 23, 22, '2025-09-15', 100.00, 'https://upload.wikimedia.org/wikipedia/en/5/5cf/Celtic_FC.svg'),
            (23, 22, 23, '2025-09-20', 100.00, 'https://upload.wikimedia.org/wikipedia/en/4/43/Rangers_FC.svg'),
            (24, 25, 24, '2025-09-25', 60.00, 'https://upload.wikimedia.org/wikipedia/commons/2/2e/Flamengo_braz_logo.svg'),
            (25, 24, 25, '2025-10-01', 70.00, 'https://upload.wikimedia.org/wikipedia/commons/4/41/Boca_Juniors_logo18.svg');
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("✅ DATABASE RESTORED! (25 Personalities, 25 Stadiums, 25 Matches)");

        } catch (Exception e) {
            System.out.println("❌ Database.Setup Failed. Check password/server.");
            e.printStackTrace();
        }
    }
}