package controller;

import dao.BookDAO;
import dao.BookDAOImpl;
import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.time.LocalDate;
import java.util.List;

public class LibraryController {

    private final BookDAO bookDAO;
    private final String currentUserId = "user1";

    //costruttore di default (utilizzato dall'applicazione con PostgreSQL)
    public LibraryController() {
        this.bookDAO = new BookDAOImpl();
    }

    //costruttore per i Test (permette di iniettare MockBookDAO)
    public LibraryController(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public List<Book> getBooksByStatus(ReadingStatus status) {
        return bookDAO.getBooksByStatus(currentUserId, status);
    }

    public List<Book> searchBooks(String query) {
        return bookDAO.searchBooks(query);
    }

    public ReadingInteraction getInteraction(String bookId) {
        return bookDAO.getInteraction(bookId, currentUserId);
    }

    //AGGIORNAMENTO STATO DI LETTURA

    public void updateReadingStatus(String bookId, ReadingStatus status) {
        updateReadingStatus(bookId, status, LocalDate.now());
    }

    public void updateReadingStatus(String bookId, ReadingStatus status, LocalDate completionDate) {
        bookDAO.updateReadingStatus(bookId, currentUserId, status, completionDate);
    }

    //AGGIORNAMENTO VALUTAZIONE

    public void updateRating(String bookId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5.");
        }

        bookDAO.updateRating(bookId, currentUserId, rating);
    }

    //AGGIORNAMENTO RECENSIONE

    //usa la data odierna
    public void updateReview(String bookId, int rating, String reviewText) {
        updateReview(bookId, rating, reviewText, LocalDate.now());
    }

    public void updateReview(String bookId, int rating, String reviewText, LocalDate completionDate) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5.");
        }

        LocalDate dateToUse = (completionDate != null) ? completionDate : LocalDate.now();

        bookDAO.updateReview(bookId, currentUserId, rating, reviewText, dateToUse);

        ReadingInteraction inter = getInteraction(bookId);
        if (inter != null) {
            inter.setRating(rating);
            inter.setReviewText(reviewText);
            inter.setStatus(ReadingStatus.FINISHED);

            if (inter.getEndDate() == null) {
                inter.setEndDate(dateToUse);
            }
        }
    }

    public void removeInteraction(String bookId) {
        bookDAO.rimuoviLibroDaLibreria(bookId);
    }

    public void rimuoviLibro(String bookId) {
        bookDAO.rimuoviLibroDaLibreria(bookId);
    }
}