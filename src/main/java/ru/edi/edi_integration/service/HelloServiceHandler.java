package ru.edi.edi_integration.service;


import ru.edi.convert_api.HelloService;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceHandler {

    private final HelloService helloService;

    public HelloServiceHandler(HelloService helloService) {
        this.helloService = helloService;
    }

    public String handleHello(String name) {
        return helloService.sendHello(name);
    }

    public String handleDocument(String name) {
        return helloService.getDocument();
    }
}

