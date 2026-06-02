package streaming.model; // Declara que esta classe pertence ao pacote streaming.model

import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas (ano, mes, dia, hora, minuto, segundo)
import java.util.Objects; // Importa metodos utilitarios para comparar objetos de forma segura e gerar hash codes

/*
 * DICIONARIO:
 * - abstract: palavra-chave que impede criar objetos desta classe diretamente; serve apenas como base para outras
 * - class: modelo ou planta que define a estrutura e comportamento de um tipo de objeto
 * - Entity: nome desta classe; representa qualquer coisa que tenha identidade no sistema
 * - protected: nivel de acesso que permite que a propria classe e as suas subclasses acedam ao atributo
 * - String: tipo de dados que guarda texto (sequencia de caracteres)
 * - LocalDateTime: tipo de dados que guarda uma data e hora sem fuso horario
 */
public abstract class Entity { // Define a classe abstrata Entity, que e a classe base de todas as entidades do sistema

    protected String id; // Atributo que guarda o identificador unico da entidade (visivel pelas subclasses)
    protected LocalDateTime createdAt; // Atributo que guarda a data e hora em que esta entidade foi criada

    /*
     * DICIONARIO:
     * - construtor: metodo especial que e chamado quando se cria um novo objeto; inicializa os atributos
     * - this: referencia ao proprio objeto que esta a ser usado neste momento
     * - parametro: valor que e passado para dentro de um metodo ou construtor
     */
    public Entity(String id, LocalDateTime createdAt) { // Construtor da classe Entity, recebe o id e a data de criacao
        this.id = id; // Guarda o id recebido como parametro no atributo id deste objeto
        this.createdAt = createdAt; // Guarda a data de criacao recebida como parametro no atributo createdAt deste objeto
    } // Fim do construtor

    /*
     * DICIONARIO:
     * - getter: metodo que devolve o valor de um atributo privado ou protegido
     * - return: palavra-chave que devolve um valor ao codigo que chamou este metodo
     * - public: nivel de acesso que permite que qualquer classe aceda a este metodo
     */
    public String getId() { // Metodo getter que devolve o identificador unico desta entidade
        return id; // Devolve o valor do atributo id
    } // Fim do metodo getId

    /*
     * DICIONARIO:
     * - LocalDateTime: tipo que combina data e hora num so objeto (ex: 2025-03-15T14:30:00)
     */
    public LocalDateTime getCreatedAt() { // Metodo getter que devolve a data e hora de criacao desta entidade
        return createdAt; // Devolve o valor do atributo createdAt
    } // Fim do metodo getCreatedAt

    /*
     * DICIONARIO:
     * - Override: anotacao que indica que estamos a substituir um metodo herdado da classe pai (Object)
     * - equals: metodo que compara se dois objetos sao logicamente iguais
     * - boolean: tipo de dados que so pode ser true (verdadeiro) ou false (falso)
     * - instanceof: operador que verifica se um objeto e de um determinado tipo
     * - Objects.equals: metodo utilitario que compara dois objetos de forma segura, tratando valores null
     */
    @Override // Indica que estamos a redefinir o metodo equals que vem da classe Object
    public boolean equals(Object o) { // Metodo que verifica se este objeto e igual a outro objeto recebido
        if (this == o) return true; // Se ambos apontam para o mesmo espaco na memoria, sao o mesmo objeto, logo sao iguais
        if (!(o instanceof Entity other)) return false; // Se o outro objeto nao for uma Entity, entao nao podem ser iguais
        return Objects.equals(this.id, other.id); // Compara os ids das duas entidades; se forem iguais, as entidades sao consideradas iguais
    } // Fim do metodo equals

    /*
     * DICIONARIO:
     * - hashCode: metodo que gera um numero inteiro a partir do objeto, usado para organizar objetos em estruturas rapidas como HashMap
     * - Objects.hash: metodo utilitario que calcula o hashCode a partir de um ou mais valores
     * - int: tipo de dados que guarda numeros inteiros
     */
    @Override // Indica que estamos a redefinir o metodo hashCode que vem da classe Object
    public int hashCode() { // Metodo que gera um codigo numerico unico baseado no id, util para estruturas de dados rapidas
        return Objects.hash(id); // Calcula e devolve o hash code usando apenas o atributo id
    } // Fim do metodo hashCode

} // Fim da classe Entity