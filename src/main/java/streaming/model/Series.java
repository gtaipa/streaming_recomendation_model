package streaming.model; // 📁 Como de costume, vai tudo para a pasta 'streaming.model'.

import java.time.LocalDateTime; // 🕒 O clássico import das horinhas.
import java.util.ArrayList; // 📦 O nosso criador de listas vazias.
import java.util.List; // 📚 A cena das listas.

/**
 * Série, isto é, conteúdo com temporadas, episódios e estado.
 */
public class Series extends Content { // 🏗️ Criamos a classe Series, que também é filha do grande "Content".

  /** Número de temporadas. */
  private int seasons; // 📺 Quantas temporadas tem esta brincadeira?

  /** Episódios da série. */
  private List<Episode> episodes; // 🎬 A lista gigante de episódios para maratonar.

  /** Estado da série. */
  private SeriesStatus status; // 🏁 Está a dar, acabou ou foi cancelada? (Lembras-te do Enum?)

  /**
   * Cria uma série.
   * <p>
   * Complexidade: {@code O(1)}.
   *
   * @param id identificador único
   * @param createdAt data/hora de criação
   * @param title título
   * @param releaseYear ano de lançamento
   * @param genre género
   * @param region região
   * @param seasons número de temporadas
   * @param status estado da série
   */
  public Series (String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region, int seasons, SeriesStatus status) { // 🏭 Construtor, onde montamos a série inteira.
    // Reutiliza os campos comuns do conteúdo base.
    super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // 🚀 Chama a fábrica do pai (Content) para tratar das tretas habituais, nota a zero, etc.
    this.seasons = seasons; // 📺 Guarda o número de temporadas.
    this.status = status; // 🏁 Guarda o estado atual da série.
    // Lista de episódios guardada em memória e inicialmente vazia.
    this.episodes = new ArrayList<>(); // 📦 Deixa a lista de episódios vazia, pronta para levar com a maratona.
  } // 🚪 Construtor fechado.

  /**
   * Devolve o número de temporadas.
   *
   * @return número de temporadas
   */
  public int getSeasons() { // 📥 Tem quantas seasons?
    return seasons; // 📤 Toma lá a info.
  } // 🚪 Fim.

  /**
   * Devolve o estado da série.
   *
   * @return estado da série
   */
  public SeriesStatus getStatus() { // 📥 Ainda está no ar?
    return status; // 📤 Dá o estado (Ended, Ongoing, etc).
  } // 🚪 Fim.

  /**
   * Devolve a lista de episódios.
   *
   * @return lista de episódios
   */
  public List<Episode> getEpisodes() { // 📥 Quero ver os episódios todos!
    return episodes; // 📤 Toma lá a lista cheia deles.
  } // 🚪 Fim.

  /**
   * Adiciona um episódio se ainda não existir um com o mesmo número.
   * <p>
   * Complexidade: {@code O(n)} no pior caso, porque percorre a lista de episódios.
   *
   * @param e episódio a adicionar
   */
  public void addEpisode(Episode e) { // 🔄 Função para meter um episódio novo na série.
    // Procuramos manualmente para evitar duplicados por número de episódio.
    for (Episode episode : episodes) { // 🕵️ Vai dar uma volta pelos episódios que já lá estão...
      if (episode.getEpisodeNumber() == e.getEpisodeNumber()) { // ⚠️ Se encontrar um com o mesmo número (tipo o episódio 5 a querer entrar 2 vezes)...
        return; // 🛑 Baza da função! Não mete cenas repetidas aqui.
      }
    }
    // Se não houver duplicado, a referência é guardada na lista.
    episodes.add(e); // ✅ Se não houver estrilho, adiciona o episódio à vontade.
  } // 🚪 Fim da função.

  /**
   * Devolve o tipo de conteúdo.
   *
   * @return {@code Series}
   */
  @Override // ♻️ Sobrescreve o tipo de conteúdo do Content genérico.
  public String getContentType() { // 📥 Que tipo de cena é esta?
    return "Series"; // 📤 "Sou uma série bro!"
  } // 🚪 Fim.

  /**
   * Devolve detalhes da série para exibição.
   *
   * @return descrição curta da série
   */
  @Override // ♻️ Dá-nos os detalhes à maneira das Séries.
  public String getDetails() { // 📥 Detalhes faz favor?
    // Protegemos a saída contra null para evitar exceções na UI.
    int epCount = (episodes == null) ? 0 : episodes.size(); // 🛡️ Se a lista for nula (tipo, deu barraca) dizemos que tem 0, senão conta o tamanho real.
    String safeStatus = (status == null) ? "Unknown" : status.toString(); // 🛡️ Se o estado for nulo (ninguém sabe), manda "Unknown". Se não manda o estado real.
    return "Seasons: " + seasons + ", Episodes: " + epCount + ", Status: " + safeStatus; // 📝 E pimba, monta o texto bonito final!
  } // 🚪 Fim dos detalhes.

} // 🛑 Acabou a série. Próximo!
