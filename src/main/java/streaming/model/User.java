package streaming.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User extends Entity {
    private String username;
    private String email;
    private String passwordHash;
    private String region;
    private LocalDate registrationDate;
    private List<Genre> preferences;
    private List<User> following;

    public User(String id, LocalDateTime createdAt, String username, String email, String passwordHash) {
        // 1º Passo: Satisfazer o construtor da classe mãe (Entity)
        super(id, createdAt);
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.preferences = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public void follow(User u) {
        if (u != null && !this.following.contains(u)) {
            this.following.add(u);
        }
    }

    public List<User> getFollowing() {
        return following;
    }

    public List<Genre> getPreferences() {
        return preferences;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "User:" + username + ",E-mail:" + email;
    }
}