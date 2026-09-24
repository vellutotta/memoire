package dao;

import database.DatabaseManager;
import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {

    private final Connection connection = DatabaseManager.getInstance();

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in getAllBooks: " + e.getMessage(), e);
        }
        return books;
    }

    @Override
    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in searchBooks: " + e.getMessage(), e);
        }
        return books;
    }

    @Override
    public Book getBookById(String bookId) {
        String sql = "SELECT * FROM Books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in getBookById: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void addBook(Book book) {
        String sql = "INSERT INTO Books (id, title, author, cover_url) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCoverUrl());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in addBook: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateReadingStatus(String bookId, String userId, ReadingStatus status) {
        String sql = "INSERT INTO User_Books (book_id, user_id, status) VALUES (?, ?, ?) " +
                "ON CONFLICT (book_id, user_id) DO UPDATE SET status = EXCLUDED.status";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            stmt.setString(3, status != null ? status.name() : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in updateReadingStatus: " + e.getMessage(), e);
        }
    }

    @Override
    public ReadingInteraction getInteraction(String bookId, String userId) {
        String sql = "SELECT * FROM User_Books WHERE book_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String statusStr = rs.getString("status");
                ReadingStatus status = null;
                if (statusStr != null && !statusStr.trim().isEmpty()) {
                    status = ReadingStatus.valueOf(statusStr.trim().toUpperCase());
                }
                int rating = rs.getInt("rating");
                String review = rs.getString("review");

                return new ReadingInteraction(bookId, userId, status, rating, review);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in getInteraction: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void updateRating(String bookId, String userId, int rating) {
        String sql = "INSERT INTO User_Books (book_id, user_id, rating) VALUES (?, ?, ?) " +
                "ON CONFLICT (book_id, user_id) DO UPDATE SET rating = EXCLUDED.rating";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            stmt.setInt(3, rating);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in updateRating: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateReview(String bookId, String userId, int rating, String reviewText) {
        String sql = "INSERT INTO User_Books (book_id, user_id, rating, review) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (book_id, user_id) DO UPDATE SET rating = EXCLUDED.rating, review = EXCLUDED.review";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            stmt.setInt(3, rating);
            stmt.setString(4, reviewText);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in updateReview: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> getBooksByStatus(String userId, ReadingStatus status) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM Books b JOIN User_Books ub ON b.id = ub.book_id " +
                "WHERE ub.user_id = ? AND ub.status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, status.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore SQL in getBooksByStatus: " + e.getMessage(), e);
        }
        return books;
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("cover_url")
        );
    }
}