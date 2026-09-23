package model;

import java.time.LocalDate;

public class ReadingInteraction {
    private String bookId;
    private String userId;
    private ReadingStatus status;
    private int rating; // Valore da 1 a 5
    private LocalDate startDate;
    private LocalDate endDate;

    public ReadingInteraction() {
    }

    public ReadingInteraction(String bookId, String userId, ReadingStatus status, int rating, LocalDate startDate, LocalDate endDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.status = status;
        this.rating = rating;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ReadingStatus getStatus() {
        return status;
    }

    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}