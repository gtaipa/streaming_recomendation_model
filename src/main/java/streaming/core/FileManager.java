package streaming.core;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import streaming.db.StreamingDB;
import streaming.db.StreamingGraph;
import streaming.db.StreamingGraphAPI;
import streaming.model.*;

/**
 * R10 + R11 - Gestão de ficheiros.
 *
 * R10: Importação/exportação de dados em ficheiros de texto (.txt).
 * R11: Serialização/desserialização binária (.bin).
 *
 * FORMATO DOS FICHEIROS TXT:
 * Cada linha representa uma entidade ou relação, com campos separados por ";".
 * A primeira palavra identifica o tipo:
 * GENRE;id;name
 * ARTIST;id;createdAt;name;nationality;birthDate;gender
 * MOVIE;id;createdAt;title;releaseYear;genreId;region;duration;directorId
 * SERIES;id;createdAt;title;releaseYear;genreId;region;seasons;status
 * EPISODE;seriesId;seasonNum;episodeNum;title;duration;rating
 * USER;id;createdAt;username;email;passwordHash;region;registrationDate
 * INTERACTION;userId;contentId;type;timestamp;watchedPct;rating;weight
 * FOLLOW;followerId;followedId
 */
public class FileManager {

  // ========================= R11: SERIALIZAÇÃO BINÁRIA =========================

  /**
   * R11: Exporta o estado completo (DB + Grafo) para ficheiro binário.
   *
   * Estratégia: reutiliza o formato TXT (R10) mas escreve cada linha
   * como string binária via DataOutputStream. Assim evitamos problemas
   * com classes da algs4 que não implementam Serializable.
   */
  public void serializeBin(StreamingDB db, StreamingGraph graph, String path) {
    try (DataOutputStream dos = new DataOutputStream(
            new BufferedOutputStream(new FileOutputStream(path)))) {
      // Recolher todas as linhas no mesmo formato do exportTxt
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exportGenres(pw, db);
      exportArtists(pw, db);
      exportContents(pw, db);
      exportUsers(pw, db);
      exportInteractions(pw, graph);
      exportFollows(pw, db);
      pw.flush();

      // Linhas partidas de forma segura (ignora o \r no Windows)
      String[] lines = sw.toString().split("\\r?\\n");

      // Escrever número de linhas + cada linha em binário
      dos.writeInt(lines.length);
      for (String line : lines) {
        dos.writeUTF(line);
      }
      System.out.println("Serialização binária concluída em: " + path);
    } catch (IOException e) {
      System.err.println("Erro ao serializar binário: " + e.getMessage());
    }
  }

  /**
   * R11: Importa o estado completo de ficheiro binário para DB e Grafo.
   */
  public void deserializeBin(String path, StreamingDB db, StreamingGraphAPI api) {
    try (DataInputStream dis = new DataInputStream(
            new BufferedInputStream(new FileInputStream(path)))) {
      int numLines = dis.readInt();
      for (int i = 0; i < numLines; i++) {

        // Lê a linha e faz trim para limpar qualquer "lixo" invisível
        String line = dis.readUTF().trim();

        // Se a linha estiver vazia, ignora
        if (line.isEmpty()) continue;

        try {
          parseLine(line, db, api);
        } catch (Exception e) {
          System.err.println("Erro na linha binária " + (i + 1) + ": " + e.getMessage());
        }
      }
      System.out.println("Desserialização binária concluída de: " + path);
    } catch (IOException e) {
      System.err.println("Erro ao desserializar binário: " + e.getMessage());
    }
  }

  // ========================= R10: EXPORTAR TEXTO =========================

  /**
   * R10: Exporta todos os dados (DB + Grafo) para ficheiro de texto.
   * Ordem: Genres → Artists → Contents → Episodes → Users → Interactions → Follows
   */
  public void exportTxt(StreamingDB db, StreamingGraph graph, String path) {
    try (PrintWriter w = new PrintWriter(new FileWriter(path))) {
      exportGenres(w, db);
      exportArtists(w, db);
      exportContents(w, db);
      exportUsers(w, db);
      exportInteractions(w, graph);
      exportFollows(w, db);
      System.out.println("Exportação TXT concluída em: " + path);
    } catch (IOException e) {
      System.err.println("Erro ao exportar TXT: " + e.getMessage());
    }
  }

