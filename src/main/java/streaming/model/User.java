package streaming.model; // Declara que esta classe pertence ao pacote streaming.model

import java.time.LocalDate; // Importa a classe que representa datas simples (apenas ano, mes e dia, sem hora)
import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas (ano, mes, dia, hora, minuto, segundo)
import java.util.ArrayList; // Importa a classe que cria listas dinamicas que crescem conforme necessario
import java.util.List; // Importa a interface que define o comportamento de uma lista ordenada de elementos

/**
 * Representa um utilizador da plataforma de streaming.
 */
/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - extends: palavra-chave que indica heranca; User herda os atributos e metodos de Entity
 * - Entity: classe pai que fornece id e createdAt
 * - private: nivel de acesso que so permite acesso dentro desta propria classe
 * - String: tipo de dados que guarda texto
 * - LocalDate: tipo que guarda uma data sem hora (ex: 2024-03-15)
 * - List: colecao ordenada de elementos que pode crescer dinamicamente
 * - ArrayList: implementacao concreta de List baseada num array que cresce automaticamente
 * - Genre: classe que representa um genero de conteudo (ex: Acao, Drama)
 * - User: classe que representa outro utilizador, usado para relacoes de seguimento
 */
public class User extends Entity { // Define a classe User que herda de Entity, representando um utilizador da plataforma

    /** Nome de utilizador unico da conta. */
    private String username; // Guarda o nome de utilizador unico desta conta

    /** Endereco de email do utilizador. */
    private String email; // Guarda o endereco de email do utilizador

    /** Hash seguro da password do utilizador. */
    private String passwordHash; // Guarda o hash da password; nunca a password em texto simples, por seguranca

    /** Regiao geografica do utilizador (ex.: PT, US). */
    private String region; // Guarda a regiao geografica do utilizador

    /** Data em que o utilizador se registou na plataforma. */
    private LocalDate registrationDate; // Guarda a data em que o utilizador se registou na plataforma

    /** Lista de generos preferidos pelo utilizador. */
    private List<Genre> preferences; // Guarda a lista de generos de conteudo que o utilizador prefere

    /** Lista de outros utilizadores que este utilizador segue. */
    private List<User> following; // Guarda a lista de utilizadores que este utilizador segue

    /**
     * Cria um novo utilizador com os dados basicos de autenticacao.
     *
     * @param id identificador unico do utilizador
     * @param createdAt data e hora de criacao do registo
     * @param username nome de utilizador
     * @param email endereco de email do utilizador
     * @param passwordHash hash da password do utilizador
     */
    /*
     * DICIONARIO:
     * - construtor: metodo especial que e chamado quando se cria um novo objeto; inicializa os atributos
     * - super: chamada ao construtor da classe pai (Entity) para inicializar id e createdAt
     * - this: referencia ao proprio objeto que esta a ser criado
     * - new ArrayList<>(): cria uma nova lista vazia pronta a receber elementos
     */
    public User(String id, LocalDateTime createdAt, String username, String email, String passwordHash) { // Construtor que cria um novo utilizador com os dados basicos de autenticacao
        super(id, createdAt); // Chama o construtor da classe pai Entity para guardar o id e a data de criacao
        this.username = username; // Guarda o nome de utilizador recebido no atributo username
        this.email = email; // Guarda o endereco de email recebido no atributo email
        this.passwordHash = passwordHash; // Guarda o hash da password recebido no atributo passwordHash
        this.preferences = new ArrayList<>(); // Inicializa a lista de preferencias como vazia
        this.following = new ArrayList<>(); // Inicializa a lista de seguidos como vazia
    } // Fim do construtor

    /**
     * Permite a este utilizador seguir outro utilizador, caso ainda nao o esteja a seguir.
     *
     * @param u o utilizador a ser seguido
     */
    /*
     * DICIONARIO:
     * - contains: metodo de List que verifica se um elemento ja existe na lista
     * - add: metodo de List que adiciona um elemento no fim da lista
     * - null: valor especial que representa a ausencia de objeto
     */
    public void follow(User u) { // Metodo que adiciona um utilizador a lista de seguidos, evitando duplicados
        if (u != null && !this.following.contains(u)) { // Verifica se o utilizador nao e null e ainda nao esta na lista
            this.following.add(u); // Adiciona o utilizador a lista de seguidos
        } // Fim da verificacao
    } // Fim do metodo follow

