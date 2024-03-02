package ru.edi.edi_integration.service.router;

import ru.edi.edi_integration.db.RegDataInMessageRepository;
import ru.edi.edi_integration.db.RegDataInRepository;
import ru.edi.edi_integration.dto.EdifactConvertDto;
import ru.edi.edi_integration.dto.EdifactTransitDto;
import ru.edi.edi_integration.entity.RegDataInEntity;
import ru.edi.edi_integration.entity.RegDataInMessageEntity;
import ru.edi.edi_integration.enums.EdifactType;
import ru.edi.edi_integration.enums.ReceiverId;
import ru.edi.edi_integration.enums.RecipientName;
import ru.edi.edi_integration.enums.SenderCode;
import ru.edi.edi_integration.mapper.EntityMapper;
import ru.edi.edi_integration.mapper.MapperDto;
import ru.edi.edi_integration.model.ResultValidModel;
import ru.edi.edi_integration.service.kafka.KafkaSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class EdifactRouterService {

    private final KafkaSenderService kafkaProducerService;
    private final EntityMapper entityMapper;
    private final RegDataInRepository regDataInRepository;
    private final RegDataInMessageRepository regDataInMessageRepository;
    private final MapperDto mapperDto;

    @Autowired
    public EdifactRouterService(KafkaSenderService kafkaProducerService, EntityMapper entityMapper, RegDataInRepository regDataInRepository, RegDataInMessageRepository regDataInMessageRepository, MapperDto mapperDto) {
        this.kafkaProducerService = kafkaProducerService;
        this.entityMapper = entityMapper;
        this.regDataInRepository = regDataInRepository;
        this.regDataInMessageRepository = regDataInMessageRepository;
        this.mapperDto = mapperDto;
    }

    public void edifactSelectorSend(String ediId, String senderId, String edifact, Boolean signatureMark, SenderCode senderCode) {
        try {
            List<String> params = getParams(edifact);

            String sender = params.get(0);
            String receiver = params.get(1);
            String ediType = params.get(2);

            log.info("Params for edifactSelectorSend sender: {}, receiver: {},  edifact type: {} ", sender, receiver, ediType);

            RecipientName recipientName = RecipientName.valueOf(sender);
            ReceiverId receiverId = ReceiverId.valueOf(receiver);
            EdifactType edifactType = EdifactType.valueOf(ediType);

            EdifactConvertDto messageEdifact = entityMapper.createMessageEdifactEntity(senderId, signatureMark, edifact);
            String edifactJsonDto = javaToJson(messageEdifact);

            switch (recipientName) {
                case AZ:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                case APERAK:
                                case IFCSUM:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        case GVCMPSRU_2:
                            switch (edifactType) {
                                case PARTIN:
                                case APERAK:
                                case INVOIC:
                                case IFTMIN:
                                    sendInTransit(senderId, edifact, senderCode, recipientName);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        default:
                            log.error("Not Found Receiver  " + receiverId);
                            throw new IllegalArgumentException("Not Found Receiver " + receiverId);
                    }
                    break;

                case CZI:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        default:
                            log.error("Not Found Receiver  " + receiverId);
                            throw new IllegalArgumentException("Not Found Receiver " + receiverId);
                    }
                    break;

                case IRC:
                case IKOLDO:
                case DBCPL:
                case EVREDI:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                case APERAK:
                                case IFCSUM:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        default:
                            log.error("Not Found Receiver  " + receiverId);
                            throw new IllegalArgumentException("Not Found Receiver " + receiverId);
                    }
                    break;

                case LG:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                case APERAK:
                                case IFCSUM:
                                case IFTMAN:
                                case IFTMCS:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        default:
                            log.error("Not Found Receiver  " + receiverId);
                            throw new IllegalArgumentException("Not Found Receiver " + receiverId);
                    }
                    break;

                case ISC_LDZ:
                case UBTZNCH:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                case APERAK:
                                case IFCSUM:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        case GVCMPSRU_2:
                            switch (edifactType) {
                                case PARTIN:
                                case INVOIC:
                                case IFTMIN:
                                    sendInTransit(senderId, edifact, senderCode, recipientName);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        default:
                            log.error("Not Found Receiver  " + receiverId);
                            throw new IllegalArgumentException("Not Found Receiver " + receiverId);
                    }
                    break;

                case KZ:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                case IFCSUM:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        case GVCMPSRU_2:
                            switch (edifactType) {
                                case PARTIN:
                                    sendInTransit(senderId, edifact, senderCode, recipientName);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        default:
                            log.error("Not Found Receiver  " + receiverId);
                            throw new IllegalArgumentException("Not Found Receiver " + receiverId);
                    }
                    break;

                case KZECP:
                case IVCKRG:
                case EASUPPGP:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                    }
                    break;

                case ITCMORCN:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                case APERAK:
                                case IFCSUM:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                    }
                    break;

                case TDG:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        case GVCMPSRU_2:
                            switch (edifactType) {
                                case INVOIC:
                                case IFTMIN:
                                    sendInTransit(senderId, edifact, senderCode, recipientName);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                            break;
                        default:
                            log.error("Not Found Receiver  " + receiverId);
                            throw new IllegalArgumentException("Not Found Receiver " + receiverId);
                    }
                    break;

                case YAJDK:
                    switch (receiverId) {
                        case GVCMPSRU:
                            switch (edifactType) {
                                case IFTMIN:
                                    kafkaProducerService.sendEdifactToConvert(ediId, edifactJsonDto);
                                    writeRegDataIn(ediId, senderId, edifact, senderCode);
                                    break;
                                case CONTRL:
                                    // sendCONTRL
                                    break;
                                default:
                                    log.error("Not Found Edifact type " + edifactType);
                                    throw new IllegalArgumentException("Invalid edifact type " + edifactType);
                            }
                    }
                    break;
                default:
                    log.error("Not Found Receiver  " + recipientName);
                    throw new IllegalArgumentException("Not Found Sender " + recipientName);
            }
        } catch (Exception e) {
            log.error("Failed edifactSelectorSend method {}", e.getMessage());
            throw new IllegalArgumentException("Failed edifactSelectorSend method " + e.getMessage());
        }
    }

    @Transactional(transactionManager = "TM_EDI_INT", propagation = Propagation.REQUIRES_NEW)
    public void writeRegDataIn(String id, String senderId, String edifact, SenderCode senderCode) {
        try {
            ResultValidModel resultValidModel = entityMapper.createResultValidModel(id, senderId, edifact, senderCode, true, "VALID");

            RegDataInEntity regDataInEntity = entityMapper.modelToRegDataInEntity(resultValidModel);
            RegDataInMessageEntity regDataInMessageEntity = entityMapper.modelToRegDataInMessage(resultValidModel);

            regDataInMessageRepository.saveAndFlush(regDataInMessageEntity);
            regDataInRepository.saveAndFlush(regDataInEntity);
        } catch (Exception e) {
            log.error("Failed to write Edifact data to the registration table", e);
        }
    }

    public void sendInTransit(String senderId, String edifact, SenderCode senderCode, RecipientName recipientName) {
        EdifactTransitDto edifactTransitDto = mapperDto.createEdifactTransitDto(senderId, edifact, senderCode, recipientName);
        String edifactTransitDtoJson = javaToJson(edifactTransitDto);
        try {
            kafkaProducerService.sendEdifactTotransit(senderId, edifactTransitDtoJson);
        } catch (Exception e) {
            log.error("Failed to send EdifactTransitDto via Kafka", e);
        }
    }

    @SneakyThrows
    static String javaToJson(Object edifactMsg)  {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(edifactMsg);
    }

    private List<String> getParams(String edifact) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(edifact);
        int count = 0;
        while (matcher.find()) {
            count++;
            if (count == 4 || count == 6 || count == 14) {
                list.add(matcher.group());
            }
        }
        return list;
    }
}
