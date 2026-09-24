package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void testFullConstructorAndGetters() {
        // Creazione di un libro usando il costruttore completo
        Book book = new Book("1", "Assistant to the Villain", "Hannah Nicole Maehrer", "978-1234567890", "https://cover.jpg");

        // Verifica che tutti i getter restituiscano i valori corretti
        assertEquals("1", book.getId());
        assertEquals("Assistant to the Villain", book.getTitle());
        assertEquals("Hannah Nicole Maehrer", book.getAuthor());
        assertEquals("978-1234567890", book.getIsbn());
        assertEquals("https://cover.jpg", book.getCoverUrl());
    }

    @Test
    void testSettersAndEmptyConstructor() {
        // Creazione di un libro vuoto
        Book book = new Book();

        // Modifica dei campi tramite i Setter
        book.setId("2");
        book.setTitle("Nocticadia");
        book.setAuthor("Keri Lake");
        book.setIsbn("978-0987654321");
        book.setCoverUrl("https://nocticadia.jpg");

        // Verifica aggiornamenti
        assertEquals("2", book.getId());
        assertEquals("Nocticadia", book.getTitle());
        assertEquals("Keri Lake", book.getAuthor());
        assertEquals("978-0987654321", book.getIsbn());
        assertEquals("https://nocticadia.jpg", book.getCoverUrl());
    }
}