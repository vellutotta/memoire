-- Tabella degli Utenti
CREATE TABLE Users (
                       ID SERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL
);

-- Tabella dei Libri
CREATE TABLE Books (
                       ID VARCHAR(50) PRIMARY KEY, -- Usiamo VARCHAR perché se in futuro usate le API, gli ID dei libri sono spesso codici (es. ISBN)
                       titolo VARCHAR(255) NOT NULL,
                       autore VARCHAR(100),
                       copertina VARCHAR(255) -- Qui salveremo l'URL dell'immagine di copertina
);

-- Tabella Ponte (La libreria personale dell'utente)
CREATE TABLE User_Books (
                            ID_Utente INT REFERENCES Users(ID),
                            ID_Libro VARCHAR(50) REFERENCES Books(ID),
                            stato VARCHAR(20) NOT NULL, -- Es: 'UNREAD', 'READING', 'FINISHED'
                            rating INT CHECK (rating >= 1 AND rating <= 5), -- Vincolo per le 5 stelle
                            data_inizio DATE,
                            data_fine DATE,
                            PRIMARY KEY (ID_Utente, ID_Libro)
);