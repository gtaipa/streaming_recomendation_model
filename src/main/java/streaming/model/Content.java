package streaming.model; // Declara que esta classe pertence ao pacote streaming.model

import java.time.LocalDateTime; // Importa a classe LocalDateTime para trabalhar com datas e horas
import java.util.List; // Importa a interface List para usar listas de objetos

/**
 * Classe abstrata que define os campos e comportamentos comuns a todos os conteudos.
 */
/*
 * DICIONARIO:
 * - abstract: classe que nao pode ser instanciada diretamente, serve apenas como modelo para outras classes
 * - extends: indica que esta classe herda de outra classe, recebendo os seus atributos e metodos
 * - Entity: classe pai que contem campos comuns como identificador e data de criacao
 * - Content: classe base que define os campos comuns a todos os tipos de conteudo (filmes, series, documentarios)
 * - private: modificador de acesso que impede o acesso direto ao campo fora da classe
 * - String: tipo de dados que representa texto
 * - int: tipo de dados que representa numeros inteiros
 * - double: tipo de dados que representa numeros decimais com maior precisao
 * - Genre: tipo enumerado ou classe que representa o genero do conteudo
 * - List: estrutura de dados que guarda uma colecao ordenada de elementos
 * - Artist: classe que representa um artista associado ao conteudo
 */
public abstract class Content extends Entity { // Define a classe abstrata Content que herda de Entity

  /** Titulo do conteudo. */
  private String title; // Campo privado que guarda o titulo do conteudo

  /** Ano de lancamento do conteudo. */
  private int releaseYear; // Campo privado que guarda o ano de lancamento do conteudo

  /** Genero associado ao conteudo. */
  private Genre genre; // Campo privado que guarda o genero do conteudo

  /** Regiao associada ao conteudo. */
  private String region; // Campo privado que guarda a regiao associada ao conteudo

  /** Classificacao media do conteudo, numa escala de 0 a 10. */
  private double avgRating; // Campo privado que guarda a classificacao media de 0 a 10

  /** Numero total de visualizacoes do conteudo. */
  private int totalViews; // Campo privado que guarda o numero total de visualizacoes

  /** Lista de artistas associados ao conteudo. */
  private List<Artist> artists; // Campo privado que guarda a lista de artistas associados ao conteudo

  /**
   * Cria um conteudo com os dados comuns a filmes, series e documentarios.
   *
   * @param id identificador unico do conteudo
   * @param createdAt data e hora de criacao
   * @param title titulo do conteudo
   * @param releaseYear ano de lancamento
   * @param genre genero do conteudo
   * @param region regiao associada ao conteudo
   * @param avgRating classificacao media
   * @param totalViews numero total de visualizacoes
   * @param artists artistas associados ao conteudo
   */
  /*
   * DICIONARIO:
   * - protected: modificador de acesso que permite acesso apenas pela propria classe e pelas subclasses
   * - super: chamada ao construtor da classe pai para inicializar os campos herdados
   * - this: referencia ao objeto atual, usada para distinguir campos da classe dos parametros
   * - LocalDateTime: classe que representa uma data e hora sem fuso horario
   */
  protected Content(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region, // Define o construtor protegido com todos os parametros necessarios para criar um conteudo
      double avgRating, int totalViews, List<Artist> artists) { // Continuacao dos parametros do construtor
    super(id, createdAt); // Chama o construtor da classe pai Entity para guardar o identificador e a data de criacao
    this.title = title; // Atribui o titulo recebido ao campo da classe
    this.releaseYear = releaseYear; // Atribui o ano de lancamento recebido ao campo da classe
    this.genre = genre; // Atribui o genero recebido ao campo da classe
    this.region = region; // Atribui a regiao recebida ao campo da classe
    this.avgRating = avgRating; // Atribui a classificacao media recebida ao campo da classe
    this.totalViews = totalViews; // Atribui o total de visualizacoes recebido ao campo da classe
    this.artists = artists; // Atribui a lista de artistas recebida ao campo da classe
  } // Fim do construtor de Content

