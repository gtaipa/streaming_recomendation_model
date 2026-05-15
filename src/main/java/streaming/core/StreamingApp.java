package streaming.core;

import streaming.db.StreamingDB;
import streaming.model.Content;
import streaming.model.Genre;

import java.util.List;

public class StreamingApp {

    public static void main(String[] args) {
        // Criamos uma instância da tua base de dados
        StreamingDB db = new StreamingDB();

        System.out.println("--- Iniciando Testes da StreamingDB ---");

        // 1. Criar dados de exemplo
        Genre acao = new Genre("G1", "Ação");
        //Content filme1 = new Content("F1", "Gladiador", 2000, acao);
        //Content filme2 = new Content("F2", "Top Gun", 2022, acao);

        // 2. Testar Adição
        //db.addContent(filme1);
        //db.addContent(filme2);
        System.out.println("Filmes adicionados com sucesso.");

        // 3. Testar Pesquisa por Género (R3 - Red-Black BST)
        List<Content> listaAcao = db.searchByGenre(acao.getName());
        System.out.println("Encontrados " + listaAcao.size() + " filmes de Ação.");
        // Deve imprimir 2

        // 4. Testar Remoção e Consistência (Limpeza dos índices)
        db.removeContent("F1");
        listaAcao = db.searchByGenre(acao.getName());
        System.out.println("Após remover F1, restam " + listaAcao.size() + " filmes de Ação.");
        // Deve imprimir 1

        if (db.validateConsistency()) {
            System.out.println(" Base de dados consistente!");
        } else {
            System.out.println(" Erro de consistência detetado!");
        }
    }
}
