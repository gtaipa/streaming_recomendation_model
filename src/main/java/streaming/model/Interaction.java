package streaming.model; // Definição do pacote pertencente à camada do modelo de domínio do ecossistema.

import java.time.LocalDateTime; // Dependência da API de data e hora para registo cronológico preciso dos eventos.

/**
 * Interação entre um {@link User} e uma entidade alvo.
 * <p>
 * Pode representar uma interação com conteúdo ou uma relação de follow entre utilizadores.
 */
public class Interaction { // Entidade de domínio que modela os metadados associados às ações dos utilizadores.

  // Os atributos guardam o estado da interação em memória (Encapsulamento).
  /** Utilizador que realizou a interação. */
  private User user; // Referência ao objeto do utilizador que desencadeou e originou a ação.

  /** Conteúdo associado à interação. */
  private Content content; // Referência ao conteúdo multimédia alvo (aplicável a interações de consumo de media).

  /** Utilizador alvo (apenas para interações do tipo FOLLOW). */
  private User targetUser; // Referência ao utilizador de destino, exclusiva para ligações de natureza social.

  /** Tipo de interação. */
  private InteractionType type; // Tipo enumerado que encapsula a taxonomia e restrições da ação executada.

  /** Momento em que ocorreu a interação. */
  private LocalDateTime timestamp; // Registo temporal imutável (timestamp) que assinala a ocorrência do evento.

  /** Percentagem vista (0..100), se aplicável. */
  private double watchedPct; // Métrica quantitativa escalar que expressa a taxa de retenção/visualização do conteúdo.

  /** Rating atribuído, se aplicável. */
  private double rating; // Métrica de avaliação explícita atribuída quantitativamente pelo utilizador.

  /** Peso/preferência calculada para recomendação. */
  private double weight; // Fator de ponderação algébrica utilizado para alimentar o cálculo de afinidade no grafo.

  /*
   * DICIONÁRIO DE TERMOS TÉCNICOS (ATRIBUTOS)
   * - Modelo de Domínio (Domain Model): Camada do software que representa os conceitos, regras de negócio e dados reais do problema (neste caso, o ecossistema de streaming).
   * - Encapsulamento: Princípio de POO que oculta os dados internos de uma classe (usando modificadores private), expondo o comportamento apenas através de métodos controlados.
   * - Metadados: Dados estruturados que fornecem informação sobre outros dados (ex: a hora a que um vídeo foi visto).
   * - Tipo Enumerado (Enum): Tipo de dado especial em Java que restringe o valor de uma variável a um conjunto predefinido de constantes (evita strings livres e erros de digitação).
   * - Timestamp: Instante de tempo registado de forma precisa e linear no formato de calendário atómico ou do sistema.
   */

  /**
   * Cria uma interação com conteúdo.
   * <p>
   * Complexidade: {@code O(1)}.
   */
  public Interaction(User user, Content content, InteractionType type, LocalDateTime timestamp, double watchedPct, double rating, double weight) { // Construtor parametrizado para instanciar interações baseadas em consumo de conteúdos multimédia.
    // Atribuição direta dos parâmetros aos atributos da instância.
    this.user = user; // Injeta a referência do utilizador originador.
    this.content = content; // Injeta a referência do conteúdo multimédia avaliado/visualizado.
    this.targetUser = null; // Define a nulidade do utilizador alvo, dado que o escopo desta instância é um conteúdo.
    this.type = type; // Atribui a tipologia da interação com base no enumerado.
    this.timestamp = timestamp; // Grava o instante temporal do evento.
    this.watchedPct = watchedPct; // Armazena a métrica percentual de reprodução do ficheiro.
    this.rating = rating; // Regista a classificação quantitativa introduzida.
    this.weight = weight; // Atribui a magnitude de peso inicial calculada para processamento algorítmico.
  }

  /*
   * DICIONÁRIO DE TERMOS TÉCNICOS (CONSTRUTOR CONTEÚDO)
   * - Construtor Parametrizado: Método especial invocado no momento da criação de um objeto (new) que inicializa os atributos da instância com os valores passados por argumento.
   * - Instanciar: O ato de criar um objeto concreto na memória RAM a partir do molde/projeto definido por uma classe.
   * - Injeção de Referência: Passagem do endereço de memória de um objeto existente para dentro de um atributo, permitindo a associação direta entre as duas entidades.
   * - Complexidade O(1): Notação Big-O que indica tempo de execução constante. Significa que a operação demora exatamente o mesmo tempo, independentemente do volume total de dados do sistema.
   */

  /**
   * Cria uma interação de FOLLOW entre utilizadores.
   * <p>
   * Complexidade: {@code O(1)}.
   */
  public Interaction(User from, User to, LocalDateTime timestamp, double weight) { // Sobrecarga do construtor otimizada para o estabelecimento de vínculos de cariz social.
    // Inicialização do estado do objeto direcionado para ligações entre utilizadores.
    this.user = from; // Estabelece o vértice de origem da relação (o utilizador seguidor).
    this.content = null; // Define a nulidade do conteúdo, dada a ausência de componentes multimédia nesta ação.
    this.targetUser = to; // Estabelece o vértice de destino da relação (o utilizador seguido).
    this.type = InteractionType.FOLLOW; // Força a tipologia do evento estaticamente para a constante de enumeração FOLLOW.
    this.timestamp = timestamp; // Assinala a marca temporal do estabelecimento do vínculo.
    this.watchedPct = 0.0; // Inicializa a taxa de visualização com valor nulo por incompatibilidade semântica.
    this.rating = 0.0; // Inicializa a classificação com valor nulo por incompatibilidade semântica.
    this.weight = weight; // Atribui o peso associado à relevância da conectividade social para o motor de grafos.
  }

