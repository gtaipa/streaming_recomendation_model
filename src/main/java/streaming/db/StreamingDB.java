package streaming.db;

import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import streaming.model.Artist;
import streaming.model.Content;
import streaming.model.Genre;
import streaming.model.User;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * In-memory database engine for the streaming platform.
 * <p>
 * Manages core entities (Users, Content, Artists, Genres) using hash tables for O(1) access
 * and Red-Black BSTs for optimized O(log N) indexed searches.
 */
public class StreamingDB {

    /** Primary hash table for active users. */
    private SeparateChainingHashST<String, User> users;
    /** Hash table for archived users. */
    private SeparateChainingHashST<String, User> archivedusers;
    /** Primary hash table for contents. */
    private SeparateChainingHashST<String, Content> contents;
    /** Primary hash table for artists. */
    private SeparateChainingHashST<String, Artist> artists;
    /** Primary hash table for genres. */
    private SeparateChainingHashST<String, Genre> genres;

    /** BST index for users by region. */
    private RedBlackBST<String, List<User>> usersByRegion;
    /** BST index for users by registration date. */
    private RedBlackBST<ChronoLocalDate, List<User>> usersByRegistrationDate;
    /** BST index for content by genre name. */
    private RedBlackBST<String, List<Content>> contentsByGenre;
    /** BST index for content by title. */
    private RedBlackBST<String, List<Content>> contentsByTitle;
    /** BST index for content by region. */
    private RedBlackBST<String, List<Content>> contentsByRegion;
    /** BST index for artists by nationality. */
    private RedBlackBST<String, List<Artist>> artistsByNationality;
    /** BST index for artists by birth date. */
    private RedBlackBST<ChronoLocalDate, List<Artist>> artistsByBirthDate;
    /** BST index for content by release year. */
    private RedBlackBST<Integer, List<Content>> contentsByReleaseYear;
    /** BST index for content by rating. */
    private RedBlackBST<Double, List<Content>> contentsByRating;
    /** BST index for content by views. */
    private RedBlackBST<Integer, List<Content>> contentsByViews;
    /** BST index for content by duration. */
    private RedBlackBST<Integer, List<Content>> contentsByDuration;

    /**
     * Initializes a new database with all storage and indexing structures.
     */
    public StreamingDB() {
        this.users = new SeparateChainingHashST<>();
        this.contents = new SeparateChainingHashST<>();
        this.artists = new SeparateChainingHashST<>();
        this.genres = new SeparateChainingHashST<>();
        this.archivedusers = new SeparateChainingHashST<>();

        this.usersByRegion = new RedBlackBST<>();
        this.usersByRegistrationDate = new RedBlackBST<>();
        this.contentsByGenre = new RedBlackBST<>();
        this.contentsByTitle = new RedBlackBST<>();
        this.contentsByRegion = new RedBlackBST<>();
        this.artistsByNationality = new RedBlackBST<>();
        this.artistsByBirthDate = new RedBlackBST<>();
        this.contentsByReleaseYear = new RedBlackBST<>();
        this.contentsByRating = new RedBlackBST<>();
        this.contentsByViews = new RedBlackBST<>();
        this.contentsByDuration = new RedBlackBST<>();
    }

