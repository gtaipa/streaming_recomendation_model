package streaming.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void equalsAndHashCodeAreBasedOnId() {
        Genre g1 = new Genre("G1", "Drama");
        Genre g1b = new Genre("G1", "AnyName");
        Genre g2 = new Genre("G2", "Drama");

        assertEquals(g1, g1b);
        assertEquals(g1.hashCode(), g1b.hashCode());
        assertNotEquals(g1, g2);
    }
}

