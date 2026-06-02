package streaming.model; // 📁 Coloca o ficheiro na pasta "streaming.model"

import java.time.LocalDateTime; // 🕒 Ferramenta para lidar com datas e horas no Java.
import java.util.List; // 📚 Ferramenta para criar listas de coisas (neste caso, artistas).

/**
 * Documentário, isto é, conteúdo com duração, tópico e narrador.
 */
public class Documentary extends Content { // 🏗️ Criação da classe Documentary, que herda as características da classe Content (filho herda do pai).

  /** Duração em minutos. */
  private int duration; // ⏱️ Variável inteira que diz quantos minutos dura o documentário.

  /** Tópico/tema principal. */
  private String topic; // 🏷️ Variável de texto (String) para guardar o tema central (ex: "Natureza", "História").

  /** Narrador do documentário. */
  private Artist narrator; // 🎙️ Guarda o Artista que está a dar a voz à cena toda.

  /**
   * Cria um documentário.
   * <p>
   * Complexidade: {@code O(1)}.
   *
   * @param id identificador único
   * @param createdAt data/hora de criação
   * @param title título
   * @param releaseYear ano de lançamento
   * @param genre género
   * @param region região
   * @param avgRating rating médio
   * @param totalViews total de visualizações
   * @param artists artistas associados
   * @param duration duração em minutos
   * @param topic tópico
   * @param narrator narrador
   */
  public Documentary( // 🏭 O construtor. É a função principal que "monta" o Documentário com todas as informações.
      String id, // 🆔 O ID.
      LocalDateTime createdAt, // 📅 Data em que foi criado.
      String title, // 🎬 O título do documentário.
      int releaseYear, // 📅 Ano de lançamento.
      Genre genre, // 🎭 O género.
      String region, // 🌍 Região (país) a que pertence.
      double avgRating, // ⭐ A classificação média que a malta lhe deu.
      int totalViews, // 👁️ Quantas vezes já foi visto no total.
      List<Artist> artists, // 🧑‍🤝‍🧑 A lista da malta que participou.
      int duration, // ⏱️ Quantos minutos tem.
      String topic, // 🏷️ O tema principal.
      Artist narrator // 🎙️ E, finalmente, o narrador.
  ) { // 🚪 Abre o bloco do construtor.
    // Reutiliza os campos comuns do conteúdo base.
    super(id, createdAt, title, releaseYear, genre, region, avgRating, totalViews, artists); // 🚀 Chama o construtor do "pai" (Content) para não estar a repetir código com as coisas básicas!
    this.duration = duration; // ⏱️ Atribui a duração que recebemos à variável da classe.
    this.topic = topic; // 🏷️ Guarda o tópico.
    this.narrator = narrator; // 🎙️ Guarda o narrador.
  } // 🚪 Fim do construtor.

  /**
   * Devolve o tópico/tema do documentário.
   *
   * @return tópico
   */
  public String getTopic() { // 📥 Função (getter) para perguntar: "Qual é o tema disto?"
    return topic; // 📤 E ele responde o tema!
  } // 🚪 Fim.

  /**
   * Devolve o narrador do documentário.
   *
   * @return narrador
   */
  public Artist getNarrator() { // 📥 Função para pedir quem é o narrador.
    return narrator; // 📤 Toma lá o artista narrador.
  } // 🚪 Fim.

  /**
   * Devolve a duração em minutos.
   *
   * @return duração em minutos
   */
  public int getDuration() { // 📥 Função para perguntar quantos minutos demora.
    return duration; // 📤 Dá-nos a duração.
  } // 🚪 Fim.

  /**
   * Devolve o tipo de conteúdo.
   *
   * @return {@code Documentary}
   */
  @Override // ♻️ Substitui o método do pai "Content" para dizer especificamente o que isto é.
  public String getContentType() { // 📥 Função para descobrir o tipo disto.
    return "Documentary"; // 📤 Responde sempre "Documentary". Óbvio!
  } // 🚪 Fim.

  /**
   * Devolve detalhes do documentário para exibição.
   *
   * @return descrição curta do documentário
   */
  @Override // ♻️ Outro método reescrito para dar os detalhes disto formatados para impressão.
  public String getDetails() { // 📥 Função para dar os pormenores básicos.
    // Proteção contra null para manter a saída legível.
    String narratorName = (narrator == null) ? "Unknown" : narrator.getName(); // 🛡️ Se o narrador não existir (for null), chamamos-lhe "Unknown" (Desconhecido). Se existir, apanha o nome dele!
    String safeTopic = (topic == null) ? "Unknown" : topic; // 🛡️ A mesma cena de segurança para o tópico.
    return "Topic: " + safeTopic + ", Duration: " + duration + " min, Narrator: " + narratorName; // 📝 Constrói uma string fixe com tudo isto misturado.
  } // 🚪 Fim dos detalhes.

} // 🛑 Fim do ficheiro do documentário. Finito!
