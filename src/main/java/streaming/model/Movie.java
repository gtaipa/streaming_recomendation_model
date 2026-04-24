package streaming.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Movie extends Content {

  private int duration;

  private Artist director;

  private List<Artist> cast;

  public Movie(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region,
      int duration, Artist director) {
super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // 0.0 = rating, 0 = views, new ArrayList<>() = artists
    this.duration = duration;
    this.director = director;
    this.cast = new ArrayList<>();
    

  }

  public int getDuration() {
    return duration;
  }

  public Artist getDirector() {
    return director;
  }

  public List<Artist> getCast() {
    return cast;
  }

  public String getContentType() {
    return "Movie";
  }



  public void addCast(Artist artist) {
    this.cast.add(artist);
  }

  @Override
  public String toString() {
    return "Movie: " + getTitle() + " (" + getReleaseYear() + ")";
  }

}