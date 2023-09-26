package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
        log.info(message);
    }

}
