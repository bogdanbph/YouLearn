package com.youlearn.youlearn.exception.notfound;

import com.youlearn.youlearn.exception.NotFoundException;

public class PageNotFoundException extends NotFoundException {
    public PageNotFoundException(Integer pageNumber) {
        super("page.not.found", "Page of interviews with id " + pageNumber + " does not exist");
    }
}
