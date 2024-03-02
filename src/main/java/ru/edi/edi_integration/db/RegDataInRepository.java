package ru.edi.edi_integration.db;

import ru.edi.edi_integration.entity.RegDataInEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegDataInRepository extends JpaRepository<RegDataInEntity, String> {
}

