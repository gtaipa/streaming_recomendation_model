package streaming.model; // Declara que esta classe pertence ao pacote 'streaming.model', que agrupa todas as classes de modelo da aplicação.

import java.time.LocalDate; // Importa a classe LocalDate para representar datas (apenas dia, mês e ano).
import java.time.LocalDateTime; // Importa a classe LocalDateTime para representar datas e horas com precisão.
import java.util.ArrayList; // Importa a classe ArrayList para criar listas dinâmicas de objetos.
import java.util.List; // Importa a interface List, que define uma coleção ordenada de elementos.

/**
 * Representa um utilizador da plataforma de streaming.
 * <p>
 * Esta classe armazena dados de perfil, credenciais de segurança (hash da password) e relacionamentos sociais (seguidores).
 * A maioria das operações possui complexidade de tempo O(1), exceto as verificações em listas,
 * que podem ter complexidade linear (O(n)) em relação ao tamanho da coleção.
 */
public class User extends Entity { // A classe User estende Entity, herdando atributos e métodos base para entidades do sistema.

    /** Nome de utilizador único da conta. */
    private String username; // Armazena o nome de utilizador.

    /** Endereço de email do utilizador. */
    private String email; // Armazena o endereço de email do utilizador.

    /** Hash seguro da password do utilizador. */
    private String passwordHash; // Armazena o hash da password, nunca a password em texto simples, por segurança.

    /** Região geográfica do utilizador (ex.: PT, US). */
    private String region; // Armazena a região do utilizador.

    /** Data em que o utilizador se registou na plataforma. */
    private LocalDate registrationDate; // Armazena a data de registo do utilizador.

    /** Lista de géneros preferidos pelo utilizador. */
    private List<Genre> preferences; // Contém os géneros de conteúdo que o utilizador prefere.

    /** Lista de outros utilizadores que este utilizador segue. */
    private List<User> following; // Contém referências aos utilizadores que são seguidos.

    /**
     * Construtor para criar uma nova instância de utilizador.
     * <p>
     * Complexidade: {@code O(1)}.
     *
     * @param id identificador único do utilizador
     * @param createdAt data e hora de criação do registo do utilizador
     * @param username nome de utilizador
     * @param email endereço de email do utilizador
     * @param passwordHash hash da password do utilizador
     */
    public User(String id, LocalDateTime createdAt, String username, String email, String passwordHash) {
        // Chama o construtor da classe pai (Entity) para inicializar o ID e a data de criação.
        super(id, createdAt);
        this.username = username; // Atribui o nome de utilizador fornecido.
        this.email = email; // Atribui o endereço de email fornecido.
        this.passwordHash = passwordHash; // Atribui o hash da password fornecido.
        // Inicializa as listas de preferências e seguidores como vazias para evitar NullPointerExceptions.
        this.preferences = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    /**
     * Permite a este utilizador seguir outro utilizador, caso ainda não o esteja a seguir.
     * <p>
     * Complexidade: {@code O(n)} no pior caso, devido à verificação {@link List#contains(Object)}
     * que percorre a lista de utilizadores seguidos.
     *
     * @param u o utilizador a ser seguido
     */
    public void follow(User u) {
        // Verifica se o utilizador a seguir não é nulo e se ainda não está na lista de 'following' para evitar duplicações.
        if (u != null && !this.following.contains(u)) {
            this.following.add(u); // Adiciona o utilizador à lista de seguidos.
        }
    }

    /**
     * Retorna a lista de utilizadores que este utilizador está a seguir.
     *
     * @return uma lista de objetos User que representa os utilizadores seguidos.
     */
    public List<User> getFollowing() {
        return following; // Devolve a lista de utilizadores atualmente seguidos.
    }

    /**
     * Retorna a lista de géneros de conteúdo preferidos por este utilizador.
     *
     * @return uma lista de objetos Genre que representa as preferências do utilizador.
     */
    public List<Genre> getPreferences() {
        return preferences; // Devolve a lista de géneros preferidos.
    }

    /**
     * Retorna o hash da password do utilizador.
     *
     * @return uma String contendo o hash da password.
     */
    public String getPasswordHash() {
        return passwordHash; // Devolve o hash seguro da password.
    }

    /**
     * Retorna o nome de utilizador.
     *
     * @return uma String contendo o nome de utilizador.
     */
    public String getUsername() {
        return username; // Devolve o nome de utilizador.
    }

    /**
     * Define um novo nome de utilizador para esta conta.
     *
     * @param username o novo nome de utilizador a ser definido.
     */
    public void setUsername(String username) {
        this.username = username; // Atualiza o nome de utilizador com o valor fornecido.
    }

    /**
     * Retorna o endereço de email do utilizador.
     *
     * @return uma String contendo o endereço de email.
     */
    public String getEmail() {
        return email; // Devolve o endereço de email do utilizador.
    }

    /**
     * Define um novo endereço de email para esta conta.
     *
     * @param email o novo endereço de email a ser definido.
     */
    public void setEmail(String email) {
        this.email = email; // Atualiza o endereço de email com o valor fornecido.
    }

    /**
     * Define um novo hash da password para o utilizador.
     *
     * @param passwordHash o novo hash da password a ser definido.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash; // Atualiza o hash da password com o valor fornecido.
    }

    /**
     * Retorna a região geográfica do utilizador.
     *
     * @return uma String contendo a região do utilizador.
     */
    public String getRegion() {
        return region; // Devolve a região do utilizador.
    }

    /**
     * Define a região geográfica do utilizador.
     *
     * @param region a nova região a ser definida.
     */
    public void setRegion(String region) {
        this.region = region; // Atualiza a região do utilizador com o valor fornecido.
    }

    /**
     * Retorna a data de registo do utilizador.
     *
     * @return um objeto LocalDate representando a data de registo.
     */
    public LocalDate getRegistrationDate() {
        return registrationDate; // Devolve a data de registo.
    }

    /**
     * Define uma nova data de registo para o utilizador.
     * Geralmente usado em operações de administração ou testes.
     *
     * @param registrationDate a nova data de registo a ser definida.
     */
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate; // Atualiza a data de registo com o valor fornecido.
    }

    /**
     * Representação textual do utilizador.
     *
     * @return texto resumido do utilizador
     */
    @Override // ♻️ Reescrevemos a função de criar texto que já vem por defeito.
    public String toString() { // 🖨️ A cena de "print" num formato bonitinho.
        return "User:" + username + ",E-mail:" + email; // 📝 Manda a string "User: o-teu-nome, E-mail: o-teu-email". Simples e direto.
    } // 🚪 E acabámos com o Utilizador! Adeus!
} // 🛑 Fim da classe User.
