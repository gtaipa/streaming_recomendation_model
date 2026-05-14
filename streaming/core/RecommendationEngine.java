package streaming.core;

import streaming.model.Genre;
import streaming.db.StreamingDB;
import streaming.model.List;
import streaming.db.StreamingGraph;
import streaming.model.Content;
import streaming.model.User;

public class RecommendationEngine {

  public StreamingGraph graph;

  public StreamingDB db;

  public List recommend(User u) {
  return null;
  }

  public List trending(Genre genre) {
  return null;
  }

  public List followersWatched(User u, Content c) {
  return null;
  }

}