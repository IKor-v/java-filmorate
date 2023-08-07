package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Incorrect address")
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
        log.info(message);
    }
}
