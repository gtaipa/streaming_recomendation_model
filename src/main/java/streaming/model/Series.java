package streaming.model; // Declara que esta classe pertence ao pacote streaming.model

import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas (ano, mes, dia, hora, minuto, segundo)
import java.util.ArrayList; // Importa a classe que implementa listas dinamicas que crescem automaticamente
import java.util.List; // Importa a interface que define o contrato de uma lista ordenada de elementos

/**
 * Representa uma serie da plataforma, com temporadas, episodios e estado.
 */
/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - extends: palavra-chave que indica que esta classe herda atributos e metodos de outra classe
 * - Content: classe pai que contem os campos comuns a todos os tipos de conteudo
 * - Series: nome desta classe; representa uma serie televisiva com episodios e estado
 * - private: nivel de acesso que so permite acesso dentro desta propria classe
 * - int: tipo de dados para numeros inteiros
 * - List: colecao ordenada de elementos que pode crescer dinamicamente
 * - SeriesStatus: enum que representa o estado atual da serie (ONGOING, ENDED, CANCELLED)
 * - Episode: classe que representa um episodio individual da serie
 */
public class Series extends Content { // Define a classe Series que herda de Content, representando uma serie televisiva

  /** Numero de temporadas da serie. */
  private int seasons; // Guarda o numero total de temporadas desta serie

  /** Lista de episodios da serie. */
  private List<Episode> episodes; // Guarda a lista de todos os episodios desta serie

  /** Estado atual da serie. */
  private SeriesStatus status; // Guarda o estado atual da serie (em producao, terminada ou cancelada)

  /**
   * Cria uma serie com os seus dados essenciais.
   *
   * @param id identificador unico da serie
   * @param createdAt data e hora de criacao
   * @param title titulo da serie
   * @param releaseYear ano de lancamento
   * @param genre genero da serie
   * @param region regiao associada a serie
   * @param seasons numero de temporadas
   * @param status estado atual da serie
   */
  /*
   * DICIONARIO:
   * - construtor: metodo especial chamado quando se cria um novo objeto
   * - super: chamada ao construtor da classe pai (Content) para inicializar os campos herdados
   * - this: referencia ao objeto atual que esta a ser criado
   * - new ArrayList<>(): cria uma nova lista vazia pronta a receber elementos
   * - 0.0: valor inicial para a classificacao media (zero porque a serie e nova)
   * - 0: valor inicial para o total de visualizacoes (zero porque a serie e nova)
   */
  public Series(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region, int seasons, SeriesStatus status) { // Construtor que cria uma nova serie com todos os seus dados essenciais
    super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // Chama o construtor da classe pai Content com valores iniciais para nota e visualizacoes
    this.seasons = seasons; // Guarda o numero de temporadas recebido como parametro
    this.status = status; // Guarda o estado da serie recebido como parametro
    this.episodes = new ArrayList<>(); // Inicializa a lista de episodios como vazia; os episodios sao adicionados depois
  } // Fim do construtor

  /**
   * Devolve o numero de temporadas da serie.
   *
   * @return numero de temporadas
   */
  /*
   * DICIONARIO:
   * - getter: metodo que devolve o valor de um atributo privado
   * - return: palavra-chave que devolve um valor ao codigo que chamou este metodo
   */
  public int getSeasons() { // Metodo getter que devolve o numero de temporadas da serie
    return seasons; // Devolve o valor do atributo seasons
  } // Fim do metodo getSeasons

  /**
   * Devolve o estado atual da serie.
   *
   * @return estado da serie
   */
  public SeriesStatus getStatus() { // Metodo getter que devolve o estado atual da serie
    return status; // Devolve o valor do atributo status
  } // Fim do metodo getStatus

  /**
   * Devolve a lista de episodios da serie.
   *
   * @return lista de episodios
   */
  public List<Episode> getEpisodes() { // Metodo getter que devolve a lista de episodios da serie
    return episodes; // Devolve a lista de episodios
  } // Fim do metodo getEpisodes

  /**
   * Adiciona um episodio a serie se ainda nao existir um com o mesmo numero.
   * Complexidade: O(n) no pior caso, porque percorre a lista de episodios.
   *
   * @param e episodio a adicionar
   */
  /*
   * DICIONARIO:
   * - for-each: ciclo que percorre todos os elementos de uma colecao um a um
   * - return: sai do metodo imediatamente sem adicionar o episodio duplicado
   * - getEpisodeNumber: metodo de Episode que devolve o numero do episodio
   * - add: metodo da interface List que adiciona um elemento no fim da lista
   */
  public void addEpisode(Episode e) { // Metodo que adiciona um episodio a lista da serie, evitando duplicados
    for (Episode episode : episodes) { // Percorre todos os episodios ja existentes na lista
      if (episode.getEpisodeNumber() == e.getEpisodeNumber()) { // Verifica se ja existe um episodio com o mesmo numero
        return; // Se encontrar um duplicado, sai do metodo sem adicionar
      } // Fim da verificacao de duplicado
    } // Fim do ciclo de verificacao
    episodes.add(e); // Se nao houver duplicado, adiciona o episodio no fim da lista
  } // Fim do metodo addEpisode

  /**
   * Devolve o tipo de conteudo.
   *
   * @return a string {@code Series}
   */
  /*
   * DICIONARIO:
   * - Override: anotacao que indica que estamos a redefinir um metodo herdado da classe pai
   * - getContentType: metodo abstrato definido em Content que e aqui implementado
   */
  @Override // Indica que estamos a redefinir o metodo abstrato getContentType herdado de Content
  public String getContentType() { // Metodo que identifica este conteudo como sendo do tipo serie
    return "Series"; // Devolve a string que identifica este objeto como uma serie
  } // Fim do metodo getContentType

  /**
   * Devolve detalhes da serie para apresentacao.
   *
   * @return descricao com numero de temporadas, episodios e estado
   */
  /*
   * DICIONARIO:
   * - Override: anotacao que indica que estamos a redefinir um metodo herdado
   * - operador ternario: expressao da forma (condicao) ? valorSeVerdadeiro : valorSeFalso
   * - null: valor especial que representa a ausencia de objeto
   * - size(): metodo de List que devolve o numero de elementos na lista
   * - toString(): metodo que converte o objeto do enum numa representacao em texto
   */
  @Override // Indica que estamos a redefinir o metodo abstrato getDetails herdado de Content
  public String getDetails() { // Metodo que devolve os detalhes especificos desta serie
    int epCount = (episodes == null) ? 0 : episodes.size(); // Se a lista de episodios for null usa 0; caso contrario conta os episodios reais
    String safeStatus = (status == null) ? "Unknown" : status.toString(); // Se o estado for null usa "Unknown"; caso contrario usa o valor real do enum
    return "Seasons: " + seasons + ", Episodes: " + epCount + ", Status: " + safeStatus; // Constroi e devolve uma string com o numero de temporadas, episodios e estado
  } // Fim do metodo getDetails

} // Fim da classe Series
