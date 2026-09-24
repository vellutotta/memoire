package controller;

import dao.BookDAO;
import dao.BookDAOImpl;
import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.util.List;

public class LibraryController {

    private final BookDAO bookDAO;
    private final String currentUserId = "user1";

    // Costruttore di default (utilizzato dall'applicazione con PostgreSQL)
    public LibraryController() {
        this.bookDAO = new BookDAOImpl();
    }

    // NUOVO: Costruttore per i Test (permette di iniettare MockBookDAO)
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

    public void updateReadingStatus(String bookId, ReadingStatus status) {
        bookDAO.updateReadingStatus(bookId, currentUserId, status);
    }

    public void updateRating(String bookId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5.");
        }

        bookDAO.updateRating(bookId, currentUserId, rating);
    }

    public void updateReview(String bookId, int rating, String reviewText) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5.");
        }

        bookDAO.updateReview(bookId, currentUserId, rating, reviewText);
        ReadingInteraction inter = getInteraction(bookId);
        if (inter != null) {
            inter.setRating(rating);
            inter.setReviewText(reviewText);
            inter.setStatus(ReadingStatus.FINISHED);

            if (inter.getEndDate() == null) {
                inter.setEndDate(java.time.LocalDate.now());
            }
        }
    }

    //collegato il metodo di rimozione al DAO
    public void removeInteraction(String bookId) {
        bookDAO.rimuoviLibroDaLibreria(bookId);
    }

    public void rimuoviLibro(String bookId) {
        bookDAO.rimuoviLibroDaLibreria(bookId);
    }
}