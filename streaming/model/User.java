package streaming.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Override
    public String toString() {
        return "User:" + username + ",E-mail:" + email;
    }
}