package dao;

import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockBookDAO implements BookDAO {

    // Liste in memoria per simulare il database
    private final List<Book> books = new ArrayList<>();
    private final List<ReadingInteraction> interactions = new ArrayList<>();

    // Costruttore: inseriamo alcuni dati di prova fittizi (Mock)
    public MockBookDAO() {
        // Libri di prova
        books.add(new Book("1", "Il Signore degli Anelli", "J.R.R. Tolkien", "9788845292613", "https://via.placeholder.com/150"));
        books.add(new Book("2", "1984", "George Orwell", "9788804668237", "https://via.placeholder.com/150"));
        books.add(new Book("3", "I Promessi Sposi", "Alessandro Manzoni", "9788817020817", "https://via.placeholder.com/150"));

        // Interazioni di prova per l'utente "user1"
        interactions.add(new ReadingInteraction("1", "user1", ReadingStatus.READING, 4, LocalDate.now().minusDays(10), null));
        interactions.add(new ReadingInteraction("2", "user1", ReadingStatus.FINISHED, 5, LocalDate.now().minusDays(30), LocalDate.now().minusDays(5)));
    }

    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    @Override
    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return getAllBooks();
        }
        String lowerQuery = query.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery) ||
                        b.getAuthor().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @Override
    public Book getBookById(String bookId) {
        return books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addBook(Book book) {
        if (book != null) {
            books.add(book);
        }
    }

    @Override
    public ReadingInteraction getInteraction(String bookId, String userId) {
        return interactions.stream()
                .filter(i -> i.getBookId().equals(bookId) && i.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> {
                    // Se non esiste ancora un'interazione per questo libro, ne crea una predefinita
                    ReadingInteraction newInteraction = new ReadingInteraction(bookId, userId, ReadingStatus.UNREAD, 0, null, null);
                    interactions.add(newInteraction);
                    return newInteraction;
                });
    }

    @Override
    public void updateReadingStatus(String bookId, String userId, ReadingStatus status) {
        ReadingInteraction interaction = getInteraction(bookId, userId);
        interaction.setStatus(status);

        if (status == ReadingStatus.READING && interaction.getStartDate() == null) {
            interaction.setStartDate(LocalDate.now());
        } else if (status == ReadingStatus.FINISHED) {
            if (interaction.getStartDate() == null) {
                interaction.setStartDate(LocalDate.now());
            }
            interaction.setEndDate(LocalDate.now());
        }
    }

    @Override
    public void updateRating(String bookId, String userId, int rating) {
        ReadingInteraction interaction = getInteraction(bookId, userId);
        interaction.setRating(rating);
    }

    @Override
    public List<Book> getBooksByStatus(String userId, ReadingStatus status) {
        List<String> bookIdsWithStatus = interactions.stream()
                .filter(i -> i.getUserId().equals(userId) && i.getStatus() == status)
                .map(ReadingInteraction::getBookId)
                .collect(Collectors.toList());

        return books.stream()
                .filter(b -> bookIdsWithStatus.contains(b.getId()))
                .collect(Collectors.toList());
    }
}