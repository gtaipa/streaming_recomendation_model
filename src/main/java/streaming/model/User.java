package streaming.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilizador da plataforma de streaming.
 * <p>
 * Contem dados de perfil, credenciais (hash) e ligacoes sociais (following).
 */

public class User extends Entity {
    /** Nome de utilizador (username). */
    private String username;
    /** Email do utilizador. */
    private String email;
    /** Hash da password (nunca guardar password em claro). */
    private String passwordHash;
    /** Regiao do utilizador (ex.: "PT", "US"). */
    private String region;
    /** Data de registo do utilizador. */
    private LocalDate registrationDate;
    /** Preferencias por genero. */
    private List<Genre> preferences;
    /** Lista de utilizadores seguidos por este utilizador. */
    private List<User> following;

    /**
     * Cria um utilizador.
     *
     * @param id identificador unico
     * @param createdAt data/hora de criacao
     * @param username username
     * @param email email
     * @param passwordHash hash da password
     */
    public User(String id, LocalDateTime createdAt, String username, String email, String passwordHash) {
        // 1º Passo: Satisfazer o construtor da classe mãe (Entity)
        super(id, createdAt);
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.preferences = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    /**
     * Segue outro utilizador (se ainda nao estiver a seguir).
     *
     * @param u utilizador a seguir
     */
    public void follow(User u) {
        if (u != null && !this.following.contains(u)) {
            this.following.add(u);
        }
    }

    /** @return lista de utilizadores seguidos */
    public List<User> getFollowing() {
        return following;
    }

    /** @return lista de preferencias por genero */
    public List<Genre> getPreferences() {
        return preferences;
    }

    /** @return hash da password */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @return username */
    public String getUsername() {
        return username;
    }

    /** @param username novo username */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return email */
    public String getEmail() {
        return email;
    }

    /** @param email novo email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @param passwordHash novo hash da password */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** @return regiao */
    public String getRegion() {
        return region;
    }

    /** @param region nova regiao */
    public void setRegion(String region) {
        this.region = region;
    }

    /** @return data de registo */
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    /** @param registrationDate nova data de registo */
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    /** @return representacao textual do utilizador */
    @Override
    public String toString() {
        return "User:" + username + ",E-mail:" + email;
    }
}
