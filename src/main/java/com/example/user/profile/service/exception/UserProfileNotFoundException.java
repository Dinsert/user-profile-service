package com.example.user.profile.service.exception;

public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException(String message) {
        super(message);
    }
}
