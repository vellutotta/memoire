package controller;

import model.ReadingInteraction;
import model.ReadingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryControllerTest {

    private LibraryController controller;

    @BeforeEach
    void setUp() {
        // Inizializza un nuovo controller prima di ogni test.
        // Utilizzerà automaticamente il tuo MockBookDAO come definito nel costruttore.
        controller = new LibraryController();
    }

    // 1. Test: Cosa succede se sposto un libro in un nuovo stato di lettura?
    @Test
    void testUpdateReadingStatus_UpdatesStateCorrectly() {
        // Assumiamo che il MockBookDAO abbia un libro con ID "1"
        String bookId = "1";
        ReadingStatus nuovoStato = ReadingStatus.READING; // o un altro valore del tuo Enum

        // Azione: il controller aggiorna lo stato
        controller.updateReadingStatus(bookId, nuovoStato);

        // Verifica: recuperiamo l'interazione e controlliamo che lo stato sia cambiato
        ReadingInteraction interazione = controller.getInteraction(bookId);

        assertNotNull(interazione, "L'interazione non dovrebbe essere nulla");
        assertEquals(nuovoStato, interazione.getStatus(), "Lo stato del libro non è stato aggiornato correttamente dal controller.");
    }

    // 2. Test: Verifica del flusso della recensione (cambia lo stato in FINISHED e imposta la data)
    @Test
    void testUpdateReview_SetsFinishedStatusAndDate() {
        String bookId = "2"; // Usiamo un altro ID fittizio per questo test
        int voto = 5;
        String testoRecensione = "Un capolavoro assoluto!";

        // Azione: salviamo una recensione tramite il controller
        controller.updateReview(bookId, voto, testoRecensione);

        // Verifica: controlliamo che la logica nel controller abbia fatto il suo dovere
        ReadingInteraction interazione = controller.getInteraction(bookId);

        assertNotNull(interazione);
        assertEquals(voto, interazione.getRating(), "Il voto non è stato salvato correttamente.");
        assertEquals(testoRecensione, interazione.getReviewText(), "Il testo della recensione non corrisponde.");

        // Verifica cruciale: lo stato deve essere diventato FINISHED automaticamente
        // (Nota: assicurati che il tuo enum contenga il valore FINISHED, altrimenti adattalo a quello reale, es. READ)
        assertEquals(ReadingStatus.FINISHED, interazione.getStatus(), "Scrivere una recensione dovrebbe impostare lo stato su FINISHED.");

        // Verifica che la data di fine sia stata impostata ad oggi
        assertEquals(LocalDate.now(), interazione.getEndDate(), "La data di fine lettura non è stata impostata alla data odierna.");
    }

    // 3. Test: Cosa succede se metto un voto fuori scala (es. 6 invece di 5)?
    @Test
    void testUpdateRating_InvalidValue_ThrowsException() {
        String bookId = "3";
        int votoInvalido = 6;

        // Verifica: controlliamo che il controller (o il DAO/Modello sottostante)
        // lanci un'eccezione se si prova a inserire un voto non consentito
        IllegalArgumentException eccezione = assertThrows(IllegalArgumentException.class, () -> {
            controller.updateRating(bookId, votoInvalido);
        });

        // Opzionale: controlla che il messaggio di errore non sia vuoto
        assertNotNull(eccezione.getMessage());
    }
}