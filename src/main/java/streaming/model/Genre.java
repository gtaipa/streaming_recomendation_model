package streaming.model; // Declara que este ficheiro pertence ao pacote streaming.model

import java.io.Serializable; // Importa a interface que permite transformar objetos em bytes para gravar em disco ou enviar pela rede
import java.util.Objects; // Importa metodos utilitarios para verificar null e comparar objetos de forma segura

/**
 * Representa um genero de conteudo da plataforma.
 */
/*
 * DICIONARIO:
 * - class: modelo que define a estrutura e comportamento de um objeto
 * - implements: palavra-chave que indica que esta classe segue o contrato de uma interface
 * - Serializable: interface que marca a classe como convertivel em bytes para ser gravada ou transmitida
 * - serialVersionUID: numero de versao que garante compatibilidade ao ler objetos gravados anteriormente
 * - final: palavra-chave que impede que o valor de um atributo seja alterado depois de definido
 * - static: pertence a classe e nao a cada objeto individual
 * - Objects.requireNonNull: metodo que verifica se um valor nao e null e lanca erro se for
 * - NullPointerException: erro que ocorre quando se tenta usar um valor null como se fosse um objeto
 */
public class Genre implements Serializable { // Define a classe Genre que implementa Serializable, representando um genero de conteudo (ex: Acao, Comedia)
    /** Identificador da versao usada na serializacao. */
    private static final long serialVersionUID = 1L; // Numero de versao fixo para garantir que objetos gravados podem ser lidos corretamente

    /** Identificador unico do genero. */
    private final String id; // Identificador unico do genero; como e final, nunca pode ser alterado depois de criado
    /** Nome do genero. */
    private String name; // Nome do genero (ex: "Terror", "Drama"); pode ser alterado

    /**
     * Cria um genero com identificador e nome obrigatorios.
     *
     * @param id identificador unico do genero
     * @param name nome do genero
     */
    /*
     * DICIONARIO:
     * - construtor: metodo especial chamado quando se cria um novo objeto
     * - Objects.requireNonNull: verifica que o valor nao e null; se for, lanca uma excecao com a mensagem indicada
     * - this: referencia ao objeto que esta a ser criado neste momento
     */
    public Genre(String id, String name) { // Construtor que cria um novo genero com um id e um nome obrigatorios
        this.id = Objects.requireNonNull(id, "id"); // Guarda o id e garante que nao e null; se for null, lanca erro
        this.name = Objects.requireNonNull(name, "name"); // Guarda o nome e garante que nao e null; se for null, lanca erro
    } // Fim do construtor

    /**
     * Devolve o identificador do genero.
     *
     * @return identificador do genero
     */
    /*
     * DICIONARIO:
     * - getter: metodo que devolve o valor de um atributo
     * - return: devolve um valor ao codigo que chamou este metodo
     */
    public String getId() { return id; } // Metodo getter que devolve o identificador do genero

    /**
     * Devolve o nome do genero.
     *
     * @return nome do genero
     */
    public String getName() { return name; } // Metodo getter que devolve o nome do genero

    /**
     * Atualiza o nome do genero.
     *
     * @param name novo nome do genero
     */
    /*
     * DICIONARIO:
     * - setter: metodo que altera o valor de um atributo
     * - Objects.requireNonNull: garante que o novo valor nao e null antes de o guardar
     */
    public void setName(String name) { this.name = Objects.requireNonNull(name, "name"); } // Metodo setter que atualiza o nome do genero, garantindo que nao e null

    /**
     * Compara generos pelo identificador.
     *
     * @param o objeto a comparar
     * @return {@code true} se os identificadores forem iguais
     */
    /*
     * DICIONARIO:
     * - Override: anotacao que indica que estamos a redefinir um metodo herdado da classe Object
     * - equals: metodo que verifica se dois objetos sao logicamente iguais
     * - instanceof: operador que verifica se um objeto e de um determinado tipo
     * - boolean: tipo que so pode ser true ou false
     */
    @Override // Indica que estamos a redefinir o metodo equals da classe Object
    public boolean equals(Object o) { // Metodo que compara se este genero e igual a outro objeto
        if (this == o) return true; // Se apontam para o mesmo objeto na memoria, sao iguais
        if (!(o instanceof Genre other)) return false; // Se o outro objeto nao for um Genre, nao sao iguais
        return id.equals(other.id); // Compara os ids; se forem iguais, os generos sao considerados o mesmo
    } // Fim do metodo equals

    /**
     * Calcula o codigo hash do genero.
     *
     * @return codigo hash baseado no identificador
     */
    /*
     * DICIONARIO:
     * - hashCode: metodo que gera um numero inteiro unico para o objeto, usado em estruturas como HashMap e HashSet
     */
    @Override // Indica que estamos a redefinir o metodo hashCode da classe Object
    public int hashCode() { return id.hashCode(); } // Devolve o hash code calculado a partir do id

    /**
     * Devolve uma representacao textual do genero.
     *
     * @return texto com identificador e nome
     */
    /*
     * DICIONARIO:
     * - toString: metodo que converte o objeto numa representacao em texto legivel
     */
    @Override // Indica que estamos a redefinir o metodo toString da classe Object
    public String toString() { // Metodo que devolve o genero como texto formatado
        return "Genre{id='" + id + "', name='" + name + "'}"; // Constroi e devolve uma string com o id e o nome do genero
    } // Fim do metodo toString
} // Fim da classe Genre
