package ru.edi.edi_integration.service.validator;

import ru.edi.edi_integration.model.ResultValidModel;
import org.springframework.stereotype.Component;

@Component
public interface DocumentValidator {
    ResultValidModel validate(String edifactMessage);
    void setResValModParam(ResultValidModel resultValidModel, String errMsg, int status, boolean validResult);
}
