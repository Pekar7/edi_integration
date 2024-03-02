package ru.edi.edi_integration.controllers;

import ru.edi.edi_integration.enums.SenderCode;
import ru.edi.edi_integration.service.EdifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edi")
public class EdifactController {

    private final EdifactService edifactService;

    @Autowired
    public EdifactController(EdifactService edifactService) {
        this.edifactService = edifactService;
    }

    @GetMapping("/test")
    public String testController() {
        return "App is work";
    }

    @PostMapping("/import/edifact")
    public ResponseEntity<String> importEdifact(@RequestParam String id,
                                                @RequestBody String edifact) {
        edifactService.processEdifact(id, edifact, false, SenderCode.ETRAN);
        return ResponseEntity.ok("Import successful");
    }
}
