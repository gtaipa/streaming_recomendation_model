package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model

/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - IdGenerator: nome desta classe; e responsavel por gerar identificadores unicos para cada tipo de entidade
 * - private: nivel de acesso que so permite acesso dentro desta propria classe
 * - int: tipo de dados que guarda numeros inteiros
 * - String.format: metodo que cria texto formatado, substituindo marcadores por valores
 * - %04d: marcador de formato que insere um numero inteiro com pelo menos 4 digitos, preenchendo com zeros a esquerda
 * - pós-incremento (++): operador que devolve o valor atual e so depois incrementa em 1
 */
public class IdGenerator { // Define a classe IdGenerator que gera IDs unicos e sequenciais para diferentes tipos de entidades

    private int userCount; // Contador que guarda o proximo numero a usar para IDs de utilizadores
    private int contentCount; // Contador que guarda o proximo numero a usar para IDs de conteudos
    private int artistCount; // Contador que guarda o proximo numero a usar para IDs de artistas
    private int genreCount; // Contador que guarda o proximo numero a usar para IDs de generos

    /*
     * DICIONARIO:
     * - construtor: metodo especial que inicializa o objeto quando e criado
     * - this: referencia ao objeto atual
     */
    public IdGenerator() { // Construtor que inicializa todos os contadores a 1
        this.userCount = 1; // Comeca o contador de utilizadores no numero 1
        this.contentCount = 1; // Comeca o contador de conteudos no numero 1
        this.artistCount = 1; // Comeca o contador de artistas no numero 1
        this.genreCount = 1; // Comeca o contador de generos no numero 1
    } // Fim do construtor

    /*
     * DICIONARIO:
     * - String.format: cria uma string formatada a partir de um modelo e valores
     * - "USER-%04d": modelo que gera texto como "USER-0001", "USER-0002", etc.
     * - userCount++: devolve o valor atual de userCount e depois soma 1 para o proximo uso
     */
    public String nextUserId() { // Metodo que gera e devolve o proximo ID de utilizador
        return String.format("USER-%04d", userCount++); // Cria o ID no formato "USER-XXXX" e avanca o contador para o proximo
    } // Fim do metodo nextUserId

    /*
     * DICIONARIO:
     * - "CONT-%04d": modelo que gera texto como "CONT-0001", "CONT-0002", etc.
     */
    public String nextContentId() { // Metodo que gera e devolve o proximo ID de conteudo
        return String.format("CONT-%04d", contentCount++); // Cria o ID no formato "CONT-XXXX" e avanca o contador
    } // Fim do metodo nextContentId

    /*
     * DICIONARIO:
     * - "ARTIST-%04d": modelo que gera texto como "ARTIST-0001", "ARTIST-0002", etc.
     */
    public String nextArtistId() { // Metodo que gera e devolve o proximo ID de artista
        return String.format("ARTIST-%04d", artistCount++); // Cria o ID no formato "ARTIST-XXXX" e avanca o contador
    } // Fim do metodo nextArtistId

    /*
     * DICIONARIO:
     * - "GENRE-%04d": modelo que gera texto como "GENRE-0001", "GENRE-0002", etc.
     */
    public String nextGenreId() { // Metodo que gera e devolve o proximo ID de genero
        return String.format("GENRE-%04d", genreCount++); // Cria o ID no formato "GENRE-XXXX" e avanca o contador
    } // Fim do metodo nextGenreId

    /*
     * DICIONARIO:
     * - reset: repor todos os contadores ao valor inicial, util para testes
     */
    public void reset() { // Metodo que repoe todos os contadores ao valor inicial 1
        this.userCount = 1; // Repoe o contador de utilizadores para 1
        this.contentCount = 1; // Repoe o contador de conteudos para 1
        this.artistCount = 1; // Repoe o contador de artistas para 1
        this.genreCount = 1; // Repoe o contador de generos para 1
    } // Fim do metodo reset
} // Fim da classe IdGenerator
