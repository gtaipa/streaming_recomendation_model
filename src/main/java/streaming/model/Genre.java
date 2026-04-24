package streaming.model;

import java.io.Serializable;
import java.util.Objects;

public class Genre implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;

    public Genre(String id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNull(name, "name"); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "Genre{id='" + id + "', name='" + name + "'}";
    }
}
