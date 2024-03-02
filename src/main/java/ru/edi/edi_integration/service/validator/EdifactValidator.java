package ru.edi.edi_integration.service.validator;

import ru.edi.edi_integration.exception.StatusCode;
import ru.edi.edi_integration.model.ResultValidModel;
import org.springframework.stereotype.Service;

@Service
public class EdifactValidator implements DocumentValidator {
    @Override
    public ResultValidModel validate(String edifactMessage) {
        ResultValidModel resultValidModel = new ResultValidModel();
        if (edifactMessage.contains("UNOY")) {
            resultValidModel.setValid(true);
            setResValModParam(resultValidModel, "VALID", StatusCode.INT_IN_OK.getCode(), true);
        } else {
            resultValidModel.setValid(false);
            setResValModParam(resultValidModel, "NOT VALID", StatusCode.INT_IN_FORMAT_CONTROL_ERROR.getCode(), false);
        }
        return resultValidModel; // TODO сделать валидацию по Edifact библиотеке и заполнить все поля в ResultValidModel
    }


    public void setResValModParam(ResultValidModel resultValidModel, String errMsg, int status, boolean validResult) {
        resultValidModel.setErrorMessage(errMsg);
        resultValidModel.setStatus(status);
        resultValidModel.setValid(validResult);
    }
}
