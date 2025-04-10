package ru.edi.edi_integration.service.discovery;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "discovery")
public class DiscoveryConfiguration {
    private List<DiscoveryItemDescriptor> entries;

    public List<DiscoveryItemDescriptor> getEntries() {
        return entries;
    }

    public void setEntries(List<DiscoveryItemDescriptor> entries) {
        this.entries = entries;
    }
}