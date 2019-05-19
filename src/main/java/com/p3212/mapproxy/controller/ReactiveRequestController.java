package com.p3212.mapproxy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReactiveRequestController {

    @GetMapping("/{first}/{second}/{third}.png")
    public ResponseEntity<?> getMapTile(@PathVariable(value = "first") int x,
                                        @PathVariable(value = "second") int y, @PathVariable(value = "third") int z) {

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(byteArrayHttpMessageConverter());
    }

}
