package streaming.model; //  Arrumado na pasta "streaming.model"

import java.time.LocalDateTime; //  Ferramenta essencial para saber o instante exato em que a interação ocorreu.

/**
 * Interação entre um {@link User} e uma entidade alvo.
 * <p>
 * Pode representar uma interação com conteúdo ou uma relação de follow entre utilizadores.
 */
public class Interaction { // ️ Criamos a classe Interaction. O "registo" de tudo o que os users fazem!

  // Os atributos guardam o estado da interação em memória.
  /** Utilizador que realizou a interação. */
  private User user; //  Quem é que foi o cromo que clicou/viu/seguiu? É este gajo.

  /** Conteúdo associado à interação. */
  private Content content; //  O que é que ele estava a ver ou a avaliar? Fica aqui! (Se for uma interação com conteúdo).

  /** Utilizador alvo (apenas para interações do tipo FOLLOW). */
  private User targetUser; //  Se ele não clicou num filme, mas sim noutra pessoa (Follow), esse utilizador alvo fica aqui guardado!

  /** Tipo de interação. */
  private InteractionType type; // 🏷 Qual foi a cena? Viu? Deu like? Seguiu? (É o tal Enum).

  /** Momento em que ocorreu a interação. */
  private LocalDateTime timestamp; //  A hora exata e os minutos da "crime scene".

  /** Percentagem vista (0..100), se aplicável. */
  private double watchedPct; //  Viu o filme todo ou adormeceu a meio? Aqui fica a percentagem (de 0 a 100).

  /** Rating atribuído, se aplicável. */
  private double rating; // Se ele avaliou, a nota que deu fica aqui!

  /** Peso/preferência calculada para recomendação. */
  private double weight; //  A importância desta interação para o algoritmo de recomendação recomendar mais coisas fixes.

  /**
   * Cria uma interação com conteúdo.
   * <p>
   * Complexidade: {@code O(1)}.
   *
   * @param user utilizador
   * @param content conteúdo
   * @param type tipo de interação
   * @param timestamp instante da interação
   * @param watchedPct percentagem vista (0..100)
   * @param rating rating atribuído
   * @param weight peso da interação
   */
  public Interaction(User user, Content content, InteractionType type, LocalDateTime timestamp, double watchedPct, double rating, double weight) { // 🏭 Construtor 1! A fábrica que arranca quando a interação é com um filme/série/etc.
    // Cada parâmetro é guardado diretamente na instância.
    this.user = user; //  Guarda quem fez.
    this.content = content; //  Guarda o que viu.
    this.targetUser = null; //  Como foi um filme, o "utilizador alvo" fica a null (vazio).
    this.type = type; //  Guarda o tipo de ação.
    this.timestamp = timestamp; //  Guarda a hora.
    this.watchedPct = watchedPct; //  Guarda a % vista.
    this.rating = rating; //  Guarda a nota dada.
    this.weight = weight; //  Guarda o peso pro algoritmo.
  }

  /**
   * Cria uma interação de FOLLOW entre utilizadores.
   * <p>
   * Complexidade: {@code O(1)}.
   *
   * @param from utilizador que segue
   * @param to utilizador seguido
   * @param timestamp instante da interação
   * @param weight peso da interação
   */
  public Interaction(User from, User to, LocalDateTime timestamp, double weight) { //  Construtor 2! Usamos este só para quando um gajo segue o outro (tipo rede social).
    // Nesta sobrecarga não existe conteúdo, porque a relação é entre utilizadores.
    this.user = from; // Guarda quem começou a seguir.
    this.content = null; //  Como seguiu uma pessoa, não há filmes envolvidos. Mete null.
    this.targetUser = to; //  Guarda o felizardo que ganhou um seguidor!
    this.type = InteractionType.FOLLOW; // ️ Força o tipo de interação para "FOLLOW".
    this.timestamp = timestamp; //  Guarda as horas.
    this.watchedPct = 0.0; //  Obviamente não há % de visualização de pessoas, fica a zero.
    this.rating = 0.0; // Não damos nota às pessoas (ainda), fica zero.
    this.weight = weight; // ⚖Guarda a relevância do follow.
  }

