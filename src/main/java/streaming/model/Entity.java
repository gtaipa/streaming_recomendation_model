package streaming.model;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Entidade base do dominio (identificada por {@code id}) com data/hora de criacao.
 * <p>
 * Subclasses tipicas: {@link User}, {@link Artist}, {@link Content}.
 */
public abstract class Entity {

    /**
     * Identificador unico da entidade.
     */
    protected String id;

    /**
     * Momento de criacao/registo da entidade.
     */
    protected LocalDateTime createdAt;


    /**
     * Cria uma entidade.
     *
     * @param id        identificador unico
     * @param createdAt data/hora de criacao
     */
    public Entity(String id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    /**
     * @return identificador unico da entidade
     */
    public String getId() {
        return id;
    }

    /**
     * @return data/hora de criacao/registo
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity other)) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