  private void exportGenres(PrintWriter w, StreamingDB db) {
    for (Genre g : db.listGenres()) {
      w.println("GENRE;" + g.getId() + ";" + g.getName());
    }
  }

  private void exportArtists(PrintWriter w, StreamingDB db) {
    for (Artist a : db.listArtists()) {
      w.println("ARTIST;" + a.getId() + ";" + a.getCreatedAt() + ";"
              + a.getName() + ";" + a.getNationality() + ";"
              + a.getBirthDate() + ";" + a.getGender());
    }
  }

  private void exportContents(PrintWriter w, StreamingDB db) {
    for (Content c : db.listContents()) {
      String genreId = c.getGenre() != null ? c.getGenre().getId() : "";
      if (c instanceof Movie m) {
        String dirId = m.getDirector() != null ? m.getDirector().getId() : "";
        w.println("MOVIE;" + m.getId() + ";" + m.getCreatedAt() + ";"
                + m.getTitle() + ";" + m.getReleaseYear() + ";"
                + genreId + ";" + m.getRegion() + ";"
                + m.getDuration() + ";" + dirId);
      } else if (c instanceof Series s) {
        // Vai buscar o status real da série em vez de forçar ONGOING
        w.println("SERIES;" + s.getId() + ";" + s.getCreatedAt() + ";"
                + s.getTitle() + ";" + s.getReleaseYear() + ";"
                + genreId + ";" + s.getRegion() + ";"
                + s.getSeasons() + ";" + s.getStatus().name());

        // Exportar episódios desta série
        for (Object obj : s.getEpisodes()) {
          Episode ep = (Episode) obj;
          w.println("EPISODE;" + s.getId() + ";"
                  + ep.getSeasonNumber() + ";" + ep.getEpisodeNumber() + ";"
                  + ep.getTitle() + ";" + ep.getDuration() + ";" + ep.getRating());
        }
      }
    }
  }

  private void exportUsers(PrintWriter w, StreamingDB db) {
    for (User u : db.listUsers()) {
      String region = u.getRegion() != null ? u.getRegion() : "";
      String regDate = u.getRegistrationDate() != null ? u.getRegistrationDate().toString() : "";
      w.println("USER;" + u.getId() + ";" + u.getCreatedAt() + ";"
              + u.getUsername() + ";" + u.getEmail() + ";"
              + u.getPasswordHash() + ";" + region + ";" + regDate);
    }
  }

  private void exportInteractions(PrintWriter w, StreamingGraph graph) {
    for (Interaction i : graph.getInteractions()) {
      if (i.getContent() == null || i.getUser() == null) continue;
      w.println("INTERACTION;" + i.getUser().getId() + ";" + i.getContent().getId() + ";"
              + i.getType() + ";" + i.getTimestamp() + ";"
              + i.getWatchedPct() + ";" + i.getRating() + ";" + i.getWeight());
    }
  }

  private void exportFollows(PrintWriter w, StreamingDB db) {
    for (User u : db.listUsers()) {
      for (User followed : u.getFollowing()) {
        w.println("FOLLOW;" + u.getId() + ";" + followed.getId());
      }
    }
  }

  // ========================= R10: IMPORTAR TEXTO =========================

