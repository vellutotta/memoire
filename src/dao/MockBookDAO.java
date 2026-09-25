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
        return books.stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null);
    }

    @Override
    public void addBook(Book book) {
        if (book != null) books.add(book);
    }

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
    public void updateReadingStatus(String bookId, String userId, ReadingStatus status, LocalDate selectedDate) {
        ReadingInteraction interaction = getInteraction(bookId, userId);
        interaction.setStatus(status);
        if (status == ReadingStatus.FINISHED) {
            interaction.setEndDate(selectedDate != null ? selectedDate : LocalDate.now());
        }
    }

    @Override
    public void updateReview(String bookId, String userId, int rating, String reviewText, LocalDate selectedDate) {
        ReadingInteraction interaction = getInteraction(bookId, userId);
        interaction.setRating(rating);
        interaction.setReviewText(reviewText);
        interaction.setStatus(ReadingStatus.FINISHED);
        interaction.setEndDate(selectedDate != null ? selectedDate : LocalDate.now());
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

        return books.stream().filter(b -> bookIdsWithStatus.contains(b.getId())).collect(Collectors.toList());
    }

    @Override
    public void rimuoviLibroDaLibreria(String bookId) {
        //implementazione vuota per i test mock
    }
}