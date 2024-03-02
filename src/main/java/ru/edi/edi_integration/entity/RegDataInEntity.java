package ru.edi.edi_integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "reg_data_in")
public class RegDataInEntity {

    @Id
    @Column(name = "id", nullable = false)
    public String id;

    @Column(name = "inner_process_id")
    public String inner_process_id;

    @Column(name = "status")
    private Integer status;

    @Column(name = "document_mode_id")
    private String documentModeID;

    @Column(name = "sender_code")
    private String senderCode;

    @Column(name = "date_create", updatable = false)
    private Date dateCreate;

    @Column(name = "date_write", updatable = false, insertable = false)
    private Date dateWrite;

    @Column(name = "date_update", insertable = false)
    private Date dateUpdate;

    @Column(name = "error_message")
    private String errorMessage;

}