    /**
     * Adds a user to the database. If the ID exists, the old user is deindexed and replaced.
     * @param newUser the user to add
     */
    public void addUser(User newUser) {
        if (newUser == null || newUser.getId() == null) return;

        // if the user already exists, remove the old entry from all BST indices first
        if (users.contains(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            removeUserFromIndices(oldUser);
        }

        users.put(newUser.getId(), newUser);
        addUserToIndices(newUser);
    }

    /**
     * Adds content to the database. If the ID exists, the old content is deindexed and replaced.
     * @param newContent the content to add
     */
    public void addContent(Content newContent) {
        if (newContent == null || newContent.getId() == null) return;

        if (contents.contains(newContent.getId())) {
            Content oldContent = contents.get(newContent.getId());
            removeContentFromIndices(oldContent);
        }

        contents.put(newContent.getId(), newContent);
        addContentToIndices(newContent);
    }

    /**
     * Adds an artist to the database. If the ID exists, the old artist is deindexed and replaced.
     * @param newArtist the artist to add
     */
    public void addArtist(Artist newArtist) {
        if (newArtist == null || newArtist.getId() == null) return;

        if (artists.contains(newArtist.getId())) {
            Artist oldArtist = artists.get(newArtist.getId());
            removeArtistFromIndices(oldArtist);
        }

        artists.put(newArtist.getId(), newArtist);
        addArtistToIndices(newArtist);
    }

    /**
     * Internal helper to add a user to all relevant BST indices.
     */
    private void addUserToIndices(User user) {
        if (user == null) return;

        String region = user.getRegion();
        if (region != null) {
            if (!usersByRegion.contains(region)) usersByRegion.put(region, new ArrayList<>());
            usersByRegion.get(region).add(user);
        }

        LocalDate regDate = user.getRegistrationDate();
        if (regDate != null) {
            if (!usersByRegistrationDate.contains(regDate)) usersByRegistrationDate.put(regDate, new ArrayList<>());
            usersByRegistrationDate.get(regDate).add(user);
        }
    }

    /**
     * Internal helper to remove a user from all BST indices using its current field values.
     */
    private void removeUserFromIndices(User user) {
        if (user == null) return;

        String region = user.getRegion();
        if (region != null && usersByRegion.contains(region)) {
            List<User> list = usersByRegion.get(region);
            list.removeIf(u -> u.getId().equals(user.getId()));
            if (list.isEmpty()) usersByRegion.delete(region);
        }

        LocalDate regDate = user.getRegistrationDate();
        if (regDate != null && usersByRegistrationDate.contains(regDate)) {
            List<User> list = usersByRegistrationDate.get(regDate);
            list.removeIf(u -> u.getId().equals(user.getId()));
            if (list.isEmpty()) usersByRegistrationDate.delete(regDate);
        }
    }

    /**
     * Internal helper to add content to all relevant BST indices.
     */
    private void addContentToIndices(Content content) {
        if (content == null) return;

        int year = content.getReleaseYear();
        if (!contentsByReleaseYear.contains(year)) contentsByReleaseYear.put(year, new ArrayList<>());
        contentsByReleaseYear.get(year).add(content);

        Genre genre = content.getGenre();
        if (genre != null && genre.getName() != null) {
            String gName = genre.getName();
            if (!contentsByGenre.contains(gName)) contentsByGenre.put(gName, new ArrayList<>());
            contentsByGenre.get(gName).add(content);
        }

        String title = content.getTitle();
        if (title != null) {
            if (!contentsByTitle.contains(title)) contentsByTitle.put(title, new ArrayList<>());
            contentsByTitle.get(title).add(content);
        }

        String region = content.getRegion();
        if (region != null) {
            if (!contentsByRegion.contains(region)) contentsByRegion.put(region, new ArrayList<>());
            contentsByRegion.get(region).add(content);
        }

        double rating = content.getAvgRating();
        if (!contentsByRating.contains(rating)) contentsByRating.put(rating, new ArrayList<>());
        contentsByRating.get(rating).add(content);

        int duration = content.getDuration();
        if (duration > 0) {
            if (!contentsByDuration.contains(duration)) contentsByDuration.put(duration, new ArrayList<>());
            contentsByDuration.get(duration).add(content);
        }

        int views = content.getTotalViews();
        if (!contentsByViews.contains(views)) contentsByViews.put(views, new ArrayList<>());
        contentsByViews.get(views).add(content);
    }

    /**
     * Internal helper to remove content from all BST indices using its current field values.
     */
    private void removeContentFromIndices(Content content) {
        if (content == null) return;

        int year = content.getReleaseYear();
        if (contentsByReleaseYear.contains(year)) {
            List<Content> list = contentsByReleaseYear.get(year);
            list.removeIf(c -> c.getId().equals(content.getId()));
            if (list.isEmpty()) contentsByReleaseYear.delete(year);
        }

        Genre genre = content.getGenre();
        if (genre != null && genre.getName() != null) {
            String gName = genre.getName();
            if (contentsByGenre.contains(gName)) {
                List<Content> list = contentsByGenre.get(gName);
                list.removeIf(c -> c.getId().equals(content.getId()));
                if (list.isEmpty()) contentsByGenre.delete(gName);
            }
        }

        String title = content.getTitle();
        if (title != null && contentsByTitle.contains(title)) {
            List<Content> list = contentsByTitle.get(title);
            list.removeIf(c -> c.getId().equals(content.getId()));
            if (list.isEmpty()) contentsByTitle.delete(title);
        }

        String region = content.getRegion();
        if (region != null && contentsByRegion.contains(region)) {
            List<Content> list = contentsByRegion.get(region);
            list.removeIf(c -> c.getId().equals(content.getId()));
            if (list.isEmpty()) contentsByRegion.delete(region);
        }

        double rating = content.getAvgRating();
        if (contentsByRating.contains(rating)) {
            List<Content> list = contentsByRating.get(rating);
            list.removeIf(c -> c.getId().equals(content.getId()));
            if (list.isEmpty()) contentsByRating.delete(rating);
        }

        int duration = content.getDuration();
        if (duration > 0 && contentsByDuration.contains(duration)) {
            List<Content> list = contentsByDuration.get(duration);
            list.removeIf(c -> c.getId().equals(content.getId()));
            if (list.isEmpty()) contentsByDuration.delete(duration);
        }

        int views = content.getTotalViews();
        if (contentsByViews.contains(views)) {
            List<Content> list = contentsByViews.get(views);
            list.removeIf(c -> c.getId().equals(content.getId()));
            if (list.isEmpty()) contentsByViews.delete(views);
        }
    }

    /**
     * Internal helper to add an artist to all relevant BST indices.
     */
    private void addArtistToIndices(Artist artist) {
        if (artist == null) return;

        String nationality = artist.getNationality();
        if (nationality != null) {
            if (!artistsByNationality.contains(nationality)) artistsByNationality.put(nationality, new ArrayList<>());
            artistsByNationality.get(nationality).add(artist);
        }

        LocalDate birthDate = artist.getBirthDate();
        if (birthDate != null) {
            if (!artistsByBirthDate.contains(birthDate)) artistsByBirthDate.put(birthDate, new ArrayList<>());
            artistsByBirthDate.get(birthDate).add(artist);
        }
    }

    /**
     * Internal helper to remove an artist from all BST indices using its current field values.
     */
    private void removeArtistFromIndices(Artist artist) {
        if (artist == null) return;

        String nationality = artist.getNationality();
        if (nationality != null && artistsByNationality.contains(nationality)) {
            List<Artist> list = artistsByNationality.get(nationality);
            list.removeIf(a -> a.getId().equals(artist.getId()));
            if (list.isEmpty()) artistsByNationality.delete(nationality);
        }

        LocalDate birthDate = artist.getBirthDate();
        if (birthDate != null && artistsByBirthDate.contains(birthDate)) {
            List<Artist> list = artistsByBirthDate.get(birthDate);
            list.removeIf(a -> a.getId().equals(artist.getId()));
            if (list.isEmpty()) artistsByBirthDate.delete(birthDate);
        }
    }

    /**
     * Adds a genre to the database.
     * @param g the genre to add
     */
    public void addGenre(Genre g) {
        if (g == null || g.getId() == null) return;
        this.genres.put(g.getId(), g);
    }

    /**
     * Retrieves a user by ID.
     * @param userId user ID
     * @return the user or null
     */
    public User getUser(String userId) {
        if (userId == null) return null;
        return this.users.get(userId);
    }

    /**
     * Retrieves content by ID.
     * @param contentId content ID
     * @return the content or null
     */
    public Content getContent(String contentId) {
        if (contentId == null) return null;
        return this.contents.get(contentId);
    }

    /**
     * Retrieves an artist by ID.
     * @param artistId artist ID
     * @return the artist or null
     */
    public Artist getArtist(String artistId) {
        if (artistId == null) return null;
        return this.artists.get(artistId);
    }

    /**
     * Retrieves a genre by ID.
     * @param genreId genre ID
     * @return the genre or null
     */
    public Genre getGenre(String genreId) {
        if (genreId == null) return null;
        return this.genres.get(genreId);
    }

    /**
     * Retrieves an archived user by ID.
     * @param userId user ID
     * @return the user or null
     */
    public User getArchivedUser(String userId) {
        if (userId == null) return null;
        return this.archivedusers.get(userId);
    }

    /**
     * Lists all active users.
     * @return list of active users
     */
    public List<User> listUsers() {
        List<User> out = new ArrayList<>();
        for (String id : this.users.keys()) {
            User u = this.users.get(id);
            if (u != null) out.add(u);
        }
        return out;
    }

    /**
     * Lists all archived users.
     * @return list of archived users
     */
    public List<User> listArchivedUsers() {
        List<User> out = new ArrayList<>();
        for (String id : this.archivedusers.keys()) {
            User u = this.archivedusers.get(id);
            if (u != null) out.add(u);
        }
        return out;
    }

    /**
     * Updates an archived user in storage.
     * @param u the user to update
     * @return true if updated
     */
    public boolean updateArchivedUser(User u) {
        if (u == null || u.getId() == null) return false;
        if (!this.archivedusers.contains(u.getId())) return false;
        this.archivedusers.put(u.getId(), u);
        return true;
    }

    /**
     * Permanently removes a user from the archive.
     * @param userId user ID
     * @return true if removed
     */
    public boolean removeArchivedUser(String userId) {
        if (userId == null) return false;
        if (!this.archivedusers.contains(userId)) return false;
        this.archivedusers.delete(userId);
        return true;
    }

    /**
     * Restores a user from archive back to active storage.
     * @param userId user ID
     * @return true if restored
     */
    public boolean restoreArchivedUser(String userId) {
        if (userId == null) return false;
        User u = this.archivedusers.get(userId);
        if (u == null) return false;
        if (this.users.contains(userId)) return false;

        this.archivedusers.delete(userId);
        addUser(u);
        return true;
    }

    /**
     * Clears all archived users.
     */
    public void clearArchivedUsers() {
        List<String> ids = new ArrayList<>();
        for (String id : this.archivedusers.keys()) ids.add(id);
        for (String id : ids) this.archivedusers.delete(id);
    }

    /**
     * Lists all content.
     * @return list of content
     */
    public List<Content> listContents() {
        List<Content> out = new ArrayList<>();
        for (String id : this.contents.keys()) {
            Content c = this.contents.get(id);
            if (c != null) out.add(c);
        }
        return out;
    }

    /**
     * Lists all artists.
     * @return list of artists
     */
    public List<Artist> listArtists() {
        List<Artist> out = new ArrayList<>();
        for (String id : this.artists.keys()) {
            Artist a = this.artists.get(id);
            if (a != null) out.add(a);
        }
        return out;
    }

    /**
     * Lists all genres.
     * @return list of genres
     */
    public List<Genre> listGenres() {
        List<Genre> out = new ArrayList<>();
        for (String id : this.genres.keys()) {
            Genre g = this.genres.get(id);
            if (g != null) out.add(g);
        }
        return out;
    }

    /**
     * Updates an existing user.
     * @param u the user to update
     * @return true if updated
     */
    public boolean updateUser(User u) {
        if (u == null || u.getId() == null) return false;
        if (!this.users.contains(u.getId())) return false;
        addUser(u);
        return true;
    }

    /**
     * Updates existing content.
     * @param c the content to update
     * @return true if updated
     */
    public boolean updateContent(Content c) {
        if (c == null || c.getId() == null) return false;
        if (!this.contents.contains(c.getId())) return false;
        addContent(c);
        return true;
    }

    /**
     * Updates an existing artist.
     * @param a the artist to update
     * @return true if updated
     */
    public boolean updateArtist(Artist a) {
        if (a == null || a.getId() == null) return false;
        if (!this.artists.contains(a.getId())) return false;
        addArtist(a);
        return true;
    }

    /**
     * Updates a genre name and refreshes content indexing.
     */
    public boolean updateGenreName(String genreId, String newName) {
        if (genreId == null || newName == null) return false;
        Genre g = this.genres.get(genreId);
        if (g == null) return false;

        String oldName = g.getName();
        if (oldName == null) oldName = "";
        g.setName(newName);

        if (this.contentsByGenre.contains(oldName)) {
            List<Content> moved = this.contentsByGenre.get(oldName);
            this.contentsByGenre.delete(oldName);

            if (moved != null && !moved.isEmpty()) {
                if (!this.contentsByGenre.contains(newName)) this.contentsByGenre.put(newName, new ArrayList<>());
                this.contentsByGenre.get(newName).addAll(moved);
            }
        }
        return true;
    }

    /**
     * Removes a user and optionally archives them.
     */
    public boolean removeUser(String userId, boolean archive) {
        if (userId == null) return false;
        User u = this.users.get(userId);
        if (u == null) return false;

        removeUserFromIndices(u);
        this.users.delete(userId);
        if (archive) this.archivedusers.put(userId, u);
        return true;
    }

    /**
     * Removes an artist.
     */
    public boolean removeArtist(String artistId) {
        if (artistId == null) return false;
        Artist a = this.artists.get(artistId);
        if (a == null) return false;

        removeArtistFromIndices(a);
        this.artists.delete(artistId);
        return true;
    }

    /**
     * Removes a genre if no content references it.
     */
    public boolean removeGenre(String genreId) {
        if (genreId == null) return false;
        Genre g = this.genres.get(genreId);
        if (g == null) return false;

        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) continue;
            Genre cg = c.getGenre();
            if (cg != null && genreId.equals(cg.getId())) return false;
        }

