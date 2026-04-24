package streaming.model;


import java.time.LocalDateTime;
import java.util.List;

public class Documentary extends Content {

  public int duration;

  public String topic;

  public Artist narrator;

  protected Documentary(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region, double avgRating, double totalviews, List<Artist> artists) {
    super(id, createdAt, title, releaseYear, genre, region, avgRating, totalviews, artists);
  }

  public String getTopic() {
  return null;
  }

  public Artist getNarrator() {
  return null;
  }

  public String getContentType() {
  return null;
  }

}