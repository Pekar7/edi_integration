package ru.edi.edi_integration.entity;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class MessageEdifactEntity {

    @Column(name = "signature_mark")
    private boolean signatureMark;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "edifact")
    private String edifact;

}