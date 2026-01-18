-- DATABASE BACKUP SCRIPT
-- RUN THIS TO RECREATE THE FULL PROJECT DATABASE

-- 1. CLEANUP (Start Fresh)
DROP VIEW IF EXISTS match_details CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS matches CASCADE;
DROP TABLE IF EXISTS teams CASCADE;
DROP TABLE IF EXISTS stadiums CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 2. CREATE ACADEMIC TABLES (5 Tables)

-- Table 1: Users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE,
                       password VARCHAR(50),
                       role VARCHAR(20) DEFAULT 'USER'
);

-- Table 2: Teams (Stores Name & Logo)
CREATE TABLE teams (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE,
                       logo_url VARCHAR(255)
);

-- Table 3: Stadiums (Stores Name & Location)
CREATE TABLE stadiums (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) UNIQUE,
                          location VARCHAR(100)
);

-- Table 4: Matches (Links Teams & Stadiums via IDs - 3NF Compliant)
CREATE TABLE matches (
                         id SERIAL PRIMARY KEY,
                         home_team_id INT REFERENCES teams(id),
                         away_team_id INT REFERENCES teams(id),
                         stadium_id INT REFERENCES stadiums(id),
                         match_date VARCHAR(20),
                         price DECIMAL(10, 2)
);

-- Table 5: Transactions (Links to Matches)
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              username VARCHAR(100),
                              match_id INT REFERENCES matches(id) ON DELETE CASCADE,
                              zone_name VARCHAR(50),
                              seat_nr INT,
                              price DECIMAL(10, 2)
);

-- 3. CREATE THE VIEW (Virtual Table for Java)
CREATE VIEW match_details AS
SELECT
    m.id,
    t1.name AS team_home,
    t2.name AS team_away,
    s.name AS stadium,
    s.location,
    m.match_date,
    m.price,
    t1.logo_url AS image_url
FROM matches m
         JOIN teams t1 ON m.home_team_id = t1.id
         JOIN teams t2 ON m.away_team_id = t2.id
         JOIN stadiums s ON m.stadium_id = s.id;

-- 4. INSERT DEFAULT DATA

-- Admin User
INSERT INTO users (username, password, role) VALUES ('Andrei_Adminu', 'Observator1', 'ADMIN');

-- Sample Teams
INSERT INTO teams (name, logo_url) VALUES
                                       ('Real Madrid', 'https://upload.wikimedia.org/wikipedia/en/5/56/Real_Madrid_CF.svg'),
                                       ('Barcelona', 'https://upload.wikimedia.org/wikipedia/en/4/47/FC_Barcelona_%28crest%29.svg'),
                                       ('Liverpool', 'https://upload.wikimedia.org/wikipedia/en/0/0c/Liverpool_FC.svg'),
                                       ('Chelsea', 'https://upload.wikimedia.org/wikipedia/en/c/cc/Chelsea_FC.svg');

-- Sample Stadiums
INSERT INTO stadiums (name, location) VALUES
                                          ('Santiago Bernabeu', 'Madrid, Spain'),
                                          ('Anfield', 'Liverpool, UK');

-- Sample Matches
INSERT INTO matches (home_team_id, away_team_id, stadium_id, match_date, price) VALUES
                                                                                    (1, 2, 1, '2025-05-20', 150.00), -- Real vs Barca
                                                                                    (3, 4, 2, '2025-06-15', 80.00);  -- Liverpool vs Chelsea