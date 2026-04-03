package streaming.model;

import java.util.List;
import String;

public abstract class Content extends Entity {

  public String title;

  public int releaseYear;

  public Genre genre;

  public String region;

  public double avgRating;

  public double totalviews;

    public List<Genre> genre;
    public List<StreamingDB> streamingDB;

  public String getDetails() {
  return null;
  }

  public String getContentType() {
  return null;
  }

}