package ru.edi.edi_integration.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSenderService {

    @Value("${project.kafka.topic.module.intIn.out.xml}")
    public String xmlOutQueue;

    @Value("${project.kafka.topic.module.intIn.out.edifact}")
    public String edifactOutQueue;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaSenderService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendXmlToConvert(String id, String edifact) {
        log.info("Send Xml with id = {} and document = {} ", id, edifact);
        kafkaTemplate.send(xmlOutQueue, id, edifact);
    }
    
    public void sendEdifactToConvert(String id, String edifact) {
        log.info("Send Edifact with id = {} and document = {} ", id, edifact);
        kafkaTemplate.send(edifactOutQueue, id, edifact);
    }

    public void sendEdifactTotransit(String edifactId, String edifactTransitDtoJson) {
    }
}
