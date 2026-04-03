package streaming.model;

import java.util.List;



public class User extends Entity {
  public String username;

  public String email;

  public String region;

  public LocalDate registrationDate;

  public List<Genre> preferences;

  public List<User> following;
public User(String id, LocalDateTime createdAt, String username, String email) {
        // 1º Passo: Satisfazer o construtor da classe mãe (Entity)
        super(id, createdAt);
        this.username = username;
        this.email=email;
        
    }



  public void follow(User u) {
  }

  public List getFollowing() {
  return following;
  }

  public List getPreferences() {
  return preferences;
  }

  public String toString() {
    String User_String = "User:" + username +",E-mail:" + email ;
  return User_String ;
  }

}