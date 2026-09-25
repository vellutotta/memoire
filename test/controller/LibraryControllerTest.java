package controller;

import dao.MockBookDAO; // Assicurati di importare la tua classe MockBookDAO
import model.ReadingInteraction;
import model.ReadingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryControllerTest {

    private LibraryController controller;

    @BeforeEach
    void setUp() {
        controller = new LibraryController(new MockBookDAO());
    }

    @Test
    void testUpdateReadingStatus_UpdatesStateCorrectly() {
        String bookId = "1";
        ReadingStatus nuovoStato = ReadingStatus.READING;

        controller.updateReadingStatus(bookId, nuovoStato);

        ReadingInteraction interazione = controller.getInteraction(bookId);

        assertNotNull(interazione, "L'interazione non dovrebbe essere nulla");
        assertEquals(nuovoStato, interazione.getStatus(), "Lo stato del libro non è stato aggiornato correttamente dal controller.");
    }

    @Test
    void testUpdateReview_SetsFinishedStatusAndDate() {
        String bookId = "2";
        int voto = 5;
        String testoRecensione = "Un capolavoro assoluto!";

        controller.updateReview(bookId, voto, testoRecensione);

        ReadingInteraction interazione = controller.getInteraction(bookId);

        assertNotNull(interazione);
        assertEquals(voto, interazione.getRating(), "Il voto non è stato salvato correttamente.");
        assertEquals(testoRecensione, interazione.getReviewText(), "Il testo della recensione non corrisponde.");
        assertEquals(ReadingStatus.FINISHED, interazione.getStatus(), "Scrivere una recensione dovrebbe impostare lo stato su FINISHED.");
        assertEquals(LocalDate.now(), interazione.getEndDate(), "La data di fine lettura non è stata impostata alla data odierna.");
    }

    @Test
    void testUpdateRating_InvalidValue_ThrowsException() {
        String bookId = "3";
        int votoInvalido = 6;

        IllegalArgumentException eccezione = assertThrows(IllegalArgumentException.class, () -> {
            controller.updateRating(bookId, votoInvalido);
        });

        assertNotNull(eccezione.getMessage());
    }
}