  /**
   * Devolve o peso da interação.
   *
   * @return peso da interação
   */
  public double getWeight() { // 📥 Querem saber o peso disto pro algoritmo?
    return this.weight; //  Retorna o peso.
  } //

  /**
   * Devolve o instante da interação.
   *
   * @return timestamp da interação
   */
  public LocalDateTime getTimestamp() {
    return this.timestamp; //  Dá as hora.
  } // Fim.
  /**
   * Representação textual resumida da interação.
   *
   * @return texto resumido
   */
  @Override // ♻️ Como é que isto se imprime de forma legível.
  public String toString() { //  A cena que mostra a interação de forma bacana.
    String target; // Variável temporária para descobrir se o alvo é pessoa ou filme.
    // A lógica escolhe a entidade alvo certa consoante o tipo da interação.
    if (content != null) target = "Content: " + content.getId(); // 🎬 Se o conteúdo existir, o alvo é ele.
    else if (targetUser != null) target = "User: " + targetUser.getId(); // 👤 Se o alvo for um user, mete lá o ID dele.
    else target = "Target: null"; // ‍♂Se estiver tudo vazio, mete "null".
    return "Interaction [" + type + "] - User: " + (user == null ? "null" : user.getId()) + " | " + target + " | Weight: " + weight; // 📝 Constrói o testamento final: "Tipo - Quem fez - O que foi feito - O peso".
  } // 🚪 Fim da impressão.

  /**
   * Devolve o utilizador que originou a interação.
   *
   * @return utilizador
   */
  public User getUser() { // 📥 Quem clicou?
    return user; // 📤 Foi este maninho.
  } // 🚪 Fim.

  /**
   * Devolve o conteúdo associado.
   *
   * @return conteúdo ou {@code null}
   */
  public Content getContent() { // 📥 Qual foi o conteúdo?
    return content; // 📤 Foi este.
  } // 🚪 Fim.

  /**
   * Devolve o utilizador seguido.
   *
   * @return utilizador seguido ou {@code null}
   */
  public User getTargetUser() { // 📥 Quem ganhou o follow?
    return targetUser; // 📤 Foi este gajo.
  } // 🚪 Fim.

  /**
   * Entidade alvo da interação.
   * - WATCH/RATE/CLICK: o {@link Content}
   * - FOLLOW: o utilizador seguido
   *
   * @return entidade alvo da interação
   */
  public Entity getTargetEntity() { // 📥 Esta função é fixe: devolve o ALVO, seja ele pessoa ou filme.
    // Se houver conteúdo, a interação aponta para conteúdo.
    if (content != null) return content; // 🎬 Se tem conteúdo, devolve o conteúdo.
    // Caso contrário, é uma relação de follow entre utilizadores.
    return targetUser; // 🎯 Se não, deve ser um user alvo, toma lá!
  } // 🚪 Fim da bruxaria.

  /**
   * Devolve o tipo de interação.
   *
   * @return tipo de interação
   */
  public InteractionType getType() { // 📥 O que é que aconteceu ao certo?
    return type; // 📤 Aconteceu isto (Watch, Rate, Follow, etc).
  } // 🚪 Fim.

  /**
   * Devolve a percentagem vista.
   *
   * @return percentagem vista
   */
  public double getWatchedPct() { // 📥 Viu quanto % do filme?
    return watchedPct; // 📤 Viu isto tudo.
  } // 🚪 Fim.

  /**
   * Devolve o rating.
   *
   * @return rating
   */
  public double getRating() { // 📥 Quantas estrelas deu?
    return rating; // 📤 Deu estas.
  } // 🚪 Fim.

  /**
   * Atualiza o peso da interação.
   *
   * @param weight novo peso
   */
  public void setWeight(double weight) { // 🔄 Função para alterar o peso da interação (o algoritmo pode querer ajustar as coisas).
    this.weight = weight; // ⚖️ Atualiza o peso.
  } // 🚪 Fim da função.
} // 🛑 Fim da classe da Interação. Acabou o registo!
