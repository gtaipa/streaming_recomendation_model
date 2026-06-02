package streaming.model; // Declara que esta classe pertence ao pacote streaming.model

import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas (ano, mes, dia, hora, minuto, segundo)
import java.util.List; // Importa a interface que define o contrato de uma lista ordenada de elementos

/**
 * Representa um documentario da plataforma, com duracao, topico e narrador.
 */
/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - extends: palavra-chave que indica que esta classe herda atributos e metodos de outra classe
 * - Content: classe pai que contem os campos comuns a todos os tipos de conteudo
 * - Documentary: nome desta classe; representa um documentario com topico e narrador
 * - private: nivel de acesso que so permite acesso dentro desta propria classe
 * - int: tipo de dados para numeros inteiros
 * - String: tipo de dados que guarda texto
 * - Artist: classe que representa um artista (neste caso o narrador do documentario)
 * - double: tipo de dados para numeros decimais com maior precisao
 * - List: colecao ordenada de elementos
 */
public class Documentary extends Content { // Define a classe Documentary que herda de Content, representando um documentario

  /** Duracao do documentario em minutos. */
  private int duration; // Guarda a duracao do documentario em minutos

  /** Topico ou tema principal do documentario. */
  private String topic; // Guarda o tema central do documentario (ex: "Natureza", "Historia")

  /** Narrador do documentario. */
  private Artist narrator; // Guarda o artista que narra o documentario

  /**
   * Cria um documentario com todos os seus dados.
   *
   * @param id identificador unico do documentario
   * @param createdAt data e hora de criacao
   * @param title titulo do documentario
   * @param releaseYear ano de lancamento
   * @param genre genero do documentario
   * @param region regiao associada ao documentario
   * @param avgRating classificacao media inicial
   * @param totalViews total de visualizacoes inicial
   * @param artists artistas associados ao documentario
   * @param duration duracao em minutos
   * @param topic topico ou tema principal
   * @param narrator narrador do documentario
   */
  /*
   * DICIONARIO:
   * - construtor: metodo especial chamado quando se cria um novo objeto
   * - super: chamada ao construtor da classe pai (Content) para inicializar os campos herdados
   * - this: referencia ao objeto atual que esta a ser criado
   * - parametro: valor que e passado para dentro do construtor para inicializar os atributos
   */
  public Documentary( // Construtor que cria um novo documentario com todos os seus dados
      String id, // Identificador unico do documentario
      LocalDateTime createdAt, // Data e hora em que o registo foi criado
      String title, // Titulo do documentario
      int releaseYear, // Ano de lancamento do documentario
      Genre genre, // Genero do documentario
      String region, // Regiao geografica associada ao documentario
      double avgRating, // Classificacao media inicial
      int totalViews, // Numero total de visualizacoes inicial
      List<Artist> artists, // Lista de artistas associados ao documentario
      int duration, // Duracao do documentario em minutos
      String topic, // Topico ou tema principal do documentario
      Artist narrator // Artista que narra o documentario
  ) { // Abre o bloco do construtor
    super(id, createdAt, title, releaseYear, genre, region, avgRating, totalViews, artists); // Chama o construtor da classe pai Content para inicializar os campos herdados
    this.duration = duration; // Guarda a duracao em minutos recebida como parametro
    this.topic = topic; // Guarda o topico recebido como parametro
    this.narrator = narrator; // Guarda o narrador recebido como parametro
  } // Fim do construtor

  /**
   * Devolve o topico ou tema do documentario.
   *
   * @return topico do documentario
   */
  /*
   * DICIONARIO:
   * - getter: metodo que devolve o valor de um atributo privado
   * - return: palavra-chave que devolve um valor ao codigo que chamou este metodo
   */
  public String getTopic() { // Metodo getter que devolve o topico do documentario
    return topic; // Devolve o valor do atributo topic
  } // Fim do metodo getTopic

  /**
   * Devolve o narrador do documentario.
   *
   * @return narrador do documentario
   */
  public Artist getNarrator() { // Metodo getter que devolve o narrador do documentario
    return narrator; // Devolve o valor do atributo narrator
  } // Fim do metodo getNarrator

  /**
   * Devolve a duracao do documentario em minutos.
   *
   * @return duracao em minutos
   */
  public int getDuration() { // Metodo getter que devolve a duracao do documentario em minutos
    return duration; // Devolve o valor do atributo duration
  } // Fim do metodo getDuration

  /**
   * Devolve o tipo de conteudo.
   *
   * @return a string {@code Documentary}
   */
  /*
   * DICIONARIO:
   * - Override: anotacao que indica que estamos a redefinir um metodo herdado da classe pai
   * - getContentType: metodo abstrato definido em Content que e aqui implementado
   */
  @Override // Indica que estamos a redefinir o metodo abstrato getContentType herdado de Content
  public String getContentType() { // Metodo que identifica este conteudo como sendo do tipo documentario
    return "Documentary"; // Devolve a string que identifica este objeto como um documentario
  } // Fim do metodo getContentType

  /**
   * Devolve detalhes do documentario para apresentacao.
   *
   * @return descricao com topico, duracao e nome do narrador
   */
  /*
   * DICIONARIO:
   * - Override: anotacao que indica que estamos a redefinir um metodo herdado
   * - operador ternario: expressao da forma (condicao) ? valorSeVerdadeiro : valorSeFalso
   * - null: valor especial que representa a ausencia de objeto
   * - getName: metodo de Artist que devolve o nome do artista
   */
  @Override // Indica que estamos a redefinir o metodo abstrato getDetails herdado de Content
  public String getDetails() { // Metodo que devolve os detalhes especificos deste documentario
    String narratorName = (narrator == null) ? "Unknown" : narrator.getName(); // Se o narrador for null usa "Unknown" para evitar erro; caso contrario usa o nome real
    String safeTopic = (topic == null) ? "Unknown" : topic; // Se o topico for null usa "Unknown"; caso contrario usa o valor real
    return "Topic: " + safeTopic + ", Duration: " + duration + " min, Narrator: " + narratorName; // Constroi e devolve uma string com o topico, duracao e nome do narrador
  } // Fim do metodo getDetails

} // Fim da classe Documentary
