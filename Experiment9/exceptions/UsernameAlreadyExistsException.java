package com.github.sambhavmahajan.cloudstorageservice.exceptions;

public class UsernameAlreadyExistsException extends Exception {
    public UsernameAlreadyExistsException(String username) {
        super("User with username " + username + " already exists");
    }
}