  /**
   * Devolve o titulo do conteudo.
   *
   * @return titulo do conteudo
   */
  /*
   * DICIONARIO:
   * - public: modificador de acesso que permite acesso de qualquer classe
   * - return: devolve um valor ao codigo que chamou o metodo
   * - getter: metodo que devolve o valor de um campo privado
   */
  public String getTitle() { // Metodo publico que devolve o titulo do conteudo
    return title; // Devolve o valor do campo title
  } // Fim do metodo getTitle

  /**
   * Devolve o ano de lancamento do conteudo.
   *
   * @return ano de lancamento
   */
  /*
   * DICIONARIO:
   * - int: tipo de dados que representa um numero inteiro
   * - return: devolve um valor ao codigo que chamou o metodo
   */
  public int getReleaseYear() { // Metodo publico que devolve o ano de lancamento
    return releaseYear; // Devolve o valor do campo releaseYear
  } // Fim do metodo getReleaseYear

  /**
   * Devolve o genero do conteudo.
   *
   * @return genero do conteudo
   */
  /*
   * DICIONARIO:
   * - Genre: tipo que representa o genero do conteudo como acao, terror, comedia, etc
   * - return: devolve um valor ao codigo que chamou o metodo
   */
  public Genre getGenre() { // Metodo publico que devolve o genero do conteudo
    return genre; // Devolve o valor do campo genre
  } // Fim do metodo getGenre

  /**
   * Devolve a regiao do conteudo.
   *
   * @return regiao associada ao conteudo
   */
  /*
   * DICIONARIO:
   * - String: tipo de dados que representa texto
   * - return: devolve um valor ao codigo que chamou o metodo
   */
  public String getRegion() { // Metodo publico que devolve a regiao do conteudo
    return region; // Devolve o valor do campo region
  } // Fim do metodo getRegion

  /**
   * Devolve a classificacao media do conteudo.
   *
   * @return classificacao media
   */
  /*
   * DICIONARIO:
   * - double: tipo de dados decimal com precisao dupla
   * - return: devolve um valor ao codigo que chamou o metodo
   */
  public double getAvgRating() { // Metodo publico que devolve a classificacao media
    return avgRating; // Devolve o valor do campo avgRating
  } // Fim do metodo getAvgRating

  /**
   * Devolve o total de visualizacoes do conteudo.
   *
   * @return total de visualizacoes
   */
  /*
   * DICIONARIO:
   * - int: tipo de dados que representa um numero inteiro
   * - return: devolve um valor ao codigo que chamou o metodo
   */
  public int getTotalViews() { // Metodo publico que devolve o total de visualizacoes
    return totalViews; // Devolve o valor do campo totalViews
  } // Fim do metodo getTotalViews

  /**
   * Devolve a lista de artistas associados ao conteudo.
   *
   * @return artistas associados
   */
  /*
   * DICIONARIO:
   * - List: colecao ordenada de elementos que permite duplicados
   * - Artist: classe que representa um artista como ator ou realizador
   * - return: devolve um valor ao codigo que chamou o metodo
   */
  public List<Artist> getArtists() { // Metodo publico que devolve a lista de artistas associados
    return artists; // Devolve o valor do campo artists
  } // Fim do metodo getArtists

  /**
   * Devolve detalhes especificos do tipo concreto de conteudo.
   *
   * @return descricao com detalhes especificos
   */
  /*
   * DICIONARIO:
   * - abstract: metodo sem corpo que obriga as subclasses a implementarem a sua propria versao
   * - String: tipo de dados que representa texto
   * - return: devolve um valor ao codigo que chamou o metodo
   */
  public abstract String getDetails( // Metodo abstrato que obriga as subclasses a devolver detalhes especificos do conteudo

  ); // Fim da declaracao do metodo abstrato getDetails

  /**
   * Devolve o tipo concreto de conteudo.
   *
   * @return tipo de conteudo
   */
  /*
   * DICIONARIO:
   * - abstract: metodo sem corpo que obriga as subclasses a implementarem a sua propria versao
   * - String: tipo de dados que representa texto
   * - getContentType: metodo que devolve o tipo de conteudo como texto
   */
  public abstract String getContentType(); // Metodo abstrato que obriga as subclasses a devolver o tipo de conteudo
} // Fim da classe Content
