package ru.edi.edi_integration.db;

import ru.edi.edi_integration.entity.RegDataInMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegDataInMessageRepository extends JpaRepository<RegDataInMessageEntity, String> {
}