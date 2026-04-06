package streaming.db;
import streaming.model.Artist;
import streaming.model.Content;
import streaming.model.User;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import java.util.List;

import java.util.ArrayList;
import streaming.model.Genre;




public class StreamingDB {

// 1. Declarar as tabelas privadas da biblioteca algs4
    private SeparateChainingHashST<String, User> users;
    private SeparateChainingHashST <String, User> archivedusers;
    private SeparateChainingHashST<String, Content> contents;
    private SeparateChainingHashST<String, Artist> artists;
    private SeparateChainingHashST<String, Genre> genres;

    public StreamingDB() {
        this.users = new SeparateChainingHashST<>();
        this.contents = new SeparateChainingHashST<>();
        this.artists = new SeparateChainingHashST<>();
        this.genres = new SeparateChainingHashST<>();   
        this.archivedusers = new SeparateChainingHashST<>();
    }
    public void addUser(User u) {
        this.users.put(u.getId(), u);
    }

    public void removeUser(String id) {
        User userToArchive = this.users.get(id);
        if (userToArchive != null) {
            this.archivedusers.put(id, userToArchive);
            this.users.delete(id);
        }
    }

    public User getUser(String id) {
        return this.users.get(id);
    }

    public void addContent(Content c) {
            this.contents.put(c.getId(), c);
    }


    public void removeContent(String id) {
        this.contents.delete(id);
    }

    public void addArtist(Artist a) {
      this.artists.put(a.getId(), a);
    }

    public List<User> searchUsersByRegion(String r) {
        List<User> results = new ArrayList<>();
        for (String id : this.users.keys()) {
            User currentUser = this.users.get(id);
            if (currentUser.getRegion().equals(r)) {
                results.add(currentUser);
            }
        }
        return results;
    }

    public List<Content> searchByGenre(Genre g) {
        List<Content> results = new ArrayList<>();
        for (String id : this.contents.keys()) {
            Content currentContent = this.contents.get(id);
            if (currentContent.getGenre().equals(g)) {
                results.add(currentContent);
            }
        }
        return results;
    }

    public List<Content> searchByTitle(String sub) {
        List<Content> results = new ArrayList<>();
        for (String id : this.contents.keys()) {
            Content currentContent = this.contents.get(id);
            if (currentContent.getTitle().toLowerCase().contains(sub.toLowerCase())) {
                results.add(currentContent);
            }
        }
        return results;
    }

    public List<Artist> searchArtistsByNationality(String n) {
        List<Artist> results = new ArrayList<>();
        for (String id : this.artists.keys()) {
            Artist currentArtist = this.artists.get(id);
            if (currentArtist.getNationality().equals(n)) {
                results.add(currentArtist);
            }
        }
        return results;
    }

    public boolean validateConsistency() {
        return false;
    }

}
