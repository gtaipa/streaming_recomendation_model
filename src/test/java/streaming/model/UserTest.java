package streaming.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void followDoesNotDuplicateUsers() {
        User u1 = new User("U1", LocalDateTime.now(), "u1", "u1@example.com", "hash");
        User u2 = new User("U2", LocalDateTime.now(), "u2", "u2@example.com", "hash");

        u1.follow(u2);
        u1.follow(u2);

        assertEquals(1, u1.getFollowing().size());
        assertSame(u2, u1.getFollowing().get(0));
    }
}

