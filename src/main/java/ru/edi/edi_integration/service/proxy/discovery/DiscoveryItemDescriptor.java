package ru.edi.edi_integration.service.proxy.discovery;

import java.util.Properties;

public class DiscoveryItemDescriptor {
    private String alias;
    private String className;
    private String handlerType;
    private Properties parameters;

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getHandlerType() { return handlerType; }
    public void setHandlerType(String handlerType) { this.handlerType = handlerType; }

    public Properties getParameters() { return parameters; }
    public void setParameters(Properties parameters) { this.parameters = parameters; }
}