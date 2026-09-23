package dao;

import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.util.List;

public interface BookDAO {

    // Recupera tutti i libri presenti nella libreria
    List<Book> getAllBooks();

    // Cerca libri per titolo o autore
    List<Book> searchBooks(String query);

    // Recupera un singolo libro tramite ID
    Book getBookById(String bookId);

    // Salva un nuovo libro nella libreria
    void addBook(Book book);

    // Recupera l'interazione dell'utente con un determinato libro (stato, rating)
    ReadingInteraction getInteraction(String bookId, String userId);

    // Aggiorna lo stato di lettura di un libro (UNREAD, READING, FINISHED)
    void updateReadingStatus(String bookId, String userId, ReadingStatus status);

    // Aggiorna la valutazione in stelle (da 1 a 5)
    void updateRating(String bookId, String userId, int rating);

    void updateReview(String bookId, String userId, int rating, String reviewText);

    // Filtra i libri in base allo stato di lettura
    List<Book> getBooksByStatus(String userId, ReadingStatus status);
}