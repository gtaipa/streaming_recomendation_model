package streaming.model;


public class Episode {

  private int seasonNumber;

  private int episodeNumber;

  private String title;

  private int duration;
  private float rating;

     
      public Episode (int seasonNumber, int episodeNumber, String title, int duration, float rating) {
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.duration = duration;
        this.rating = rating;
      }

  public int getSeasonNumber() {
  return seasonNumber;
  }

  public int getEpisodeNumber() {
  return episodeNumber;
  }

  public String getTitle() {
  return title;
  }

  public int getDuration() {
  return duration;
  }

  public float getRating() {
  return rating;
  }

  public void setRating(float rating) {
    if (rating < 0 || rating > 10) {
      throw new IllegalArgumentException("Rating must be between 0 and 10");
    }
  this.rating = rating;
  }
@Override
  public String toString() {
  return "Season"+seasonNumber+"Episode"+episodeNumber+"-"+title+"("+duration+"min)";
  }

}