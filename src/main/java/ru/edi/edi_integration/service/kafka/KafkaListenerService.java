package ru.edi.edi_integration.service.kafka;


import ru.edi.edi_integration.enums.SenderCode;
import ru.edi.edi_integration.service.EdifactService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class KafkaListenerService implements ConsumerSeekAware {

    private final EdifactService edifactService;

    public KafkaListenerService(EdifactService edifactService) {
        this.edifactService = edifactService;
    }

    @Transactional(transactionManager = "tmKafkaMessage", rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(groupId = "${project.kafka.consumer.group.id}", topics = {"${project.kafka.module.topic.edifact.adapted.encrypt}"}, containerFactory = "batchFactoryInt")
    public void edifactEncryptedInternalListener(List<ConsumerRecord<String, String>> messages) {
        try {
            for (ConsumerRecord<String, String> message : messages) {
                processEdifactListener(message, false);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processEdifactListener(ConsumerRecord<String, String> message, Boolean signatureMark) {
        try {
            String edifactId = message.key();
            String edifact = message.value();
            edifactService.processEdifact(edifactId, edifact, signatureMark, SenderCode.EDIFACT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}