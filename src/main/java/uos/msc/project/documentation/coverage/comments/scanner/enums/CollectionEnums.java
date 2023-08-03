package uos.msc.project.documentation.coverage.comments.scanner.enums;

/**
 * Enum representing different firebase collections
 */
public enum CollectionEnums {

    /**
     * Represents the ProjectEntity's collection
     */
    PROJECT("project");

    /**
     * The name of the collection.
     */
    private final String collection;

    /**
     * Constructs a CollectionEnums object with the specified collection name.
     * @param collection the name of the collection
     */
    CollectionEnums(String collection) {
        this.collection = collection;
    }

    /**
     * @return the name of the collection
     */
    public String getCollection() {
        return collection;
    }
}
