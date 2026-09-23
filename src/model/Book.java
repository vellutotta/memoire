package model;

public class Book {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String coverUrl;

    // Costruttore vuoto
    public Book() {
    }

    // Costruttore a 4 parametri (usato quando l'ISBN non è disponibile)
    public Book(String id, String title, String author, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
    }

    // Costruttore completo a 5 parametri
    public Book(String id, String title, String author, String isbn, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.coverUrl = coverUrl;
    }

    // Getter e Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}