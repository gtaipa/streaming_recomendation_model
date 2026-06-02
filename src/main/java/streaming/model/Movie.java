package streaming.model; // Declara que esta classe pertence ao pacote streaming.model

import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas (ano, mes, dia, hora, minuto, segundo)
import java.util.ArrayList; // Importa a classe que implementa listas dinamicas que crescem automaticamente
import java.util.List; // Importa a interface que define o contrato de uma lista ordenada de elementos

/**
 * Representa um filme da plataforma, com duracao, realizador e elenco.
 */
/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - extends: palavra-chave que indica que esta classe herda atributos e metodos de outra classe
 * - Content: classe pai que contem os campos comuns a todos os tipos de conteudo
 * - Movie: nome desta classe; representa um filme com duracao e realizador
 * - private: nivel de acesso que so permite acesso dentro desta propria classe
 * - int: tipo de dados para numeros inteiros
 * - Artist: classe que representa um artista (pode ser realizador ou ator)
 * - List: colecao ordenada de elementos que pode crescer dinamicamente
 */
public class Movie extends Content { // Define a classe Movie que herda de Content, representando um filme

  /** Duracao do filme em minutos. */
  private int duration; // Guarda a duracao do filme em minutos

  /** Realizador do filme. */
  private Artist director; // Guarda o artista que realizou o filme

  /** Elenco do filme. */
  private List<Artist> cast; // Guarda a lista de atores que participaram no filme

  /**
   * Cria um filme com os seus dados essenciais.
   *
   * @param id identificador unico do filme
   * @param createdAt data e hora de criacao
   * @param title titulo do filme
   * @param releaseYear ano de lancamento
   * @param genre genero do filme
   * @param region regiao associada ao filme
   * @param duration duracao em minutos
   * @param director realizador do filme
   */
  /*
   * DICIONARIO:
   * - construtor: metodo especial chamado quando se cria um novo objeto
   * - super: chamada ao construtor da classe pai (Content) para inicializar os campos herdados
   * - this: referencia ao objeto atual que esta a ser criado
   * - new ArrayList<>(): cria uma nova lista vazia pronta a receber elementos
   * - 0.0: valor inicial para a classificacao media (zero porque o filme e novo)
   * - 0: valor inicial para o total de visualizacoes (zero porque o filme e novo)
   */
  public Movie(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region,
      int duration, Artist director) { // Construtor que cria um novo filme com todos os seus dados essenciais
    super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // Chama o construtor da classe pai Content com valores iniciais para nota e visualizacoes
    this.duration = duration; // Guarda a duracao em minutos recebida como parametro
    this.director = director; // Guarda o realizador recebido como parametro
    this.cast = new ArrayList<>(); // Inicializa a lista de elenco como vazia; os atores sao adicionados depois
  } // Fim do construtor

  /**
   * Devolve a duracao do filme em minutos.
   *
   * @return duracao em minutos
   */
  /*
   * DICIONARIO:
   * - getter: metodo que devolve o valor de um atributo privado
   * - return: palavra-chave que devolve um valor ao codigo que chamou este metodo
   */
  public int getDuration() { // Metodo getter que devolve a duracao do filme em minutos
    return duration; // Devolve o valor do atributo duration
  } // Fim do metodo getDuration

  /**
   * Devolve o realizador do filme.
   *
   * @return realizador do filme
   */
  public Artist getDirector() { // Metodo getter que devolve o realizador do filme
    return director; // Devolve o valor do atributo director
  } // Fim do metodo getDirector

  /**
   * Devolve o elenco do filme.
   *
   * @return lista de atores do filme
   */
  public List<Artist> getCast() { // Metodo getter que devolve a lista de atores do filme
    return cast; // Devolve a lista de atores
  } // Fim do metodo getCast

  /**
   * Devolve o tipo de conteudo.
   *
   * @return a string {@code Movie}
   */
  /*
   * DICIONARIO:
   * - Override: anotacao que indica que estamos a redefinir um metodo herdado da classe pai
   * - getContentType: metodo abstrato definido em Content que e aqui implementado
   */
  @Override // Indica que estamos a redefinir o metodo abstrato getContentType herdado de Content
  public String getContentType() { // Metodo que identifica este conteudo como sendo do tipo filme
    return "Movie"; // Devolve a string que identifica este objeto como um filme
  } // Fim do metodo getContentType

  /**
   * Devolve detalhes do filme para apresentacao.
   *
   * @return descricao com duracao e nome do realizador
   */
  /*
   * DICIONARIO:
   * - Override: anotacao que indica que estamos a redefinir um metodo herdado
   * - operador ternario: expressao da forma (condicao) ? valorSeVerdadeiro : valorSeFalso
   * - null: valor especial que representa a ausencia de objeto
   * - NullPointerException: erro que ocorre quando se tenta usar null como se fosse um objeto
   */
  @Override // Indica que estamos a redefinir o metodo abstrato getDetails herdado de Content
  public String getDetails() { // Metodo que devolve os detalhes especificos deste filme
    String directorName = (director == null) ? "Unknown" : director.getName(); // Se o realizador for null usa "Unknown" para evitar erro; caso contrario usa o nome real
    return "Duration: " + duration + " min, Director: " + directorName; // Constroi e devolve uma string com a duracao e o nome do realizador
  } // Fim do metodo getDetails

  /**
   * Adiciona um artista ao elenco do filme.
   *
   * @param artist artista a adicionar ao elenco
   */
  /*
   * DICIONARIO:
   * - add: metodo da interface List que adiciona um elemento no fim da lista
   * - this.cast: referencia ao atributo cast deste objeto
   */
  public void addCast(Artist artist) { // Metodo que adiciona um artista a lista de elenco do filme
    this.cast.add(artist); // Adiciona o artista recebido no fim da lista de elenco
  } // Fim do metodo addCast

  /**
   * Devolve uma representacao textual do filme.
   *
   * @return texto com o titulo e ano de lancamento do filme
   */
  /*
   * DICIONARIO:
   * - Override: anotacao que indica que estamos a redefinir o metodo toString herdado de Object
   * - toString: metodo que converte o objeto numa representacao em texto legivel
   */
  @Override // Indica que estamos a redefinir o metodo toString herdado da classe Object
  public String toString() { // Metodo que devolve uma representacao textual compacta do filme
    return "Movie: " + getTitle() + " (" + getReleaseYear() + ")"; // Constroi e devolve uma string com o titulo e o ano de lancamento
  } // Fim do metodo toString

} // Fim da classe Movie
