package ru.edi.edi_integration.service.proxy;

import java.util.Properties;

public interface HandlerFactory {
    MethodHandler create(String className, Properties parameters);
}