        this.genres.delete(genreId);
        return true;
    }

    /**
     * Removes content if it exists.
     */
    public boolean removeContentIfExists(String contentId) {
        if (contentId == null) return false;
        if (!this.contents.contains(contentId)) return false;
        removeContent(contentId);
        return true;
    }

    /**
     * Searches users by region.
     */
    public List<User> searchUsersByRegion(String r) {
        if (r == null || !this.usersByRegion.contains(r)) {
            return new ArrayList<>();
        }
        return this.usersByRegion.get(r);
    }

    /**
     * Searches users by registration date range.
     */
    public List<User> searchUsersRegisteredBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return new ArrayList<>();
        if (start.isAfter(end)) {
            LocalDate tmp = start; start = end; end = tmp;
        }

        List<User> out = new ArrayList<>();
        for (ChronoLocalDate d : this.usersByRegistrationDate.keys(start, end)) {
            List<User> dayUsers = this.usersByRegistrationDate.get(d);
            if (dayUsers != null) out.addAll(dayUsers);
        }
        return out;
    }

    /**
     * Searches users by username substring.
     */
    public List<User> searchUsersByUsernameSubstring(String substring) {
        if (substring == null) return new ArrayList<>();
        String needle = substring.toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) return new ArrayList<>();

        List<User> out = new ArrayList<>();
        for (String userId : this.users.keys()) {
            User u = this.users.get(userId);
            if (u == null) continue;
            String username = u.getUsername();
            if (username != null && username.toLowerCase(Locale.ROOT).contains(needle)) out.add(u);
        }
        return out;
    }

    /**
     * Searches content by genre name.
     */
    public List<Content> searchByGenre(String genreName) {
        if (genreName == null || !this.contentsByGenre.contains(genreName)) {
            return new ArrayList<>();
        }
        return this.contentsByGenre.get(genreName);
    }

    /**
     * Searches content by release year range.
     */
    public List<Content> searchContentsReleasedBetween(int startYear, int endYear) {
        if (startYear > endYear) {
            int tmp = startYear; startYear = endYear; endYear = tmp;
        }

        List<Content> out = new ArrayList<>();
        for (Integer year : this.contentsByReleaseYear.keys(startYear, endYear)) {
            List<Content> yearContents = this.contentsByReleaseYear.get(year);
            if (yearContents != null) out.addAll(yearContents);
        }
        return out;
    }

    /**
     * Searches content by title substring.
     */
    public List<Content> searchContentsByTitleSubstring(String substring) {
        if (substring == null) return new ArrayList<>();
        String needle = substring.toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) return new ArrayList<>();

        List<Content> out = new ArrayList<>();
        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) continue;
            String title = c.getTitle();
            if (title != null && title.toLowerCase(Locale.ROOT).contains(needle)) out.add(c);
        }
        return out;
    }

    /**
     * Searches content by type.
     */
    public List<Content> searchContentsByType(String contentType) {
        if (contentType == null) return new ArrayList<>();
        String needle = contentType.toLowerCase(Locale.ROOT).trim();
        if (needle.isEmpty()) return new ArrayList<>();

        List<Content> out = new ArrayList<>();
        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) continue;
            String type = c.getContentType();
            if (type != null && type.toLowerCase(Locale.ROOT).equals(needle)) {
                out.add(c);
            }
        }
        return out;
    }

    /**
     * Searches artists by nationality.
     */
    public List<Artist> searchArtistsByNationality(String nationality) {
        if (nationality == null || !this.artistsByNationality.contains(nationality)) return new ArrayList<>();
        return this.artistsByNationality.get(nationality);
    }

    /**
     * Searches artists by birth date range.
     */
    public List<Artist> searchArtistsBornBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return new ArrayList<>();
        if (start.isAfter(end)) {
            LocalDate tmp = start; start = end; end = tmp;
        }

        List<Artist> out = new ArrayList<>();
        for (ChronoLocalDate d : this.artistsByBirthDate.keys(start, end)) {
            List<Artist> dayArtists = this.artistsByBirthDate.get(d);
            if (dayArtists != null) out.addAll(dayArtists);
        }
        return out;
    }

    /**
     * Searches artists by name substring.
     */
    public List<Artist> searchArtistsByNameSubstring(String substring) {
        if (substring == null) return new ArrayList<>();
        String needle = substring.toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) return new ArrayList<>();

        List<Artist> out = new ArrayList<>();
        for (String artistId : this.artists.keys()) {
            Artist a = this.artists.get(artistId);
            if (a == null) continue;
            String name = a.getName();
            if (name != null && name.toLowerCase(Locale.ROOT).contains(needle)) out.add(a);
        }
        return out;
    }

    /**
     * Searches artists by gender (case-insensitive linear scan).
     *
     * @param gender gender string to match (e.g. "M", "F")
     * @return list of matching artists
     */
    public List<Artist> searchArtistsByGender(String gender) {
        if (gender == null) return new ArrayList<>();
        String needle = gender.toLowerCase(Locale.ROOT);
        List<Artist> out = new ArrayList<>();
        for (String id : this.artists.keys()) {
            Artist a = this.artists.get(id);
            if (a == null) continue;
            String g = a.getGender();
            if (g != null && g.toLowerCase(Locale.ROOT).equals(needle)) out.add(a);
        }
        return out;
    }

    /**
     * Searches content whose avgRating falls within [min, max] using the rating BST index.
     *
     * @param min minimum rating (inclusive)
     * @param max maximum rating (inclusive)
     * @return list of matching contents
     */
    public List<Content> searchContentsByRatingRange(double min, double max) {
        if (min > max) { double tmp = min; min = max; max = tmp; }
        List<Content> out = new ArrayList<>();
        for (Double r : this.contentsByRating.keys(min, max)) {
            List<Content> bucket = this.contentsByRating.get(r);
            if (bucket != null) out.addAll(bucket);
        }
        return out;
    }

    /**
     * Searches content whose duration (in minutes) falls within [min, max] using the duration BST index.
     * Contents with no duration (e.g. Series) are never indexed here.
     *
     * @param min minimum duration in minutes (inclusive)
     * @param max maximum duration in minutes (inclusive)
     * @return list of matching contents
     */
    public List<Content> searchContentsByDurationRange(int min, int max) {
        if (min > max) { int tmp = min; min = max; max = tmp; }
        List<Content> out = new ArrayList<>();
        for (Integer d : this.contentsByDuration.keys(min, max)) {
            List<Content> bucket = this.contentsByDuration.get(d);
            if (bucket != null) out.addAll(bucket);
        }
        return out;
    }

    /**
     * Searches content whose totalViews falls within [min, max] using the views BST index.
     *
     * @param min minimum views (inclusive)
     * @param max maximum views (inclusive)
     * @return list of matching contents
     */
    public List<Content> searchContentsByViewsRange(int min, int max) {
        if (min > max) { int tmp = min; min = max; max = tmp; }
        List<Content> out = new ArrayList<>();
        for (Integer v : this.contentsByViews.keys(min, max)) {
            List<Content> bucket = this.contentsByViews.get(v);
            if (bucket != null) out.addAll(bucket);
        }
        return out;
    }

    /**
     * Searches users who have the given genre in their preferences list (linear scan).
     *
     * @param genreName genre name to look for
     * @return list of users who prefer that genre
     */
    public List<User> searchUsersByPreference(String genreName) {
        if (genreName == null) return new ArrayList<>();
        List<User> out = new ArrayList<>();
        for (String id : this.users.keys()) {
            User u = this.users.get(id);
            if (u == null) continue;
            for (Genre g : u.getPreferences()) {
                if (g != null && genreName.equals(g.getName())) {
                    out.add(u);
                    break;
                }
            }
        }
        return out;
    }

    /**
     * Returns years with content in range.
     */
    public Iterable<Integer> getYearsWithContent(int startYear, int endYear) {
        if (startYear > endYear) {
            int tmp = startYear; startYear = endYear; endYear = tmp;
        }
        return this.contentsByReleaseYear.keys(startYear, endYear);
    }

    /**
     * Removes content.
     */
    public void removeContent(String contentId) {
        if (contentId == null) return;
        Content c = this.contents.get(contentId);
        if (c == null) return;
        removeContentFromIndices(c);
        this.contents.delete(contentId);
    }

    /**
     * Validates database consistency between main storage and indices.
     */
    public boolean validateConsistency() {
        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) return false;
            int year = c.getReleaseYear();
            if (!this.contentsByReleaseYear.contains(year)) return false;
            if (!containsContentId(this.contentsByReleaseYear.get(year), contentId)) return false;
        }
        for (String userId : this.users.keys()) {
            User u = this.users.get(userId);
            if (u == null) return false;
            String region = u.getRegion();
            if (region != null && !this.usersByRegion.contains(region)) return false;
        }
        return true;
    }

    private static boolean containsContentId(List<Content> list, String contentId) {
        if (list == null || contentId == null) return false;
        for (Content c : list) if (c != null && contentId.equals(c.getId())) return true;
        return false;
    }

    private void removeUserById(List<User> list, String userId) {
        if (list == null || userId == null) return;
        list.removeIf(u -> u.getId().equals(userId));
    }
}
