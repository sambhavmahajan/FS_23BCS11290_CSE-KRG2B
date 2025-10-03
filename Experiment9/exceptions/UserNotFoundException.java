package com.github.sambhavmahajan.cloudstorageservice.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String username) {
        super(username + " username not found");
    }
}
