package ru.edi.edi_integration.service.proxy;

public interface MethodHandler {
    Object invoke(String methodName, Object arg, Class<?> returnType);
}