  /**
   * R10: Importa dados de ficheiro de texto para DB e Grafo.
   *
   * @param path  caminho do ficheiro
   * @param db    a StreamingDB a popular
   * @param api   a StreamingGraphAPI para registar relações no grafo
   */
  public void importTxt(String path, StreamingDB db, StreamingGraphAPI api) {
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;
      int lineNum = 0;
      while ((line = reader.readLine()) != null) {
        lineNum++;
        line = line.trim();
        if (line.isEmpty()) continue;
        try {
          parseLine(line, db, api);
        } catch (Exception e) {
          System.err.println("Erro na linha " + lineNum + ": " + e.getMessage());
        }
      }
      System.out.println("Importação TXT concluída de: " + path);
    } catch (IOException e) {
      System.err.println("Erro ao ler ficheiro TXT: " + e.getMessage());
    }
  }

  /**
   * Interpreta uma linha do ficheiro e cria a entidade correspondente.
   */
  private void parseLine(String line, StreamingDB db, StreamingGraphAPI api) {
    String[] d = line.split(";", -1); // -1 para manter campos vazios
    switch (d[0]) {
      case "GENRE" -> parseGenre(d, db);
      case "ARTIST" -> parseArtist(d, db);
      case "MOVIE" -> parseMovie(d, db);
      case "SERIES" -> parseSeries(d, db);
      case "EPISODE" -> parseEpisode(d, db);
      case "USER" -> parseUser(d, db);
      case "INTERACTION" -> parseInteraction(d, db, api);
      case "FOLLOW" -> parseFollow(d, api);
      default -> System.err.println("Tipo desconhecido: " + d[0]);
    }
  }

  // --- Parsers individuais ---

  private void parseGenre(String[] d, StreamingDB db) {
    // GENRE;id;name
    db.addGenre(new Genre(d[1], d[2]));
  }

  private void parseArtist(String[] d, StreamingDB db) {
    // ARTIST;id;createdAt;name;nationality;birthDate;gender
    LocalDateTime createdAt = LocalDateTime.parse(d[2]);
    LocalDate birthDate = d[5].isEmpty() ? null : LocalDate.parse(d[5]);
    Artist a = new Artist(d[1], createdAt, d[3], d[4], birthDate, d[6]);
    db.addArtist(a);
  }

  private void parseMovie(String[] d, StreamingDB db) {
    // MOVIE;id;createdAt;title;releaseYear;genreId;region;duration;directorId
    LocalDateTime createdAt = LocalDateTime.parse(d[2]);
    Genre genre = d[5].isEmpty() ? null : db.getGenre(d[5]);
    int releaseYear = Integer.parseInt(d[4]);
    int duration = Integer.parseInt(d[7]);
    Artist director = d[8].isEmpty() ? null : db.getArtist(d[8]);
    Movie m = new Movie(d[1], createdAt, d[3], releaseYear, genre, d[6], duration, director);
    db.addContent(m);
  }

  private void parseSeries(String[] d, StreamingDB db) {
    // SERIES;id;createdAt;title;releaseYear;genreId;region;seasons;status
    LocalDateTime createdAt = LocalDateTime.parse(d[2]);
    Genre genre = d[5].isEmpty() ? null : db.getGenre(d[5]);
    int releaseYear = Integer.parseInt(d[4]);
    int seasons = Integer.parseInt(d[7]);
    SeriesStatus status = SeriesStatus.valueOf(d[8]);
    Series s = new Series(d[1], createdAt, d[3], releaseYear, genre, d[6], seasons, status);
    db.addContent(s);
  }

  private void parseEpisode(String[] d, StreamingDB db) {
    // EPISODE;seriesId;seasonNum;episodeNum;title;duration;rating
    Content c = db.getContent(d[1]);
    if (c instanceof Series s) {
      int seasonNum = Integer.parseInt(d[2]);
      int episodeNum = Integer.parseInt(d[3]);
      int duration = Integer.parseInt(d[5]);
      float rating = Float.parseFloat(d[6]);
      s.addEpisode(new Episode(seasonNum, episodeNum, d[4], duration, rating));
    }
  }

  private void parseUser(String[] d, StreamingDB db) {
    // USER;id;createdAt;username;email;passwordHash;region;registrationDate
    LocalDateTime createdAt = LocalDateTime.parse(d[2]);
    User u = new User(d[1], createdAt, d[3], d[4], d[5]);
    if (d.length > 6 && !d[6].isEmpty()) u.setRegion(d[6]);
    if (d.length > 7 && !d[7].isEmpty()) u.setRegistrationDate(LocalDate.parse(d[7]));
    db.addUser(u);
  }

  private void parseInteraction(String[] d, StreamingDB db, StreamingGraphAPI api) {
    // INTERACTION;userId;contentId;type;timestamp;watchedPct;rating;weight
    InteractionType type = InteractionType.valueOf(d[3]);
    LocalDateTime timestamp = LocalDateTime.parse(d[4]);
    double watchedPct = Double.parseDouble(d[5]);
    double rating = Double.parseDouble(d[6]);
    if (type == InteractionType.WATCH) {
      api.addWatch(d[1], d[2], watchedPct, rating, timestamp);
    } else if (type == InteractionType.RATE) {
      api.addRating(d[1], d[2], rating, timestamp);
    } else if (type == InteractionType.CLICK) {
      api.addClick(d[1], d[2], timestamp);
    }
  }

  private void parseFollow(String[] d, StreamingGraphAPI api) {
    // FOLLOW;followerId;followedId
    api.addFollow(d[1], d[2]);
  }
}