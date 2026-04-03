package streaming.db;

import streaming.model.Genre;
import streaming.model.List;
import java.util.List;
import streaming.model.Artist;
import streaming.model.void;
import HashMap;
import streaming.model.boolean;
import RedBlackBST;
import String;
import streaming.model.Content;
import streaming.model.User;

public class StreamingDB {

  public HashMap users;

  public HashMap contents;

  public HashMap artists;

  public HashMap genres;

  public RedBlackBST usersByDate;

  public RedBlackBST contentsByYear;

  public RedBlackBST artistsByBirth;

    public List<Genre> genre;
    public List<User> user;
    public List<Artist> artist;
    public Genre genre;
    public List<User> user;
    public List<User> user;
    public List<Content> content;
    public List<Artist> artist;

  public void addUser(User u) {
  }

  public void removeUser(String id) {
  }

  public User getUser(String id) {
  return null;
  }

  public void addContent(Content c) {
  }

  public void removeContent(String id) {
  }

  public void addArtist(Artist a) {
  }

  public List searchUsersByRegion(String r) {
  return null;
  }

  public List searchByGenre(Genre g) {
  return null;
  }

  public List searchByTitle(String sub) {
  return null;
  }

  public List searchArtistsByNationality(String n) {
  return null;
  }

  public boolean validateConsistency() {
  return false;
  }

}