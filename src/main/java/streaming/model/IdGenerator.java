package streaming.model;

/**
 * Utility class to generate unique, formatted IDs for various entity types.
 * Each entity type has its own counter that starts at 1 and increments with every new ID.
 */
public class IdGenerator {

    /** Counter for generating user IDs. */
    private int userCount;
    /** Counter for generating content IDs. */
    private int contentCount;
    /** Counter for generating artist IDs. */
    private int artistCount;
    /** Counter for generating genre IDs. */
    private int genreCount;

    /**
     * Initializes a new IdGenerator with all counters set to 1.
     */
    public IdGenerator() {
        this.userCount = 1;
        this.contentCount = 1;
        this.artistCount = 1;
        this.genreCount = 1;
    }

    /**
     * Generates the next unique user ID and increments the internal counter.
     * @return A formatted String like "USER-0001".
     */
    public String nextUserId() {
        return String.format("USER-%04d", userCount++);
    }

    /**
     * Generates the next unique content ID and increments the internal counter.
     * @return A formatted String like "CONT-0001".
     */
    public String nextContentId() {
        return String.format("CONT-%04d", contentCount++);
    }

    /**
     * Generates the next unique artist ID and increments the internal counter.
     * @return A formatted String like "ARTIST-0001".
     */
    public String nextArtistId() {
        return String.format("ARTIST-%04d", artistCount++);
    }

    /**
     * Generates the next unique genre ID and increments the internal counter.
     * @return A formatted String like "GENRE-0001".
     */
    public String nextGenreId() {
        return String.format("GENRE-%04d", genreCount++);
    }

    /**
     * Resets all internal counters back to 1.
     * Useful for clearing state between tests.
     */
    public void reset() {
        this.userCount = 1;
        this.contentCount = 1;
        this.artistCount = 1;
        this.genreCount = 1;
    }
}