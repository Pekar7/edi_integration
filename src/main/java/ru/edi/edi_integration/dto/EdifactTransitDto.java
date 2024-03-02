package ru.edi.edi_integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edi.edi_integration.enums.RecipientName;
import ru.edi.edi_integration.enums.SenderCode;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EdifactTransitDto {
    private String id;
    private SenderCode senderCode;
    private String edifact;
    private RecipientName recipientName;
}