package ru.edi.edi_integration.service.proxy.discovery;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "discovery")
public class DiscoveryConfiguration {
    private List<DiscoveryItemDescriptor> entries;

}