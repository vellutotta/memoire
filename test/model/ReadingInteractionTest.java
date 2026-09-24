package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReadingInteractionTest {

    @Test
    void testUpdateStatusAndRating() {
        ReadingInteraction interaction = new ReadingInteraction();

        interaction.setStatus(ReadingStatus.READING);
        interaction.setRating(5);
        interaction.setReviewText("Libro eccezionale!");

        assertEquals(ReadingStatus.READING, interaction.getStatus());
        assertEquals(5, interaction.getRating());
        assertEquals("Libro eccezionale!", interaction.getReviewText());
    }
}