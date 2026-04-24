package streaming.core;

import java.io.*;
import streaming.db.StreamingDB;

public class FileManager {

  // ==========================================
  // MÉTODOS DE SERIALIZAÇÃO (Ficheiros Binários)
  // ==========================================

  public void serialize(StreamingDB db, String path) {
    // Usamos try-with-resources para garantir que o ficheiro é fechado no fim
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
      oos.writeObject(db);
      System.out.println("Base de dados guardada com sucesso em: " + path);
    } catch (IOException e) {
      System.err.println("Erro ao serializar a base de dados: " + e.getMessage());
    }
  }

  public StreamingDB deserialize(String path) {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
      StreamingDB db = (StreamingDB) ois.readObject();
      System.out.println("Base de dados carregada com sucesso de: " + path);
      return db;
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Erro ao desserializar a base de dados: " + e.getMessage());
      return null;
    }
  }

  // ==========================================
  // MÉTODOS DE FICHEIROS DE TEXTO (TXT / CSV)
  // ==========================================

  public void exportTxt(StreamingDB db, String path) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {

      // Exemplo de como farias a exportação:
      // Tens de criar um método na StreamingDB que devolva todos os utilizadores/conteúdos,
      // iterar sobre eles e escrever linha a linha.
      writer.println("TIPO;ID;NOME;..."); // Cabeçalho exemplo

      // for (User u : db.getAllUsers()) {
      //     writer.println("USER;" + u.getId() + ";" + u.getUsername() + ";" + u.getRegion());
      // }

      System.out.println("Exportação para TXT concluída em: " + path);
    } catch (IOException e) {
      System.err.println("Erro ao exportar para TXT: " + e.getMessage());
    }
  }

  public StreamingDB importTxt(String path) {
    StreamingDB db = new StreamingDB();

    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;

      while ((line = reader.readLine()) != null) {
        // Aqui vais ler o ficheiro linha a linha e "cortar" pelos separadores
        // Exemplo se os dados estiverem separados por ponto e vírgula (;):

        // String[] dados = line.split(";");
        // if (dados[0].equals("USER")) {
        //     User u = new User(dados[1], dados[2], ...);
        //     db.addUser(u);
        // }
      }
      System.out.println("Importação de TXT concluída de: " + path);

    } catch (IOException e) {
      System.err.println("Erro ao ler ficheiro TXT: " + e.getMessage());
    }

    return db;
  }
}