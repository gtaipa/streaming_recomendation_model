package streaming.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Series extends Content {

  private int seasons;

  private List<Episode> episodes;

  private SeriesStatus status;

    public Series (String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region, int seasons, SeriesStatus status) {
super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // 0.0 = rating, 0 = views, new ArrayList<>() = artists
      this.seasons = seasons;
      this.status = status;
      this.episodes = new ArrayList<>();
    }

  public int getSeasons() {
  return seasons;
  }

  public List getEpisodes() {
  return episodes;
  }

  public void addEpisode(Episode e) {
    for (Episode episode : episodes) {
      if (episode.getEpisodeNumber() == e.getEpisodeNumber()) {
        return;
      }
    }
    episodes.add(e);
  }

  public String getContentType() {
  return "Series";
  }

}