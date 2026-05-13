package streaming.core;

//import streaming.db.StreamingGraph;
import streaming.db.StreamingDB;
import streaming.model.Content;
import streaming.model.Genre;

//import streaming.model.List;
import streaming.model.User;

/**
 * Motor de recomendacao (placeholder para fases seguintes).
 * <p>
 * Nesta fase, a classe expõe a ligacao a {@link StreamingDB} e serve de ponto de extensao
 * para algoritmos de recomendacao (ex.: trending por genero, recomendacoes por similaridade, etc.).
 */
public class RecommendationEngine {

  //public StreamingGraph graph;

  public StreamingDB db;

  /*public List recommend(User u) {
  return null;
  }

  public List trending(Genre genre) {
  return null;
  }

  public List followersWatched(User u, Content c) {
  return null;
  }
*/
}
