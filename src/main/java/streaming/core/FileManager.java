package streaming.core; // Declara que esta classe pertence ao pacote streaming.core

import java.io.*; // Importa as classes de entrada e saida (FileWriter, BufferedReader, DataOutputStream, etc.)
import java.time.LocalDate; // Importa a classe que representa datas simples (apenas ano, mes e dia)
import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas
import streaming.db.StreamingDB; // Importa a base de dados em memoria com as entidades da plataforma
import streaming.db.StreamingGraph; // Importa o grafo de entidades e relacoes da plataforma
import streaming.db.StreamingGraphAPI; // Importa a API publica de gestao do grafo
import streaming.model.*; // Importa todas as classes do pacote streaming.model (Genre, Artist, Movie, Series, etc.)

/**
 * Gestao de ficheiros para importacao e exportacao de dados da plataforma.
 *
 * Implementa importacao e exportacao em texto (.txt) e em formato binario (.bin).
 */
/*
 * DICIONARIO:
 * - serializacao: processo de converter objetos em memoria para um formato que pode ser guardado em ficheiro
 * - deserializacao: processo inverso; le dados do ficheiro e reconstroi os objetos em memoria
 * - DataOutputStream: escreve tipos primitivos e texto em formato binario
 * - DataInputStream: le tipos primitivos e texto de formato binario
 * - BufferedOutputStream: encapsula um stream de saida para escrever em blocos, melhorando o desempenho
 * - BufferedInputStream: encapsula um stream de entrada para ler em blocos, melhorando o desempenho
 * - FileOutputStream: stream de saida para escrever num ficheiro
 * - FileInputStream: stream de entrada para ler de um ficheiro
 * - PrintWriter: escreve texto formatado em streams de saida
 * - BufferedReader: le texto linha a linha de forma eficiente
 * - split(";", -1): divide uma string pelo separador ";", mantendo os campos vazios no fim
 * - trim: remove espacos em branco no inicio e no fim de uma string
 * - LocalDateTime.parse: converte uma string no formato ISO para um objeto LocalDateTime
 * - LocalDate.parse: converte uma string no formato ISO para um objeto LocalDate
 * - Integer.parseInt: converte uma string para um numero inteiro
 * - Double.parseDouble: converte uma string para um numero decimal
 * - Float.parseFloat: converte uma string para um numero decimal de menor precisao
 * - SeriesStatus.valueOf: converte uma string para o valor do enum SeriesStatus
 * - InteractionType.valueOf: converte uma string para o valor do enum InteractionType
 */
public class FileManager { // Define a classe FileManager responsavel pela gestao de ficheiros da plataforma

    /**
     * Exporta o estado completo (base de dados e grafo) para um ficheiro binario.
     *
     * @param db a base de dados com as entidades a exportar
     * @param graph o grafo com as relacoes a exportar
     * @param path o caminho do ficheiro de destino
     */
    /*
     * DICIONARIO:
     * - DataOutputStream: escreve dados em formato binario
     * - dos.writeInt: escreve um numero inteiro em formato binario
     * - dos.writeUTF: escreve uma string em formato UTF-8 binario
     * - StringWriter: acumula texto em memoria como se fosse um ficheiro
     * - split("\\r?\\n"): divide o texto em linhas, tratando tanto \n (Unix) como \r\n (Windows)
     */
    public void serializeBin(StreamingDB db, StreamingGraph graph, String path) { // Metodo que serializa todos os dados para um ficheiro binario
        try (DataOutputStream dos = new DataOutputStream( // Cria o stream de saida binario
                new BufferedOutputStream(new FileOutputStream(path)))) { // Encapsula com buffer para melhor desempenho
            StringWriter sw = new StringWriter(); // Cria um acumulador de texto em memoria
            PrintWriter pw = new PrintWriter(sw); // Cria um escritor de texto para o acumulador
            exportGenres(pw, db); // Escreve todos os generos no formato TXT
            exportArtists(pw, db); // Escreve todos os artistas no formato TXT
            exportContents(pw, db); // Escreve todos os conteudos no formato TXT
            exportUsers(pw, db); // Escreve todos os utilizadores no formato TXT
            exportInteractions(pw, graph); // Escreve todas as interacoes no formato TXT
            exportFollows(pw, db); // Escreve todas as relacoes de seguimento no formato TXT
            pw.flush(); // Garante que todo o texto foi escrito no acumulador

            String[] lines = sw.toString().split("\\r?\\n"); // Divide o texto em linhas, tratando fins de linha Windows e Unix

            dos.writeInt(lines.length); // Escreve o numero total de linhas em binario
            for (String line : lines) { // Percorre cada linha do texto
                dos.writeUTF(line); // Escreve cada linha em formato UTF-8 binario
            } // Fim do ciclo de escrita
            System.out.println("Serializacao binaria concluida em: " + path); // Informa que a serializacao foi concluida
        } catch (IOException e) { // Captura erros de entrada/saida
            System.err.println("Erro ao serializar binario: " + e.getMessage()); // Imprime a mensagem de erro
        } // Fim do bloco try-catch
    } // Fim do metodo serializeBin

