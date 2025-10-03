package com.github.sambhavmahajan.cloudstorageservice.exceptions;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(String email) {
        super("User with email " + email + " already exists");
    }
}
