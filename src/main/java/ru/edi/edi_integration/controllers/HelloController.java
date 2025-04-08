package ru.edi.edi_integration.controllers;

import org.springframework.web.bind.annotation.*;
import ru.edi.edi_integration.service.HelloServiceHandler;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    private final HelloServiceHandler helloServiceHandler;

    public HelloController(HelloServiceHandler helloServiceHandler) {
        this.helloServiceHandler = helloServiceHandler;
    }

    @GetMapping("/proxy/karolina/{karolina}")
    public String callKarolina(@PathVariable String karolina) {
        return helloServiceHandler.handleHello(karolina);
    }
}
