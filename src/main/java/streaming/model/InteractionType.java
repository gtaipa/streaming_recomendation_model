package streaming.model; // Definição do pacote pertencente à camada de modelo do sistema.

/**
 * Tipologia de interações suportadas pelo sistema de recomendação.
 * <p>
 * Define as categorias de ações que um utilizador pode executar, influenciando o cálculo de afinidade.
 */
public enum InteractionType { // Tipo enumerado que encapsula a taxonomia das ações de utilizador.

    /** O utilizador visualizou o conteúdo (consumo de media). */
    WATCH, 

    /** O utilizador atribuiu uma classificação explícita ao conteúdo. */
    RATE, 

    /** O utilizador demonstrou interesse preliminar através de um clique. */
    CLICK, 

    /** O utilizador estabeleceu uma ligação de seguimento social com outro utilizador. */
    FOLLOW 

}

/*
 * DICIONÁRIO DE TERMOS TÉCNICOS
 * - Enum (Tipo Enumerado): Estrutura de dados que permite definir um conjunto restrito de constantes nomeadas, garantindo segurança de tipos (type-safety).
 * - Taxonomia: Sistema de classificação organizado de forma hierárquica ou categórica.
 * - Afinidade: Métrica de proximidade ou interesse entre entidades (utilizador-conteúdo ou utilizador-utilizador).
 */
