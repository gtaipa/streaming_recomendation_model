package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model

import java.time.LocalDate; // Importa a classe para representar datas simples (apenas ano, mes e dia)
import java.time.LocalDateTime; // Importa a classe para representar data e hora juntas
import java.time.Period; // Importa a classe que calcula intervalos de tempo entre datas (anos, meses, dias)
import java.util.ArrayList; // Importa a classe que cria listas dinamicas que crescem conforme necessario
import java.util.List; // Importa a interface que define o comportamento de uma lista ordenada de elementos

/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - extends: palavra-chave que indica heranca; Artist herda os atributos e metodos de Entity
 * - Entity: classe pai que fornece id e createdAt
 * - private: nivel de acesso que so permite acesso dentro desta propria classe
 * - String: tipo de dados que guarda texto
 * - LocalDate: tipo que guarda uma data sem hora
 * - List: colecao ordenada de elementos; pode crescer ou encolher
 * - ArrayList: implementacao concreta de List baseada num array que cresce automaticamente
 * - ArtistRole: enum que define os papeis possiveis (ACTOR, DIRECTOR, etc.)
 * - Movie: classe que representa um filme
 */
public class Artist extends Entity { // Define a classe Artist que herda de Entity, representando um artista da plataforma

    private String name; // Guarda o nome do artista
    private String nationality; // Guarda a nacionalidade do artista (ex: PT, US, BR)
    private LocalDate birthDate; // Guarda a data de nascimento do artista
    private String gender; // Guarda o genero do artista em texto livre
    private List<ArtistRole> artistRoles; // Lista com os papeis que o artista desempenha (pode ter varios ao mesmo tempo)
    private List<Movie> movies; // Lista de filmes em que o artista participou

    /*
     * DICIONARIO:
     * - construtor: metodo especial que inicializa um novo objeto quando e criado
     * - super: chamada ao construtor da classe pai (Entity) para inicializar id e createdAt
     * - this: referencia ao objeto atual
     * - new ArrayList<>(): cria uma nova lista vazia pronta a receber elementos
     */
    public Artist(String id, LocalDateTime createdAt, String name, String nationality, LocalDate birthDate, String gender) { // Construtor que cria um novo artista com todos os dados basicos
        super(id, createdAt); // Chama o construtor da classe pai Entity para guardar o id e a data de criacao
        this.name = name; // Guarda o nome recebido no atributo name
        this.nationality = nationality; // Guarda a nacionalidade recebida no atributo nationality
        this.birthDate = birthDate; // Guarda a data de nascimento recebida no atributo birthDate
        this.gender = gender; // Guarda o genero recebido no atributo gender
        this.artistRoles = new ArrayList<>(); // Inicializa a lista de papeis como uma lista vazia
        this.movies = new ArrayList<>(); // Inicializa a lista de filmes como uma lista vazia
    } // Fim do construtor

    /*
     * DICIONARIO:
     * - getter: metodo que devolve o valor de um atributo privado
     * - return: devolve um valor ao codigo que chamou este metodo
     */
    public String getName() { // Metodo getter que devolve o nome do artista
        return name; // Devolve o valor do atributo name
    } // Fim do metodo getName

    public String getNationality() { // Metodo getter que devolve a nacionalidade do artista
        return nationality; // Devolve o valor do atributo nationality
    } // Fim do metodo getNationality

    public LocalDate getBirthDate() { // Metodo getter que devolve a data de nascimento do artista
        return birthDate; // Devolve o valor do atributo birthDate
    } // Fim do metodo getBirthDate

    public String getGender() { // Metodo getter que devolve o genero do artista
        return gender; // Devolve o valor do atributo gender
    } // Fim do metodo getGender

    /*
     * DICIONARIO:
     * - Period: classe que representa um periodo de tempo (diferenca entre duas datas)
     * - Period.between: metodo que calcula a diferenca entre duas datas
     * - LocalDate.now(): devolve a data de hoje
     * - getYears(): extrai apenas a parte dos anos do periodo calculado
     * - null: valor especial que significa ausencia de objeto
     */
    public int getAge() { // Metodo que calcula e devolve a idade atual do artista em anos
        if (birthDate != null) { // Verifica se a data de nascimento existe (nao e null)
            return Period.between(birthDate, LocalDate.now()).getYears(); // Calcula a diferenca entre a data de nascimento e hoje, e devolve so os anos
        } // Fim do if
        return 0; // Se a data de nascimento for null, devolve 0 porque nao e possivel calcular
    } // Fim do metodo getAge

    public List<ArtistRole> getArtistRoles() { // Metodo getter que devolve a lista de papeis do artista
        return artistRoles; // Devolve a lista de papeis
    } // Fim do metodo getArtistRoles

    public List<Movie> getMovies() { // Metodo getter que devolve a lista de filmes do artista
        return movies; // Devolve a lista de filmes
    } // Fim do metodo getMovies

    /*
     * DICIONARIO:
     * - Override: anotacao que indica que estamos a redefinir um metodo herdado
     * - toString: metodo que converte o objeto numa representacao em texto
     */
    @Override // Indica que estamos a redefinir o metodo toString herdado da classe Object
    public String toString() { // Metodo que devolve uma representacao em texto do artista
        return "Artist: " + name; // Devolve o texto "Artist: " seguido do nome do artista
    } // Fim do metodo toString
} // Fim da classe Artist
