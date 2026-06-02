package streaming.core; // Declara que esta classe pertence ao pacote streaming.core

import streaming.db.StreamingDB; // Importa a base de dados em memoria com as entidades da plataforma
import streaming.model.Content; // Importa a classe abstrata base para todos os tipos de conteudo
import streaming.model.Genre; // Importa a classe que representa um genero de conteudo
import streaming.model.User; // Importa a classe que representa um utilizador da plataforma

/**
 * Motor de recomendacao da plataforma de streaming.
 *
 * Nesta fase, a classe expoe a ligacao a StreamingDB e serve de ponto de extensao
 * para algoritmos de recomendacao (ex.: trending por genero, recomendacoes por similaridade).
 */
/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - RecommendationEngine: nome desta classe; motor responsavel por gerar recomendacoes
 * - StreamingDB: base de dados em memoria que serve de fonte de dados para as recomendacoes
 * - public: nivel de acesso que permite que qualquer classe aceda a este atributo ou metodo
 * - placeholder: implementacao provisoria que sera expandida em fases futuras
 */
public class RecommendationEngine { // Define a classe RecommendationEngine que serve de motor de recomendacoes da plataforma

    /** Base de dados com as entidades da plataforma. */
    public StreamingDB db; // Atributo que guarda a referencia a base de dados usada para gerar recomendacoes

    // Os metodos de recomendacao serao implementados em fases futuras:
    // - recommend(User u): devolve conteudos recomendados para um utilizador
    // - trending(Genre genre): devolve os conteudos mais populares de um genero
    // - followersWatched(User u, Content c): devolve os seguidores que viram um conteudo

} // Fim da classe RecommendationEngine
