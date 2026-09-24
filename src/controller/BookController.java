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

    //cerca i libri online via API
    public List<Book> cercaLibriOnline(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return apiClient.searchBooks(query);
    }

    //salva un libro nel DB
    public void salvaLibroNellaLibreria(Book book) {
        bookDAO.addBook(book);
    }

    //rimuove un libro dal DB
    public void rimuoviLibro(String bookId) {
        bookDAO.rimuoviLibroDaLibreria(bookId);
    }

    //recupera tutti i libri salvati nel DB
    public List<Book> getLibreriaUtente() {
        return bookDAO.getAllBooks();
    }
}