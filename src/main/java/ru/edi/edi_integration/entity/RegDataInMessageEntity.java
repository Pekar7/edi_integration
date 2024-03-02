package ru.edi.edi_integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reg_data_in_message")
public class RegDataInMessageEntity {

    @Id
    @Column(name = "id", nullable = false, insertable = false)
    private String id;

    @Column(name = "message")
    private String message;
}