package ru.edi.edi_integration.mapper;

import ru.edi.edi_integration.dto.EdifactConvertDto;
import ru.edi.edi_integration.entity.RegDataInEntity;
import ru.edi.edi_integration.entity.RegDataInMessageEntity;
import ru.edi.edi_integration.enums.SenderCode;
import ru.edi.edi_integration.exception.StatusCode;
import ru.edi.edi_integration.model.ResultValidModel;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class EntityMapper {

    public RegDataInEntity modelToRegDataIn(ResultValidModel resultValidModel) {
        RegDataInEntity regDataInEntity = new RegDataInEntity(); //TODO дополнить
        regDataInEntity.setId(resultValidModel.getId());
        return regDataInEntity;
    }

    public EdifactConvertDto createMessageEdifactEntity(String senderId, boolean signatureMark, String edifact) {
        EdifactConvertDto edifactConvertDto = new EdifactConvertDto();
        edifactConvertDto.setIdSender(senderId);
        edifactConvertDto.setEdifact(edifact);
        edifactConvertDto.setSignatureMark(signatureMark);
        return edifactConvertDto;
    }

    public RegDataInEntity modelToRegDataInEntity(ResultValidModel resultValidModel) {
        RegDataInEntity regDataInEntity = new RegDataInEntity();
        regDataInEntity.setId(resultValidModel.getId());
        regDataInEntity.setInner_process_id(resultValidModel.getInner_process_id());
        regDataInEntity.setStatus(resultValidModel.getStatus());
        regDataInEntity.setDocumentModeID(resultValidModel.getDocumentModeID());
        regDataInEntity.setSenderCode(resultValidModel.getSenderCode());
        regDataInEntity.setDateCreate(resultValidModel.getDateCreate());
        regDataInEntity.setErrorMessage(resultValidModel.getErrorMessage());
        regDataInEntity.setDateUpdate(resultValidModel.getDateUpdate());
        regDataInEntity.setDateWrite(resultValidModel.getDateWrite());
        regDataInEntity.setDocumentModeID(resultValidModel.getDocumentModeID());

        return regDataInEntity;
    }

    public RegDataInMessageEntity modelToRegDataInMessage(ResultValidModel resultValidModel) {
        RegDataInMessageEntity regDataInMessageEntity = new RegDataInMessageEntity();
        regDataInMessageEntity.setId(resultValidModel.getId());
        regDataInMessageEntity.setMessage(resultValidModel.getDocument());

        return regDataInMessageEntity;
    }

    public ResultValidModel createResultValidModel(String id, String senderId, String edifact, SenderCode senderCode, boolean validResult, String errorMessage) {
        ResultValidModel resultValidModel = new ResultValidModel();
        resultValidModel.setId(id);
        resultValidModel.setInner_process_id(senderId);
        resultValidModel.setValid(validResult);
        resultValidModel.setSenderCode(senderCode.name());
        resultValidModel.setErrorMessage(errorMessage);
        resultValidModel.setStatus(validResult ? StatusCode.INT_IN_OK.getCode() : StatusCode.INT_IN_NOT_CLASSIFIED_ERROR.getCode());
        resultValidModel.setDocument(edifact);
        resultValidModel.setDateCreate(new Timestamp(new Date().getTime()));
        resultValidModel.setDateWrite(new Timestamp(new Date().getTime()));
        return resultValidModel;
    }

}
