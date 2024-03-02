package ru.edi.edi_integration.model;

import lombok.Data;

import java.util.Date;

@Data
public class ResultValidModel {
    private String id;
    private boolean isValid;
    private String inner_process_id;
    private Integer status;
    private String documentModeID;
    private String senderCode;
    private String version;
    private Date dateCreate;
    private Date dateWrite;
    private Date dateUpdate;
    private String errorMessage;
    private String hashCode;
    private String document;
}
