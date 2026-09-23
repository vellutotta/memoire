package controller;

import dao.BookDAO;
import dao.MockBookDAO;
import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.util.List;

public class LibraryController {

    private final BookDAO bookDAO;
    private final String currentUserId = "user1"; // Utente fittizio per il test

    public LibraryController() {
        this.bookDAO = new MockBookDAO();
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public List<Book> getBooksByStatus(ReadingStatus status) {
        return bookDAO.getBooksByStatus(currentUserId, status);
    }

    public ReadingInteraction getInteraction(String bookId) {
        return bookDAO.getInteraction(bookId, currentUserId);
    }

    public void updateReadingStatus(String bookId, ReadingStatus status) {
        bookDAO.updateReadingStatus(bookId, currentUserId, status);
    }

    public void updateRating(String bookId, int rating) {
        bookDAO.updateRating(bookId, currentUserId, rating);
    }
}