  /*
   * DICIONÁRIO DE TERMOS TÉCNICOS (CONSTRUTOR SOCIAL)
   * - Sobrecarga de Métodos (Method Overloading): Recurso que permite que uma classe tenha múltiplos construtores ou métodos com o mesmo nome, desde que as suas assinaturas (lista de parâmetros) sejam diferentes.
   * - Vértice (Node): Entidade individual contida num grafo (neste contexto, utilizadores e conteúdos são vértices).
   * - Vínculo / Aresta (Edge): Linha de ligação que conecta dois vértices num grafo, representando formalmente uma relação entre eles (a interação é a materialização de uma aresta).
   * - Incompatibilidade Semântica: Ocorre quando um atributo ou operação não faz sentido lógico para um determinado estado do objeto (ex: calcular a percentagem de vídeo assistida ao seguir uma pessoa).
   */

  /**
   * Representação textual resumida da interação.
   *
   * @return texto resumido
   */
  @Override // Sobrescrita do método herdado da classe base Object para polimorfismo comportamental.
  public String toString() { // Método de serialização textual para depuração (debugging) e auditoria de logs.
    String target; // Declaração de uma variável local para centralizar o identificador do destino analítico.
    
    // Estrutura de controlo de fluxo condicional para avaliar a semântica do alvo da aresta.
    if (content != null) target = "Content: " + content.getId(); // Extrai a identidade se o alvo for de natureza multimédia.
    else if (targetUser != null) target = "User: " + targetUser.getId(); // Extrai a identidade se o alvo for de natureza social.
    else target = "Target: null"; // Bloco de contingência para o tratamento de referências nulas.
    
    // Concatenação estruturada dos atributos para geração da representação textual unificada.
    return "Interaction [" + type + "] - User: " + (user == null ? "null" : user.getId()) + " | " + target + " | Weight: " + weight;
  }

  /**
   * Entidade alvo da interação.
   * - WATCH/RATE/CLICK: o {@link Content}
   * - FOLLOW: o utilizador seguido
   *
   * @return entidade alvo da interação
   */
  public Entity getTargetEntity() { // Método polimórfico que abstrai o tipo concreto do alvo, devolvendo a superclasse comum.
    // Avaliação condicional baseada na presença do estado do objeto.
    if (content != null) return content; // Devolve a instância do conteúdo se esta se encontrar preenchida.
    
    return targetUser; // Retorna o utilizador alvo caso a condição precedente não seja satisfeita.
  }

  /*
   * DICIONÁRIO DE TERMOS TÉCNICOS (MÉTODOS LÓGICOS)
   * - Sobrescrita (@Override): Substituição de um método herdado de uma classe-mãe (ou da classe suprema Object) por uma implementação específica na subclasse.
   * - Serialização: Processo de converter o estado de um objeto em memória numa sequência de caracteres (String) ou bytes para exibição, armazenamento ou transmissão.
   * - Tratamento de Contingência: Lógica de salvaguarda implementada no código para evitar falhas críticas do sistema (como o erro fatal NullPointerException) quando os dados estão ausentes.
   * - Abstração / Polimorfismo: Capacidade de tratar diferentes objetos de subclasses (Movie, User, etc.) através da sua superclasse comum (Entity), simplificando a manipulação genérica de dados.
   */

  /**
   * Devolve o peso da interação.
   *
   * @return peso da interação
   */
  public double getWeight() { 
    return this.weight; // Retorna o valor atual do fator de ponderação.
  }

  /**
   * Devolve o instante da interação.
   *
   * @return timestamp da interação
   */
  public LocalDateTime getTimestamp() {
    return this.timestamp; // Retorna a marca temporal do evento.
  }

  /**
   * Devolve o utilizador que originou a interação.
   *
   * @return utilizador
   */
  public User getUser() { 
    return user; // Retorna a referência do utilizador emissor.
  }

  /**
   * Devolve o conteúdo associado.
   *
   * @return conteúdo ou {@code null}
   */
  public Content getContent() { 
    return content; // Retorna a referência do conteúdo multimédia envolvido.
  }

  /**
   * Devolve o utilizador seguido.
   *
   * @return utilizador seguido ou {@code null}
   */
  public User getTargetUser() { 
    return targetUser; // Retorna a referência do utilizador recetor.
  }

  /**
   * Devolve o tipo de interação.
   *
   * @return tipo de interação
   */
  public InteractionType getType() { 
    return type; // Retorna a tipologia enumerada da ação.
  }

  /**
   * Devolve a percentagem vista.
   *
   * @return percentagem vista
   */
  public double getWatchedPct() { 
    return watchedPct; // Retorna a métrica de consumo escalar.
  }

  /**
   * Devolve o rating.
   *
   * @return rating
   */
  public double getRating() { 
    return rating; // Retorna o score avaliativo registado.
  }

  /**
   * Atualiza o peso da interação.
   *
   * @param weight novo peso
   */
  public void setWeight(double weight) { // Único ponto de mutabilidade controlada da classe para reajuste algorítmico dinâmico.
    this.weight = weight; // Sobrescreve o estado anterior do peso em memória.
  }

  /*
   * DICIONÁRIO DE TERMOS TÉCNICOS (GETTERS/SETTERS)
   * - Métodos de Acesso (Getters/Setters): Métodos públicos criados para permitir a leitura ou escrita controlada dos atributos privados de uma classe, garantindo o princípio do encapsulamento.
   * - Mutabilidade: Propriedade que permite que o estado interno de um objeto seja alterado após ter sido criado. Nesta classe, apenas o atributo weight é mutável (através de setWeight), tornando todos os outros dados imutáveis e protegidos contra alterações acidentais.
   */
}
