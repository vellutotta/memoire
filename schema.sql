CREATE TABLE Users (
                       id VARCHAR(50) PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE Books (
                       id VARCHAR(50) PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(100),
                       cover_url VARCHAR(255)
);

--libreria personale dell'utente
CREATE TABLE User_Books (
                            user_id VARCHAR(50) REFERENCES Users(id) ON DELETE CASCADE,
                            book_id VARCHAR(50) REFERENCES Books(id) ON DELETE CASCADE,
                            status VARCHAR(20),
                            rating INT CHECK (rating >= 1 AND rating <= 5),
                            review TEXT,
                            data_inizio DATE,
                            data_fine DATE,
                            PRIMARY KEY (user_id, book_id)
);