package dao;

import database.DatabaseManager;
import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDAOImplTest {

    private BookDAO bookDAO;

    @BeforeEach
    void setUp() {
        bookDAO = new BookDAOImpl();
        // Pulisce eventuali residui da test precedenti prima di iniziare
        puliscieDatabase();
        // Crea l'utente di prova necessario per le relazioni
        creaUtenteDiProvaSeNonEsiste("U1", "Mario Test", "test@email.com");
    }

    @AfterEach
    void tearDown() {
        // Pulisce il database alla fine di ogni test
        puliscieDatabase();
    }

    private void puliscieDatabase() {
        String deleteInteractions = "DELETE FROM User_Books WHERE book_id LIKE 'TEST-%'";
        String deleteBooks = "DELETE FROM Books WHERE id LIKE 'TEST-%'";

        try (PreparedStatement stmt1 = DatabaseManager.getInstance().prepareStatement(deleteInteractions);
             PreparedStatement stmt2 = DatabaseManager.getInstance().prepareStatement(deleteBooks)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void creaUtenteDiProvaSeNonEsiste(String id, String username, String email) {
        String sql = "INSERT INTO Users (id, username, email) VALUES (?, ?, ?) ON CONFLICT (id) DO NOTHING";
        try (PreparedStatement stmt = DatabaseManager.getInstance().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAddAndGetBookById() {
        Book book = new Book("TEST-999", "Test Title", "Test Author", "http://example.com/cover.jpg");
        bookDAO.addBook(book);

        Book retrieved = bookDAO.getBookById("TEST-999");
        assertNotNull(retrieved, "Il libro dovrebbe essere trovato");
        assertEquals("Test Title", retrieved.getTitle());
    }

    @Test
    void testSearchBooks() {
        Book book = new Book("TEST-A1", "Java Programming", "John Doe", "http://example.com/cover.jpg");
        bookDAO.addBook(book);

        List<Book> results = bookDAO.searchBooks("Java");
        assertFalse(results.isEmpty(), "La ricerca dovrebbe trovare dei libri");
    }

    @Test
    void testUserInteraction() {
        Book book = new Book("TEST-B1", "Database Systems", "Jane Doe", "http://example.com/cover.jpg");
        bookDAO.addBook(book);

        bookDAO.updateReadingStatus("TEST-B1", "U1", ReadingStatus.FINISHED);
        bookDAO.updateReview("TEST-B1", "U1", 5, "Ottimo libro!");

        ReadingInteraction interaction = bookDAO.getInteraction("TEST-B1", "U1");
        assertNotNull(interaction, "L'interazione dovrebbe esistere");
        assertEquals(ReadingStatus.FINISHED, interaction.getStatus(), "Lo stato deve essere FINISHED");
        assertEquals(5, interaction.getRating());
        assertEquals("Ottimo libro!", interaction.getReview());
    }
}