package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model

/**
 * Enumera os estados possiveis de uma serie.
 */
/*
 * DICIONARIO:
 * - enum: tipo especial de classe que define um conjunto fixo de valores possiveis
 * - SeriesStatus: nome deste enum; indica em que estado se encontra uma serie
 * - constante: valor que nao muda durante a execucao do programa
 */
public enum SeriesStatus { // Define o enum SeriesStatus com os estados possiveis de uma serie

    /** Serie ainda em producao ou com novos episodios previstos. */
    ONGOING, // A serie ainda esta em producao, com novos episodios a serem lancados
    /** Serie terminada de forma natural. */
    ENDED, // A serie terminou de forma natural, a historia foi concluida
    /** Serie cancelada antes da conclusao natural. */
    CANCELLED; // A serie foi cancelada antes de terminar naturalmente

} // Fim do enum SeriesStatus
