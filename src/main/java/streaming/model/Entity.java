package streaming.model;

import java.time.LocalDateTime;


public abstract class Entity {

  protected String id;

  protected LocalDateTime createdAt;


  public Entity(String id, LocalDateTime createdAt) {
    this.id = id;
    this.createdAt = createdAt;
  }

  public String getId() {

  return id;
  }

  public LocalDateTime getCreatedAt() {
  return createdAt;
  }

  public boolean equals(Object o) {
  return false;
  }

}