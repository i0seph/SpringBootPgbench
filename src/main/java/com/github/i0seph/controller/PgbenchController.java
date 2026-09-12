package com.github.i0seph.controller;

import com.github.i0seph.service.PgbenchService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PgbenchController {

    private final PgbenchService pgbenchService;

    public PgbenchController(PgbenchService pgbenchService) {
        this.pgbenchService = pgbenchService;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getVersion() {
        return pgbenchService.getMtime();
    }
}
