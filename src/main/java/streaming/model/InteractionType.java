package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model

/**
 * Enumera os tipos de interacao que um utilizador pode realizar.
 */
/*
 * DICIONARIO:
 * - enum: tipo especial de classe que define um conjunto fixo de valores possiveis
 * - InteractionType: nome deste enum; lista os tipos de interacao que um utilizador pode fazer na plataforma
 * - constante: valor que nao muda durante a execucao do programa
 */
public enum InteractionType { // Define o enum InteractionType com os tipos de acoes que um utilizador pode realizar

    /** Visualizacao de um conteudo. */
    WATCH, // O utilizador assistiu a um conteudo (deu play e viu)
    /** Classificacao atribuida a um conteudo. */
    RATE, // O utilizador avaliou um conteudo (deu uma nota ou classificacao)
    /** Clique ou demonstracao de interesse num conteudo. */
    CLICK, // O utilizador clicou num conteudo (demonstrou interesse ao clicar)
    /** Relacao social em que um utilizador segue outro. */
    FOLLOW // O utilizador passou a seguir outro utilizador (relacao social entre pessoas)

} // Fim do enum InteractionType
