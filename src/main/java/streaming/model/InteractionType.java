package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model

/*
 * DICIONARIO:
 * - enum: tipo especial de classe que define um conjunto fixo de valores possiveis
 * - InteractionType: nome deste enum; lista os tipos de interacao que um utilizador pode fazer na plataforma
 * - constante: valor que nao muda durante a execucao do programa
 */
public enum InteractionType { // Define o enum InteractionType com os tipos de acoes que um utilizador pode realizar

    WATCH, // O utilizador assistiu a um conteudo (deu play e viu)
    RATE, // O utilizador avaliou um conteudo (deu uma nota ou classificacao)
    CLICK, // O utilizador clicou num conteudo (demonstrou interesse ao clicar)
    FOLLOW // O utilizador passou a seguir outro utilizador (relacao social entre pessoas)

} // Fim do enum InteractionType
