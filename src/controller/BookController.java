package controller;

import api.OpenLibraryClient;
import dao.BookDAO;
import dao.BookDAOImpl;
import model.Book;

import java.util.List;

public class BookController {
    private final BookDAO bookDAO;
    private final OpenLibraryClient apiClient;

    public BookController() {
        this.bookDAO = new BookDAOImpl();
        this.apiClient = new OpenLibraryClient();
    }

    // Cerca i libri online via API
    public List<Book> cercaLibriOnline(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return apiClient.searchBooks(query);
    }

    // Salva un libro nel Database PostgreSQL
    public void salvaLibroNellaLibreria(Book book) {
        bookDAO.addBook(book);
    }

    // Recupera tutti i libri salvati nel Database
    public List<Book> getLibreriaUtente() {
        return bookDAO.getAllBooks();
    }
}