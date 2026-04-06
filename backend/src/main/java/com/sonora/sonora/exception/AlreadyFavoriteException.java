package com.sonora.sonora.exception;

public class AlreadyFavoriteException extends RuntimeException {
    public AlreadyFavoriteException(Long trackId) {
        super("Track already in favorites: " + trackId);
    }
}