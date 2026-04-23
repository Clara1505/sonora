package com.sonora.sonora.exception;

/** Exception thrown when a favorite already exists for a track. */
public class AlreadyFavoriteException extends RuntimeException {

    /**
     * Creates the exception with the duplicated track identifier.
     *
     * @param trackId duplicated track identifier
     */
    public AlreadyFavoriteException(final Long trackId) {
        super("Track already in favorites: " + trackId);
    }
}
