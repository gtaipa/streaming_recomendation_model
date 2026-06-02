package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model

/**
 * Enumera os papeis que um artista pode desempenhar num conteudo.
 */
/*
 * DICIONARIO:
 * - enum: tipo especial de classe que define um conjunto fixo de constantes (valores que nunca mudam)
 * - ArtistRole: nome deste enum; lista os papeis possiveis que um artista pode ter
 * - constante: valor fixo que nao pode ser alterado durante a execucao do programa
 */
public enum ArtistRole { // Define o enum ArtistRole com a lista de papeis que um artista pode desempenhar

    /** Artista que interpreta personagens. */
    ACTOR, // Representa o papel de ator ou atriz, a pessoa que interpreta personagens
    /** Artista que dirige a producao. */
    DIRECTOR, // Representa o papel de realizador ou diretor, a pessoa que dirige a producao
    /** Artista que narra o conteudo. */
    NARRATOR, // Representa o papel de narrador, a pessoa que conta a historia com a sua voz
    /** Artista que escreve o guiao ou roteiro. */
    WRITER; // Representa o papel de escritor ou argumentista, a pessoa que escreve o guiao ou roteiro

} // Fim do enum ArtistRole
