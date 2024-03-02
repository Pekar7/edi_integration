package ru.edi.edi_integration.mapper;

import ru.edi.edi_integration.dto.EdifactTransitDto;
import ru.edi.edi_integration.enums.RecipientName;
import ru.edi.edi_integration.enums.SenderCode;
import org.springframework.stereotype.Component;

@Component
public class MapperDto {

    public EdifactTransitDto createEdifactTransitDto(String senderId, String edifact, SenderCode senderCode, RecipientName recipientName) {
        EdifactTransitDto edifactTransitDto = new EdifactTransitDto();
        edifactTransitDto.setId(senderId);
        edifactTransitDto.setEdifact(edifact);
        edifactTransitDto.setSenderCode(senderCode);
        edifactTransitDto.setRecipientName(recipientName);
        return edifactTransitDto;
    }
}