    /**
     * Importa o estado completo de um ficheiro binario para a base de dados e o grafo.
     *
     * @param path o caminho do ficheiro a importar
     * @param db a base de dados a popular com os dados importados
     * @param api a API do grafo para registar as relacoes importadas
     */
    /*
     * DICIONARIO:
     * - DataInputStream: le dados de formato binario
     * - dis.readInt: le um numero inteiro de formato binario
     * - dis.readUTF: le uma string de formato UTF-8 binario
     * - parseLine: metodo privado que interpreta uma linha e cria a entidade correspondente
     */
    public void deserializeBin(String path, StreamingDB db, StreamingGraphAPI api) { // Metodo que deserializa dados de um ficheiro binario para a base de dados e o grafo
        try (DataInputStream dis = new DataInputStream( // Cria o stream de entrada binario
                new BufferedInputStream(new FileInputStream(path)))) { // Encapsula com buffer para melhor desempenho
            int numLines = dis.readInt(); // Le o numero total de linhas guardadas no ficheiro
            for (int i = 0; i < numLines; i++) { // Percorre cada linha do ficheiro

                String line = dis.readUTF().trim(); // Le a linha e remove espacos em branco no inicio e no fim

                if (line.isEmpty()) continue; // Se a linha estiver vazia, ignora e passa a seguinte

                try {
                    parseLine(line, db, api); // Interpreta a linha e cria a entidade correspondente
                } catch (Exception e) { // Captura erros ao processar cada linha individualmente
                    System.err.println("Erro na linha binaria " + (i + 1) + ": " + e.getMessage()); // Imprime o erro sem interromper o processo
                } // Fim do bloco try-catch interno
            } // Fim do ciclo de leitura
            System.out.println("Desserializacao binaria concluida de: " + path); // Informa que a deserializacao foi concluida
        } catch (IOException e) { // Captura erros de entrada/saida
            System.err.println("Erro ao desserializar binario: " + e.getMessage()); // Imprime a mensagem de erro
        } // Fim do bloco try-catch
    } // Fim do metodo deserializeBin

    /**
     * Exporta todos os dados para um ficheiro de texto.
     *
     * @param db a base de dados com as entidades a exportar
     * @param graph o grafo com as relacoes a exportar
     * @param path o caminho do ficheiro de destino
     */
    /*
     * DICIONARIO:
     * - PrintWriter: escreve texto formatado linha a linha num ficheiro
     * - FileWriter: cria um escritor de texto para um ficheiro
     * - println: escreve uma linha de texto no ficheiro
     * - IOException: excecao lancada quando ocorre um erro de entrada/saida
     */
    public void exportTxt(StreamingDB db, StreamingGraph graph, String path) { // Metodo que exporta todos os dados para um ficheiro de texto
        try (PrintWriter w = new PrintWriter(new FileWriter(path))) { // Cria o escritor de texto para o ficheiro
            exportGenres(w, db); // Exporta todos os generos
            exportArtists(w, db); // Exporta todos os artistas
            exportContents(w, db); // Exporta todos os conteudos e episodios
            exportUsers(w, db); // Exporta todos os utilizadores
            exportInteractions(w, graph); // Exporta todas as interacoes
            exportFollows(w, db); // Exporta todas as relacoes de seguimento
            System.out.println("Exportacao TXT concluida em: " + path); // Informa que a exportacao foi concluida
        } catch (IOException e) { // Captura erros de entrada/saida
            System.err.println("Erro ao exportar TXT: " + e.getMessage()); // Imprime a mensagem de erro
        } // Fim do bloco try-catch
    } // Fim do metodo exportTxt

