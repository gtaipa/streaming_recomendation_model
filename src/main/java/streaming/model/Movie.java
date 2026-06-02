package streaming.model; // 📁 Diz que este ficheiro vai para a pasta 'streaming.model'.

import java.time.LocalDateTime; // 🕒 Ferramenta para gerir tempos.
import java.util.ArrayList; // 📦 Ferramenta para criar as nossas famosas "listas elásticas".
import java.util.List; // 📚 A noção de "Lista".

/**
 * Filme, isto é, conteúdo com duração e realizador.
 */
public class Movie extends Content { // 🏗️ Criamos a classe Movie (Filme), que herda todas as características gerais de Content!

  /** Duração em minutos. */
  private int duration; // ⏱️ Quantos minutos de sofá vamos precisar.

  /** Realizador do filme. */
  private Artist director; // 🎬 O boss do estúdio, o realizador!

  /** Elenco (cast) do filme. */
  private List<Artist> cast; // 🧑‍🤝‍🧑 A malta toda que entrou no filme (os atores).

  /**
   * Cria um filme.
   * <p>
   * Complexidade: {@code O(1)}.
   *
   * @param id identificador único
   * @param createdAt data/hora de criação
   * @param title título
   * @param releaseYear ano de lançamento
   * @param genre género
   * @param region região
   * @param duration duração em minutos
   * @param director realizador
   */
  public Movie(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region,
      int duration, Artist director) { // 🏭 O construtor. Recebe toda a tralha necessária para montar um filme.
    // Um filme reutiliza os campos comuns da classe Content.
    super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // 🚀 Chama o construtor do "pai" (Content). Mete a nota a 0, as views a 0 e os artistas como uma lista vazia, para começar levezinho!
    this.duration = duration; // ⏱️ Guarda os minutos de filme.
    this.director = director; // 🎬 Guarda o realizador.
    // O cast começa vazio e pode ser preenchido depois.
    this.cast = new ArrayList<>(); // 📦 Inicializa o elenco vazio. Os atores chegam depois.
  } // 🚪 Fechou a loja de filmes.

  /**
   * Devolve a duração em minutos.
   *
   * @return duração em minutos
   */
  public int getDuration() { // 📥 Quantos minutos tem isto?
    return duration; // 📤 Toma lá a duração.
  } // 🚪 Fim.

  /**
   * Devolve o realizador.
   *
   * @return realizador
   */
  public Artist getDirector() { // 📥 Quem realizou o filme?
    return director; // 📤 Foi este senhor(a).
  } // 🚪 Fim.

  /**
   * Devolve o elenco.
   *
   * @return lista do cast
   */
  public List<Artist> getCast() { // 📥 Quem é a malta que participa nisto?
    return cast; // 📤 Aqui tens a malta do elenco!
  } // 🚪 Fim.

  /**
   * Devolve o tipo de conteúdo.
   *
   * @return {@code Movie}
   */
  @Override // ♻️ Vamos substituir o método abstrato que estava no pai.
  public String getContentType() { // 📥 Que tipo de conteúdo és tu?
    return "Movie"; // 📤 "Sou um filme, bro!"
  } // 🚪 Fim.

  /**
   * Devolve detalhes do filme para exibição.
   *
   * @return descrição curta do filme
   */
  @Override // ♻️ Substituímos a outra função abstrata para dar os pormenores do filme.
  public String getDetails() { // 📥 Queres detalhes disto?
    // Se o realizador não existir, mostramos Unknown para evitar NullPointerException.
    String directorName = (director == null) ? "Unknown" : director.getName(); // 🛡️ Proteção de milhões: Se não houver realizador, é "Unknown", senão o Java passava-se (Dava erro null).
    return "Duration: " + duration + " min, Director: " + directorName; // 📝 Constrói o texto bonitinho.
  } // 🚪 Fim.

  /**
   * Adiciona um artista ao elenco.
   *
   * @param artist artista a adicionar
   */
  public void addCast(Artist artist) { // 🔄 Função para meter mais atores no filme.
    // A lista está em memória e cresce com referências para artistas.
    this.cast.add(artist); // ➕ Adiciona o gajo à lista do elenco!
  } // 🚪 Fim.

  @Override // ♻️ Altera o modo como o objeto é convertido para texto.
  public String toString() { // 🖨️ Quando tentamos "imprimir" o filme...
    // Texto curto para tabelas e debug.
    return "Movie: " + getTitle() + " (" + getReleaseYear() + ")"; // 📝 Fica tipo "Movie: Titanic (1997)". Classic!
  } // 🚪 Fim.

} // 🛑 Fecha o ficheiro. Corta!
