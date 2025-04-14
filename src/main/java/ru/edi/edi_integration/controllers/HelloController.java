package ru.edi.edi_integration.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.edi.edi_integration.service.HelloServiceHandler;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    private final HelloServiceHandler helloServiceHandler;

    public HelloController(HelloServiceHandler helloServiceHandler) {
        this.helloServiceHandler = helloServiceHandler;
    }

    @Operation(summary = "Send hello to someone", operationId = "handleHello")
    @PostMapping("/proxy/karolina/{karolina}")
    public String callKarolina(@PathVariable String karolina) {
        return helloServiceHandler.handleHello(karolina);
    }

    @GetMapping("/whoami")
    public String whoami(Authentication auth) {
        return auth.getAuthorities().toString();
    }

}
