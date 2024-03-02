package ru.edi.edi_integration.service;

import ru.edi.edi_integration.model.ResultValidModel;
import ru.edi.edi_integration.service.validator.XmlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class XMLService {

    private final XmlValidator xmlValidator;

    @Autowired
    public XMLService(XmlValidator xmlValidator) {
        this.xmlValidator = xmlValidator;
    }

    public void processEdifact(String id, String xml) {
        ResultValidModel resultValidModel = xmlValidator.validate(xml);
    }
}

