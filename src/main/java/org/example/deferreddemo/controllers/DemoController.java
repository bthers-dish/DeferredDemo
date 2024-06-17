package org.example.deferreddemo.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
public class DemoController {

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    DeferredResult<ResponseEntity<Object>> getResult() {
        return new DeferredResult<>();
    }
}
