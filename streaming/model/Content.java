package streaming.model;

import java.util.List;

public abstract class Content extends Entity {

  public String title;

  public int releaseYear;

  public Genre genre;

  public String region;

  public double avgRating;

  public double totalviews;

  private List<Artist> artists;
  public  Content (String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region, double avgRating, double totalviews, List<Artist> artists){
    super(id, createdAt);
    this.title = title;
    this.releaseYear = releaseYear;
    this.genre = genre;
    this.region = region;
    this.avgRating = avgRating;
    this.totalviews = totalviews;
    this.artists = artists;
  }


  public String getDetails() {
  return null;
  }

  public String getContentType() {
  return null;
  }

}