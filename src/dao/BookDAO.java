package dao;

import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.time.LocalDate;
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

    //aggiorna lo stato di lettura di un libro indicando la data selezionata
    void updateReadingStatus(String bookId, String userId, ReadingStatus status, LocalDate selectedDate);

    //aggiorna la valutazione in stelle (da 1 a 5)
    void updateRating(String bookId, String userId, int rating);

    //aggiorna la recensione e la valutazione indicando la data selezionata
    void updateReview(String bookId, String userId, int rating, String reviewText, LocalDate selectedDate);

    //filtra i libri in base allo stato di lettura
    List<Book> getBooksByStatus(String userId, ReadingStatus status);
}