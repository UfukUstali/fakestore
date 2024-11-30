package fakestore.model;

/**
 * Represents the current state of the response
 */
public enum Status {
    /**
     * the response was successful
     */
    SUCCESS,
    /**
     * the response was unsuccessful
     */
    ERROR,
    /**
     * the response is still being fetched
     */
    PENDING
}