    /**
     * Escreve todos os generos no formato GENRE;id;name.
     *
     * @param w o escritor de texto
     * @param db a base de dados com os generos
     */
    private void exportGenres(PrintWriter w, StreamingDB db) { // Metodo privado que exporta todos os generos para o escritor
        for (Genre g : db.listGenres()) { // Percorre todos os generos da base de dados
            w.println("GENRE;" + g.getId() + ";" + g.getName()); // Escreve cada genero no formato GENRE;id;name
        } // Fim do ciclo
    } // Fim do metodo exportGenres

    /**
     * Escreve todos os artistas no formato ARTIST;id;createdAt;name;nationality;birthDate;gender.
     *
     * @param w o escritor de texto
     * @param db a base de dados com os artistas
     */
    private void exportArtists(PrintWriter w, StreamingDB db) { // Metodo privado que exporta todos os artistas para o escritor
        for (Artist a : db.listArtists()) { // Percorre todos os artistas da base de dados
            w.println("ARTIST;" + a.getId() + ";" + a.getCreatedAt() + ";"
                    + a.getName() + ";" + a.getNationality() + ";"
                    + a.getBirthDate() + ";" + a.getGender()); // Escreve cada artista no formato definido
        } // Fim do ciclo
    } // Fim do metodo exportArtists

    /**
     * Escreve todos os conteudos (filmes e series com episodios) nos formatos definidos.
     *
     * @param w o escritor de texto
     * @param db a base de dados com os conteudos
     */
    /*
     * DICIONARIO:
     * - instanceof: verifica se um objeto e de um determinado tipo (Movie, Series, etc.)
     * - pattern matching: sintaxe moderna que combina instanceof com criacao de variavel (ex: c instanceof Movie m)
     * - genreId: ID do genero do conteudo; string vazia se nao tiver genero
     * - dirId: ID do realizador; string vazia se nao tiver realizador
     * - s.getStatus().name(): converte o valor do enum SeriesStatus para texto
     */
    private void exportContents(PrintWriter w, StreamingDB db) { // Metodo privado que exporta todos os conteudos para o escritor
        for (Content c : db.listContents()) { // Percorre todos os conteudos da base de dados
            String genreId = c.getGenre() != null ? c.getGenre().getId() : ""; // Vai buscar o ID do genero ou string vazia
            if (c instanceof Movie m) { // Se o conteudo for um filme...
                String dirId = m.getDirector() != null ? m.getDirector().getId() : ""; // Vai buscar o ID do realizador ou string vazia
                w.println("MOVIE;" + m.getId() + ";" + m.getCreatedAt() + ";"
                        + m.getTitle() + ";" + m.getReleaseYear() + ";"
                        + genreId + ";" + m.getRegion() + ";"
                        + m.getDuration() + ";" + dirId); // Escreve o filme no formato MOVIE;...
            } else if (c instanceof Series s) { // Se o conteudo for uma serie...
                w.println("SERIES;" + s.getId() + ";" + s.getCreatedAt() + ";"
                        + s.getTitle() + ";" + s.getReleaseYear() + ";"
                        + genreId + ";" + s.getRegion() + ";"
                        + s.getSeasons() + ";" + s.getStatus().name()); // Escreve a serie no formato SERIES;...

                for (Object obj : s.getEpisodes()) { // Percorre todos os episodios da serie
                    Episode ep = (Episode) obj; // Converte o objeto para Episode
                    w.println("EPISODE;" + s.getId() + ";"
                            + ep.getSeasonNumber() + ";" + ep.getEpisodeNumber() + ";"
                            + ep.getTitle() + ";" + ep.getDuration() + ";" + ep.getRating()); // Escreve cada episodio no formato EPISODE;...
                } // Fim do ciclo de episodios
            } // Fim da verificacao do tipo de conteudo
        } // Fim do ciclo de conteudos
    } // Fim do metodo exportContents

