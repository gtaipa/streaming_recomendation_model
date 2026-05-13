package streaming.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Genero de conteudo (ex.: "Drama", "Comedy").
 * <p>
 * Igualdade e {@code hashCode} baseados no {@code id}.
 */
public class Genre implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Identificador unico do genero. */
    private final String id;
    /** Nome do genero. */
    private String name;

    /**
     * Cria um genero.
     *
     * @param id identificador unico
     * @param name nome do genero
     */
    public Genre(String id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    /** @return id do genero */
    public String getId() { return id; }

    /** @return nome do genero */
    public String getName() { return name; }
    /** @param name novo nome do genero */
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
