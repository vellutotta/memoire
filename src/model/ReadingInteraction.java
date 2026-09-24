package model;

import java.time.LocalDate;

public class ReadingInteraction {
    private String bookId;
    private String userId;
    private ReadingStatus status;
    private int rating; // 1 a 5
    private String reviewText; // Recensione testuale
    private LocalDate startDate;
    private LocalDate endDate;

    public ReadingInteraction() {}

    public ReadingInteraction(String bookId, String userId, ReadingStatus status, int rating, String reviewText, LocalDate startDate, LocalDate endDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.status = status;
        this.rating = rating;
        this.reviewText = reviewText;
        this.startDate = startDate;
        this.endDate = java.time.LocalDate.now();
    }

    public ReadingInteraction(String bookId, String userId, ReadingStatus status, int rating, String review) {
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public ReadingStatus getStatus() { return status; }
    public void setStatus(ReadingStatus status) { this.status = status; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}