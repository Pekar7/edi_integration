package ru.edi.edi_integration.service;

import ru.edi.edi_integration.enums.SenderCode;
import ru.edi.edi_integration.model.ResultValidModel;
import ru.edi.edi_integration.service.router.EdifactRouterService;
import ru.edi.edi_integration.service.validator.EdifactValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EdifactService {
    private final EdifactValidator edifactValidator;
    private final EdifactRouterService edifactRouterService;

    @Autowired
    public EdifactService(EdifactValidator edifactValidator, EdifactRouterService edifactRouterService) {
        this.edifactValidator = edifactValidator;
        this.edifactRouterService = edifactRouterService;
    }

    public void processEdifact(String idSender, String edifact, Boolean signatureMark, SenderCode senderCode) {
        ResultValidModel resultValidModel = edifactValidator.validate(edifact);
        if (resultValidModel.isValid()) {
            String idEdi = UUID.randomUUID().toString();
            edifactRouterService.edifactSelectorSend(idEdi, idSender, edifact, signatureMark, senderCode);
        }
    }
}
