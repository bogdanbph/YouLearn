package com.youlearn.youlearn.exception.notfound;

import com.youlearn.youlearn.exception.NotFoundException;

public class AppUserNotFoundException extends NotFoundException {
    public AppUserNotFoundException(Long id) {
        super("User with id " + id + " does not exist");
    }
}
