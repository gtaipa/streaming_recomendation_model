package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model


/**
 * Representa um episodio individual pertencente a uma serie.
 */
/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - Episode: nome desta classe; representa um episodio individual de uma serie
 * - private: nivel de acesso que so permite acesso dentro desta propria classe
 * - int: tipo de dados para numeros inteiros
 * - float: tipo de dados para numeros decimais com menor precisao que double
 * - String: tipo de dados que guarda texto
 */
public class Episode { // Define a classe Episode que representa um episodio de uma serie

    /** Numero da temporada a que este episodio pertence. */
    private int seasonNumber; // Guarda o numero da temporada a que este episodio pertence
    /** Numero do episodio dentro da temporada. */
    private int episodeNumber; // Guarda o numero do episodio dentro da temporada
    /** Titulo do episodio. */
    private String title; // Guarda o titulo do episodio
    /** Duracao do episodio em minutos. */
    private int duration; // Guarda a duracao do episodio em minutos
    /** Classificacao do episodio numa escala de 0 a 10. */
    private float rating; // Guarda a classificacao do episodio numa escala de 0 a 10

    /**
     * Cria um episodio com temporada, numero, titulo, duracao e classificacao.
     *
     * @param seasonNumber numero da temporada
     * @param episodeNumber numero do episodio
     * @param title titulo do episodio
     * @param duration duracao em minutos
     * @param rating classificacao inicial
     */
    /*
     * DICIONARIO:
     * - construtor: metodo especial que e chamado quando se cria um novo objeto
     * - this: referencia ao objeto atual que esta a ser criado
     * - parametro: valor que e passado para dentro do construtor
     */
    public Episode (int seasonNumber, int episodeNumber, String title, int duration, float rating) { // Construtor que cria um novo episodio com todos os seus dados
        this.seasonNumber = seasonNumber; // Guarda o numero da temporada recebido
        this.episodeNumber = episodeNumber; // Guarda o numero do episodio recebido
        this.title = title; // Guarda o titulo recebido
        this.duration = duration; // Guarda a duracao em minutos recebida
        this.rating = rating; // Guarda a classificacao recebida
    } // Fim do construtor

    /**
     * Devolve o numero da temporada.
     *
     * @return numero da temporada
     */
    /*
     * DICIONARIO:
     * - getter: metodo que devolve o valor de um atributo privado
     * - return: devolve um valor ao codigo que chamou este metodo
     */
    public int getSeasonNumber() { // Metodo getter que devolve o numero da temporada
        return seasonNumber; // Devolve o valor do atributo seasonNumber
    } // Fim do metodo getSeasonNumber

    /**
     * Devolve o numero do episodio.
     *
     * @return numero do episodio
     */
    public int getEpisodeNumber() { // Metodo getter que devolve o numero do episodio
        return episodeNumber; // Devolve o valor do atributo episodeNumber
    } // Fim do metodo getEpisodeNumber

    /**
     * Devolve o titulo do episodio.
     *
     * @return titulo do episodio
     */
    public String getTitle() { // Metodo getter que devolve o titulo do episodio
        return title; // Devolve o valor do atributo title
    } // Fim do metodo getTitle

    /**
     * Devolve a duracao do episodio.
     *
     * @return duracao em minutos
     */
    public int getDuration() { // Metodo getter que devolve a duracao em minutos
        return duration; // Devolve o valor do atributo duration
    } // Fim do metodo getDuration

    /**
     * Devolve a classificacao do episodio.
     *
     * @return classificacao do episodio
     */
    public float getRating() { // Metodo getter que devolve a classificacao do episodio
        return rating; // Devolve o valor do atributo rating
    } // Fim do metodo getRating

    /**
     * Atualiza a classificacao do episodio.
     *
     * @param rating nova classificacao entre 0 e 10
     * @throws IllegalArgumentException se a classificacao estiver fora do intervalo 0 a 10
     */
    /*
     * DICIONARIO:
     * - setter: metodo que altera o valor de um atributo
     * - IllegalArgumentException: tipo de erro que e lancado quando um argumento invalido e passado a um metodo
     * - throw: palavra-chave que lanca um erro (excecao) e interrompe a execucao normal
     */
    public void setRating(float rating) { // Metodo setter que atualiza a classificacao do episodio
        if (rating < 0 || rating > 10) { // Verifica se o valor esta fora do intervalo valido (0 a 10)
            throw new IllegalArgumentException("Rating must be between 0 and 10"); // Lanca um erro informando que o rating tem de estar entre 0 e 10
        } // Fim da verificacao
        this.rating = rating; // Se o valor for valido, atualiza o atributo rating
    } // Fim do metodo setRating

    /**
     * Devolve uma representacao textual resumida do episodio.
     *
     * @return texto com temporada, episodio, titulo e duracao
     */
    /*
     * DICIONARIO:
     * - Override: anotacao que indica que estamos a redefinir um metodo herdado
     * - toString: metodo que converte o objeto numa representacao em texto
     */
    @Override // Indica que estamos a redefinir o metodo toString da classe Object
    public String toString() { // Metodo que devolve uma representacao em texto do episodio
        return "Season"+seasonNumber+"Episode"+episodeNumber+"-"+title+"("+duration+"min)"; // Constroi e devolve uma string compacta com temporada, numero, titulo e duracao
    } // Fim do metodo toString

} // Fim da classe Episode
