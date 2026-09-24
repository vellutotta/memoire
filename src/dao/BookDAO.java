package dao;

import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.util.List;

public interface BookDAO {

    //recupera tutti i libri presenti nella libreria
    List<Book> getAllBooks();

    //cerca libri per titolo o autore
    List<Book> searchBooks(String query);

    //recupera un singolo libro tramite ID
    Book getBookById(String bookId);

    //salva un nuovo libro nella libreria
    void addBook(Book book);

    void rimuoviLibroDaLibreria(String bookId);

    //recupera l'interazione dell'utente con un determinato libro (stato, rating)
    ReadingInteraction getInteraction(String bookId, String userId);

    //aggiorna lo stato di lettura di un libro (UNREAD, READING, FINISHED)
    void updateReadingStatus(String bookId, String userId, ReadingStatus status);

    //aggiorna la valutazione in stelle (da 1 a 5)
    void updateRating(String bookId, String userId, int rating);

    void updateReview(String bookId, String userId, int rating, String reviewText);

    //filtra i libri in base allo stato di lettura
    List<Book> getBooksByStatus(String userId, ReadingStatus status);
}