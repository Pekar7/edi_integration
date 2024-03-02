package ru.edi.edi_integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EdifactConvertDto {
    private String idSender;
    private boolean signatureMark;
    private String edifact;
}