    /**
     * Devolve a lista de utilizadores que este utilizador esta a seguir.
     *
     * @return lista de utilizadores seguidos
     */
    /*
     * DICIONARIO:
     * - getter: metodo que devolve o valor de um atributo privado
     * - return: palavra-chave que devolve um valor ao codigo que chamou este metodo
     */
    public List<User> getFollowing() { // Metodo getter que devolve a lista de utilizadores seguidos
        return following; // Devolve a lista de utilizadores que este utilizador esta a seguir
    } // Fim do metodo getFollowing

    /**
     * Devolve a lista de generos preferidos pelo utilizador.
     *
     * @return lista de generos preferidos
     */
    public List<Genre> getPreferences() { // Metodo getter que devolve a lista de generos preferidos
        return preferences; // Devolve a lista de generos preferidos pelo utilizador
    } // Fim do metodo getPreferences

    /**
     * Devolve o hash da password do utilizador.
     *
     * @return hash da password
     */
    public String getPasswordHash() { // Metodo getter que devolve o hash da password
        return passwordHash; // Devolve o hash seguro da password
    } // Fim do metodo getPasswordHash

    /**
     * Devolve o nome de utilizador.
     *
     * @return nome de utilizador
     */
    public String getUsername() { // Metodo getter que devolve o nome de utilizador
        return username; // Devolve o nome de utilizador
    } // Fim do metodo getUsername

    /**
     * Define um novo nome de utilizador para esta conta.
     *
     * @param username o novo nome de utilizador a ser definido
     */
    /*
     * DICIONARIO:
     * - setter: metodo que atualiza o valor de um atributo
     */
    public void setUsername(String username) { // Metodo setter que atualiza o nome de utilizador
        this.username = username; // Substitui o nome de utilizador pelo valor recebido
    } // Fim do metodo setUsername

    /**
     * Devolve o endereco de email do utilizador.
     *
     * @return endereco de email
     */
    public String getEmail() { // Metodo getter que devolve o endereco de email
        return email; // Devolve o endereco de email do utilizador
    } // Fim do metodo getEmail

    /**
     * Define um novo endereco de email para esta conta.
     *
     * @param email o novo endereco de email a ser definido
     */
    public void setEmail(String email) { // Metodo setter que atualiza o endereco de email
        this.email = email; // Substitui o endereco de email pelo valor recebido
    } // Fim do metodo setEmail

    /**
     * Define um novo hash da password para o utilizador.
     *
     * @param passwordHash o novo hash da password a ser definido
     */
    public void setPasswordHash(String passwordHash) { // Metodo setter que atualiza o hash da password
        this.passwordHash = passwordHash; // Substitui o hash da password pelo valor recebido
    } // Fim do metodo setPasswordHash

    /**
     * Devolve a regiao geografica do utilizador.
     *
     * @return regiao do utilizador
     */
    public String getRegion() { // Metodo getter que devolve a regiao geografica do utilizador
        return region; // Devolve a regiao do utilizador
    } // Fim do metodo getRegion

    /**
     * Define a regiao geografica do utilizador.
     *
     * @param region a nova regiao a ser definida
     */
    public void setRegion(String region) { // Metodo setter que atualiza a regiao do utilizador
        this.region = region; // Substitui a regiao pelo valor recebido
    } // Fim do metodo setRegion

    /**
     * Devolve a data de registo do utilizador.
     *
     * @return data de registo
     */
    public LocalDate getRegistrationDate() { // Metodo getter que devolve a data de registo do utilizador
        return registrationDate; // Devolve a data de registo
    } // Fim do metodo getRegistrationDate

    /**
     * Define uma nova data de registo para o utilizador.
     *
     * @param registrationDate a nova data de registo a ser definida
     */
    public void setRegistrationDate(LocalDate registrationDate) { // Metodo setter que atualiza a data de registo
        this.registrationDate = registrationDate; // Substitui a data de registo pelo valor recebido
    } // Fim do metodo setRegistrationDate

    /**
     * Devolve uma representacao textual do utilizador.
     *
     * @return texto com o nome de utilizador e o email
     */
    /*
     * DICIONARIO:
     * - Override: anotacao que indica que estamos a redefinir o metodo toString herdado da classe Object
     * - toString: metodo que converte o objeto numa representacao em texto legivel
     */
    @Override // Indica que estamos a redefinir o metodo toString herdado da classe Object
    public String toString() { // Metodo que devolve uma representacao textual compacta do utilizador
        return "User:" + username + ",E-mail:" + email; // Constroi e devolve uma string com o nome e o email do utilizador
    } // Fim do metodo toString

} // Fim da classe User
