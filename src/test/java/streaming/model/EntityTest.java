package streaming.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    private static final class TestEntity extends Entity {
        TestEntity(String id) {
            super(id, LocalDateTime.now());
        }
    }

    @Test
    void equalsAndHashCodeAreBasedOnId() {
        Entity a1 = new TestEntity("A1");
        Entity a1b = new TestEntity("A1");
        Entity a2 = new TestEntity("A2");

        assertEquals(a1, a1b);
        assertEquals(a1.hashCode(), a1b.hashCode());
        assertNotEquals(a1, a2);
    }
}

