package ru.edi.edi_integration.service.validator;

import ru.edi.edi_integration.model.ResultValidModel;
import org.springframework.stereotype.Service;

@Service
public class XmlValidator implements DocumentValidator {
    @Override
    public ResultValidModel validate(String edifactMessage) {
        return null; //TODO сделать валидацию по XSD схеме
    }

    @Override
    public void setResValModParam(ResultValidModel resultValidModel, String errMsg, int status, boolean validResult) {

    }
}
