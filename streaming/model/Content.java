package streaming.model;

import java.util.List;
import java.time.LocalDateTime;

public abstract class Content extends Entity {

  private String title;

  private int releaseYear;

  private Genre genre;

  private String region;

  private double avgRating;

  private double totalviews;

  private List<Artist> artists;

  protected Content(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region,
      double avgRating, double totalviews, List<Artist> artists) {
    super(id, createdAt);
    this.title = title;
    this.releaseYear = releaseYear;
    this.genre = genre;
    this.region = region;
    this.avgRating = avgRating;
    this.totalviews = totalviews;
    this.artists = artists;
  }

  public String getTitle() {
    return title;
  }

  public int getReleaseYear() {
    return releaseYear;
  }

  public Genre getGenre() {
    return genre;
  }

  public String getRegion() {
    return region;
  }

  public double getAvgRating() {
    return avgRating;
  }

  public double getTotalviews() {
    return totalviews;
  }

  public List<Artist> getArtists() {
    return artists;
  }

  public String getDetails() {

    return null;
  }

  public String getContentType() {
    return null;
  }

}