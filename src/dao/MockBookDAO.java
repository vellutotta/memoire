package dao;

import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockBookDAO implements BookDAO {

    private final List<Book> books = new ArrayList<>();
    private final List<ReadingInteraction> interactions = new ArrayList<>();

    public MockBookDAO() {
        // Libri di prova con URL copertine reali
        books.add(new Book("1", "Assistant to the Villain", "Hannah Nicole Maehrer", "9781649374042", "https://m.media-amazon.com/images/I/8123SAt8bVL._AC_UF1000,1000_QL80_.jpg"));
        books.add(new Book("2", "Dire Bound", "Sable Sorensen", "9781777222111", "https://m.media-amazon.com/images/I/810R19+NInL._AC_UF1000,1000_QL80_.jpg"));
        books.add(new Book("3", "Nocticadia", "Keri Lake", "9781958861059", "https://m.media-amazon.com/images/I/81mD3aCqBTL._AC_UF1000,1000_QL80_.jpg"));
        books.add(new Book("4", "Starside", "Alex Aster", "9780593532881", "https://m.media-amazon.com/images/I/81TAn2aAnpL._AC_UF1000,1000_QL80_.jpg"));
        books.add(new Book("5", "Red Rising", "Pierce Brown", "9780345539786", "https://m.media-amazon.com/images/I/8192i7J2mPL._AC_UF1000,1000_QL80_.jpg"));

        // Interazioni di prova con recensioni già scritte
        interactions.add(new ReadingInteraction("1", "user1", ReadingStatus.FINISHED, 5, "Un libro fantastico! Personaggi ben scritti e trama avvincente.", LocalDate.now().minusDays(30), LocalDate.now().minusDays(5)));
        interactions.add(new ReadingInteraction("2", "user1", ReadingStatus.READING, 4, "Buon ritmo iniziale, sono molto curioso di vedere come prosegue.", LocalDate.now().minusDays(10), null));
    }

    @Override
    public List<Book> getAllBooks() { return new ArrayList<>(books); }

    @Override
    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) return getAllBooks();
        String lowerQuery = query.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery) || b.getAuthor().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @Override
    public Book getBookById(String bookId) {
        return books.stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null);
    }

    @Override
    public void addBook(Book book) { if (book != null) books.add(book); }

    @Override
    public ReadingInteraction getInteraction(String bookId, String userId) {
        return interactions.stream()
                .filter(i -> i.getBookId().equals(bookId) && i.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> {
                    ReadingInteraction newInteraction = new ReadingInteraction(bookId, userId, ReadingStatus.UNREAD, 0, "", null, null);
                    interactions.add(newInteraction);
                    return newInteraction;
                });
    }

    @Override
    public void updateReadingStatus(String bookId, String userId, ReadingStatus status) {
        ReadingInteraction interaction = getInteraction(bookId, userId);
        interaction.setStatus(status);
    }

    @Override
    public void updateRating(String bookId, String userId, int rating) {
        ReadingInteraction interaction = getInteraction(bookId, userId);
        interaction.setRating(rating);
    }

    @Override
    public void updateReview(String bookId, String userId, int rating, String reviewText) {
        ReadingInteraction interaction = getInteraction(bookId, userId);
        interaction.setRating(rating);
        interaction.setReviewText(reviewText);
        interaction.setStatus(ReadingStatus.FINISHED);
        interaction.setEndDate(java.time.LocalDate.now());
    }

    @Override
    public List<Book> getBooksByStatus(String userId, ReadingStatus status) {
        List<String> bookIdsWithStatus = interactions.stream()
                .filter(i -> i.getUserId().equals(userId) && i.getStatus() == status)
                .map(ReadingInteraction::getBookId)
                .collect(Collectors.toList());

        return books.stream().filter(b -> bookIdsWithStatus.contains(b.getId())).collect(Collectors.toList());
    }
}