    /**
     * Escreve todos os utilizadores no formato USER;id;createdAt;username;email;passwordHash;region;registrationDate.
     *
     * @param w o escritor de texto
     * @param db a base de dados com os utilizadores
     */
    private void exportUsers(PrintWriter w, StreamingDB db) { // Metodo privado que exporta todos os utilizadores para o escritor
        for (User u : db.listUsers()) { // Percorre todos os utilizadores ativos da base de dados
            String region = u.getRegion() != null ? u.getRegion() : ""; // Vai buscar a regiao ou string vazia
            String regDate = u.getRegistrationDate() != null ? u.getRegistrationDate().toString() : ""; // Vai buscar a data de registo ou string vazia
            w.println("USER;" + u.getId() + ";" + u.getCreatedAt() + ";"
                    + u.getUsername() + ";" + u.getEmail() + ";"
                    + u.getPasswordHash() + ";" + region + ";" + regDate); // Escreve o utilizador no formato USER;...
        } // Fim do ciclo
    } // Fim do metodo exportUsers

    /**
     * Escreve todas as interacoes no formato INTERACTION;userId;contentId;type;timestamp;watchedPct;rating;weight.
     *
     * @param w o escritor de texto
     * @param graph o grafo com as interacoes
     */
    private void exportInteractions(PrintWriter w, StreamingGraph graph) { // Metodo privado que exporta todas as interacoes para o escritor
        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getContent() == null || i.getUser() == null) continue; // Ignora interacoes incompletas
            w.println("INTERACTION;" + i.getUser().getId() + ";" + i.getContent().getId() + ";"
                    + i.getType() + ";" + i.getTimestamp() + ";"
                    + i.getWatchedPct() + ";" + i.getRating() + ";" + i.getWeight()); // Escreve cada interacao no formato definido
        } // Fim do ciclo
    } // Fim do metodo exportInteractions

    /**
     * Escreve todas as relacoes de seguimento no formato FOLLOW;followerId;followedId.
     *
     * @param w o escritor de texto
     * @param db a base de dados com os utilizadores
     */
    private void exportFollows(PrintWriter w, StreamingDB db) { // Metodo privado que exporta todas as relacoes de seguimento para o escritor
        for (User u : db.listUsers()) { // Percorre todos os utilizadores ativos
            for (User followed : u.getFollowing()) { // Percorre os utilizadores que cada um segue
                w.println("FOLLOW;" + u.getId() + ";" + followed.getId()); // Escreve a relacao no formato FOLLOW;followerId;followedId
            } // Fim do ciclo de seguidos
        } // Fim do ciclo de utilizadores
    } // Fim do metodo exportFollows

    /**
     * Importa dados de um ficheiro de texto para a base de dados e o grafo.
     *
     * @param path o caminho do ficheiro a importar
     * @param db a base de dados a popular com os dados importados
     * @param api a API do grafo para registar as relacoes importadas
     */
    /*
     * DICIONARIO:
     * - BufferedReader: le texto linha a linha de forma eficiente
     * - FileReader: cria um leitor de texto de um ficheiro
     * - reader.readLine: le uma linha do ficheiro e devolve null quando chega ao fim
     * - lineNum: contador da linha atual para identificar erros
     * - parseLine: metodo privado que interpreta uma linha e cria a entidade correspondente
     */
    public void importTxt(String path, StreamingDB db, StreamingGraphAPI api) { // Metodo que importa dados de um ficheiro de texto para a base de dados e o grafo
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) { // Cria o leitor de texto do ficheiro
            String line; // Variavel que guarda cada linha lida
            int lineNum = 0; // Contador da linha atual
            while ((line = reader.readLine()) != null) { // Le cada linha ate ao fim do ficheiro
                lineNum++; // Incrementa o contador da linha
                line = line.trim(); // Remove espacos em branco no inicio e no fim
                if (line.isEmpty()) continue; // Se a linha estiver vazia, passa a seguinte
                try {
                    parseLine(line, db, api); // Interpreta a linha e cria a entidade correspondente
                } catch (Exception e) { // Captura erros ao processar cada linha individualmente
                    System.err.println("Erro na linha " + lineNum + ": " + e.getMessage()); // Imprime o erro sem interromper o processo
                } // Fim do bloco try-catch interno
            } // Fim do ciclo de leitura
            System.out.println("Importacao TXT concluida de: " + path); // Informa que a importacao foi concluida
        } catch (IOException e) { // Captura erros de entrada/saida
            System.err.println("Erro ao ler ficheiro TXT: " + e.getMessage()); // Imprime a mensagem de erro
        } // Fim do bloco try-catch
    } // Fim do metodo importTxt

    /**
     * Interpreta uma linha do ficheiro e cria a entidade correspondente.
     *
     * @param line a linha a interpretar
     * @param db a base de dados onde guardar a entidade
     * @param api a API do grafo para registar relacoes
     */
    /*
     * DICIONARIO:
     * - split(";", -1): divide a linha pelo separador ";", mantendo campos vazios no fim
     * - d[0]: primeiro campo da linha, identifica o tipo de entidade
     * - switch: estrutura que executa codigo diferente consoante o valor de uma expressao
     * - case "GENRE" -> parseGenre(d, db): se o tipo for GENRE, chama o metodo parseGenre
     */
    private void parseLine(String line, StreamingDB db, StreamingGraphAPI api) { // Metodo privado que interpreta uma linha e delega ao parser correto
        String[] d = line.split(";", -1); // Divide a linha pelos ponto e virgula, mantendo campos vazios
        switch (d[0]) { // Verifica o tipo indicado no primeiro campo
            case "GENRE" -> parseGenre(d, db); // Delega para o parser de generos
            case "ARTIST" -> parseArtist(d, db); // Delega para o parser de artistas
            case "MOVIE" -> parseMovie(d, db, api); // Delega para o parser de filmes
            case "SERIES" -> parseSeries(d, db); // Delega para o parser de series
            case "EPISODE" -> parseEpisode(d, db); // Delega para o parser de episodios
            case "USER" -> parseUser(d, db); // Delega para o parser de utilizadores
            case "INTERACTION" -> parseInteraction(d, db, api); // Delega para o parser de interacoes
            case "FOLLOW" -> parseFollow(d, api); // Delega para o parser de seguimentos
            default -> System.err.println("Tipo desconhecido: " + d[0]); // Avisa se o tipo nao for reconhecido
        } // Fim do switch
    } // Fim do metodo parseLine

    /**
     * Cria um genero a partir de um array de campos e adiciona-o a base de dados.
     *
     * @param d array de campos; formato: GENRE;id;name
     * @param db a base de dados onde guardar o genero
     */
    private void parseGenre(String[] d, StreamingDB db) { // Metodo privado que cria e adiciona um genero a base de dados
        db.addGenre(new Genre(d[1], d[2])); // Cria o genero com o ID e o nome e adiciona a base de dados
    } // Fim do metodo parseGenre

    /**
     * Cria um artista a partir de um array de campos e adiciona-o a base de dados.
     *
     * @param d array de campos; formato: ARTIST;id;createdAt;name;nationality;birthDate;gender
     * @param db a base de dados onde guardar o artista
     */
    /*
     * DICIONARIO:
     * - LocalDateTime.parse: converte uma string ISO para LocalDateTime
     * - LocalDate.parse: converte uma string ISO para LocalDate
     * - d[5].isEmpty(): verifica se o campo birthDate esta vazio
     */
    private void parseArtist(String[] d, StreamingDB db) { // Metodo privado que cria e adiciona um artista a base de dados
        LocalDateTime createdAt = LocalDateTime.parse(d[2]); // Converte o campo createdAt para LocalDateTime
        LocalDate birthDate = d[5].isEmpty() ? null : LocalDate.parse(d[5]); // Converte o campo birthDate, ou null se estiver vazio
        Artist a = new Artist(d[1], createdAt, d[3], d[4], birthDate, d[6]); // Cria o artista com todos os campos
        db.addArtist(a); // Adiciona o artista a base de dados
    } // Fim do metodo parseArtist

    /**
     * Cria um filme a partir de um array de campos, adiciona-o a base de dados e regista no grafo.
     *
     * @param d array de campos; formato: MOVIE;id;createdAt;title;releaseYear;genreId;region;duration;directorId
     * @param db a base de dados onde guardar o filme
     * @param api a API do grafo para associar o realizador ao filme
     */
    /*
     * DICIONARIO:
     * - Integer.parseInt: converte uma string para um numero inteiro
     * - db.getGenre: vai buscar o genero pelo ID na base de dados
     * - db.getArtist: vai buscar o artista pelo ID na base de dados
     * - api.addArtistToContent: regista a participacao do realizador no filme no grafo
     */
    private void parseMovie(String[] d, StreamingDB db, StreamingGraphAPI api) { // Metodo privado que cria e adiciona um filme a base de dados e ao grafo
        LocalDateTime createdAt = LocalDateTime.parse(d[2]); // Converte o campo createdAt para LocalDateTime
        Genre genre = d[5].isEmpty() ? null : db.getGenre(d[5]); // Vai buscar o genero pelo ID, ou null se vazio
        int releaseYear = Integer.parseInt(d[4]); // Converte o ano de lancamento para inteiro
        int duration = Integer.parseInt(d[7]); // Converte a duracao para inteiro
        Artist director = d[8].isEmpty() ? null : db.getArtist(d[8]); // Vai buscar o realizador pelo ID, ou null se vazio
        Movie m = new Movie(d[1], createdAt, d[3], releaseYear, genre, d[6], duration, director); // Cria o filme com todos os campos
        db.addContent(m); // Adiciona o filme a base de dados
        if (director != null) api.addArtistToContent(director.getId(), m.getId()); // Se houver realizador, regista a sua participacao no grafo
    } // Fim do metodo parseMovie

    /**
     * Cria uma serie a partir de um array de campos e adiciona-a a base de dados.
     *
     * @param d array de campos; formato: SERIES;id;createdAt;title;releaseYear;genreId;region;seasons;status
     * @param db a base de dados onde guardar a serie
     */
    /*
     * DICIONARIO:
     * - SeriesStatus.valueOf: converte uma string para o valor do enum SeriesStatus (ex: "ONGOING" -> ONGOING)
     */
    private void parseSeries(String[] d, StreamingDB db) { // Metodo privado que cria e adiciona uma serie a base de dados
        LocalDateTime createdAt = LocalDateTime.parse(d[2]); // Converte o campo createdAt para LocalDateTime
        Genre genre = d[5].isEmpty() ? null : db.getGenre(d[5]); // Vai buscar o genero pelo ID, ou null se vazio
        int releaseYear = Integer.parseInt(d[4]); // Converte o ano de lancamento para inteiro
        int seasons = Integer.parseInt(d[7]); // Converte o numero de temporadas para inteiro
        SeriesStatus status = SeriesStatus.valueOf(d[8]); // Converte o texto do estado para o valor do enum
        Series s = new Series(d[1], createdAt, d[3], releaseYear, genre, d[6], seasons, status); // Cria a serie com todos os campos
        db.addContent(s); // Adiciona a serie a base de dados
    } // Fim do metodo parseSeries

    /**
     * Cria um episodio a partir de um array de campos e adiciona-o a serie correspondente.
     *
     * @param d array de campos; formato: EPISODE;seriesId;seasonNum;episodeNum;title;duration;rating
     * @param db a base de dados onde procurar a serie
     */
    /*
     * DICIONARIO:
     * - Float.parseFloat: converte uma string para um numero decimal de menor precisao
     * - s.addEpisode: adiciona o episodio a lista de episodios da serie
     */
    private void parseEpisode(String[] d, StreamingDB db) { // Metodo privado que cria e adiciona um episodio a serie correspondente
        Content c = db.getContent(d[1]); // Vai buscar o conteudo pelo ID da serie
        if (c instanceof Series s) { // Verifica se o conteudo e realmente uma serie
            int seasonNum = Integer.parseInt(d[2]); // Converte o numero da temporada para inteiro
            int episodeNum = Integer.parseInt(d[3]); // Converte o numero do episodio para inteiro
            int duration = Integer.parseInt(d[5]); // Converte a duracao para inteiro
            float rating = Float.parseFloat(d[6]); // Converte a classificacao para float
            s.addEpisode(new Episode(seasonNum, episodeNum, d[4], duration, rating)); // Cria o episodio e adiciona-o a serie
        } // Fim da verificacao do tipo
    } // Fim do metodo parseEpisode

    /**
     * Cria um utilizador a partir de um array de campos e adiciona-o a base de dados.
     *
     * @param d array de campos; formato: USER;id;createdAt;username;email;passwordHash;region;registrationDate
     * @param db a base de dados onde guardar o utilizador
     */
    /*
     * DICIONARIO:
     * - d.length > 6: verifica se o array tem pelo menos 7 campos (o campo region e opcional)
     * - u.setRegion: define a regiao do utilizador
     * - u.setRegistrationDate: define a data de registo do utilizador
     */
    private void parseUser(String[] d, StreamingDB db) { // Metodo privado que cria e adiciona um utilizador a base de dados
        LocalDateTime createdAt = LocalDateTime.parse(d[2]); // Converte o campo createdAt para LocalDateTime
        User u = new User(d[1], createdAt, d[3], d[4], d[5]); // Cria o utilizador com os campos obrigatorios
        if (d.length > 6 && !d[6].isEmpty()) u.setRegion(d[6]); // Define a regiao se o campo existir e nao estiver vazio
        if (d.length > 7 && !d[7].isEmpty()) u.setRegistrationDate(LocalDate.parse(d[7])); // Define a data de registo se o campo existir e nao estiver vazio
        db.addUser(u); // Adiciona o utilizador a base de dados
    } // Fim do metodo parseUser

    /**
     * Cria uma interacao a partir de um array de campos e regista-a no grafo.
     *
     * @param d array de campos; formato: INTERACTION;userId;contentId;type;timestamp;watchedPct;rating;weight
     * @param db a base de dados com os utilizadores e conteudos
     * @param api a API do grafo para registar a interacao
     */
    /*
     * DICIONARIO:
     * - InteractionType.valueOf: converte uma string para o valor do enum InteractionType
     * - Double.parseDouble: converte uma string para um numero decimal de dupla precisao
     * - api.addWatch: regista a visualizacao no grafo
     * - api.addRating: regista a classificacao no grafo
     * - api.addClick: regista o clique no grafo
     */
    private void parseInteraction(String[] d, StreamingDB db, StreamingGraphAPI api) { // Metodo privado que cria e regista uma interacao no grafo
        InteractionType type = InteractionType.valueOf(d[3]); // Converte o texto do tipo para o valor do enum
        LocalDateTime timestamp = LocalDateTime.parse(d[4]); // Converte o timestamp para LocalDateTime
        double watchedPct = Double.parseDouble(d[5]); // Converte a percentagem vista para double
        double rating = Double.parseDouble(d[6]); // Converte a classificacao para double
        if (type == InteractionType.WATCH) { // Se for uma visualizacao...
            api.addWatch(d[1], d[2], watchedPct, rating, timestamp); // Regista a visualizacao com o timestamp historico
        } else if (type == InteractionType.RATE) { // Se for uma classificacao...
            api.addRating(d[1], d[2], rating, timestamp); // Regista a classificacao com o timestamp historico
        } else if (type == InteractionType.CLICK) { // Se for um clique...
            api.addClick(d[1], d[2], timestamp); // Regista o clique com o timestamp historico
        } // Fim da verificacao do tipo de interacao
    } // Fim do metodo parseInteraction

    /**
     * Regista uma relacao de seguimento a partir de um array de campos.
     *
     * @param d array de campos; formato: FOLLOW;followerId;followedId
     * @param api a API do grafo para registar o seguimento
     */
    private void parseFollow(String[] d, StreamingGraphAPI api) { // Metodo privado que regista uma relacao de seguimento no grafo
        api.addFollow(d[1], d[2]); // Regista que o utilizador d[1] passa a seguir o utilizador d[2]
    } // Fim do metodo parseFollow

} // Fim da classe FileManager
