package streaming.model;

import java.time.LocalDateTime;

public class Genre extends Entity {
    private String name;

    public Genre(String id, LocalDateTime createdAt, String name) {
        super(id, createdAt);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Genre: " + name;
    }
}