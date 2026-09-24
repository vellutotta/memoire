import dao.BookDAO;
import dao.BookDAOImpl;
import database.DatabaseManager;
import model.Book;
import model.ReadingStatus;
import api.OpenLibraryClient; // Nuovo import per l'API

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST CONNESSIONE E DAO ===");

        // 0. Inseriamo un utente di prova "U1" per soddisfare il vincolo di Foreign Key
        creaUtenteDiProvaSeNonEsiste("U1", "Mario Rossi", "mario@email.com");

        BookDAO bookDAO = new BookDAOImpl();

        // 1. Creazione e salvataggio di un libro di prova
        Book nuovoLibro = new Book("B101", "Il Nome della Rosa", "Umberto Eco", "copertina.jpg");

        System.out.println("\n1. Salvataggio nuovo libro...");
        bookDAO.addBook(nuovoLibro);

        // 2. Ricerca del libro per ID
        System.out.println("\n2. Ricerca del libro per ID (B101)...");
        Book libroTrovato = bookDAO.getBookById("B101");
        if (libroTrovato != null) {
            System.out.println(" Successo! Trovato: " + libroTrovato.getTitle() + " - " + libroTrovato.getAuthor());
        } else {
            System.out.println(" Libro non trovato.");
        }

        // 3. Stampa di tutti i libri presenti nel DB
        System.out.println("\n3. Lista di tutti i libri nel Database:");
        List<Book> tuttiILibri = bookDAO.getAllBooks();
        for (Book b : tuttiILibri) {
            System.out.println(" - [" + b.getId() + "] " + b.getTitle() + " (" + b.getAuthor() + ")");
        }

        // 4. Test salvataggio interazione utente (Stato e Voto)
        System.out.println("\n4. Aggiornamento stato di lettura e voto utente...");
        bookDAO.updateReadingStatus("B101", "U1", ReadingStatus.READING);
        bookDAO.updateRating("B101", "U1", 5);

        // 5. Test integrazione Open Library
        System.out.println("\n5. Test ricerca API Open Library...");
        OpenLibraryClient apiClient = new OpenLibraryClient();
        List<Book> risultati = apiClient.searchBooks("Il signore degli anelli");
        for (Book b : risultati) {
            System.out.println(" - Trovato: " + b.getTitle() + " di " + b.getAuthor() + " (ID: " + b.getId() + ")");
        }

        System.out.println("\n=== TEST COMPLETATO CON SUCCESSO! ===");
    }

    // Helper per inserire l'utente di prova U1
    private static void creaUtenteDiProvaSeNonEsiste(String id, String username, String email) {
        String sql = "INSERT INTO Users (id, username, email) VALUES (?, ?, ?) ON CONFLICT (id) DO NOTHING";
        try (Connection conn = DatabaseManager.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}