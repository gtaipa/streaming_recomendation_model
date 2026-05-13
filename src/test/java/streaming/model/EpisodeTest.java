package streaming.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpisodeTest {

    @Test
    void setRatingRejectsOutOfRangeValues() {
        Episode e = new Episode(1, 1, "Pilot", 42, 5.0f);

        assertThrows(IllegalArgumentException.class, () -> e.setRating(-0.1f));
        assertThrows(IllegalArgumentException.class, () -> e.setRating(10.1f));
    }

    @Test
    void setRatingAcceptsBoundaryValues() {
        Episode e = new Episode(1, 1, "Pilot", 42, 5.0f);

        e.setRating(0.0f);
        assertEquals(0.0f, e.getRating());

        e.setRating(10.0f);
        assertEquals(10.0f, e.getRating());
    }
}

