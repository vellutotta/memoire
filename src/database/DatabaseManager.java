package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // 1. Definisci le credenziali (Modifica URL e PASSWORD)
    private static final String URL = "jdbc:postgresql://localhost:5432/swe_books_db"; // Cambia swe_books_db se lo hai chiamato in un altro modo
    private static final String USER = "postgres";
    private static final String PASSWORD = "heatedrivalry69"; // Inserisci qui la tua password

    // 2. La variabile statica che conterrà l'unica connessione (Pattern Singleton)
    private static Connection connection = null;

    // Costruttore privato per impedire di creare altre istanze con il "new"
    private DatabaseManager() {}

    // 3. Metodo globale per ottenere la connessione
    public static Connection getInstance() {
        try {
            if (connection == null || connection.isClosed()) {
                // Crea la connessione fisica solo la prima volta che viene richiesta
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connessione al database PostgreSQL stabilita con successo!");
            }
        } catch (SQLException e) {
            System.err.println("Errore di connessione al database!");
            e.printStackTrace();
        }
        return connection;
    }
}