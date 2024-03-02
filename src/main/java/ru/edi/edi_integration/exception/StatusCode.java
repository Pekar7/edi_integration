package ru.edi.edi_integration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum StatusCode {

    DOCUMENT_TYPE_NOT_FOUND(NOT_FOUND),
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR),
    NO_CONTENT_CUST(NO_CONTENT),
    INCORRECT_PARAM(BAD_REQUEST),
    ENTITY_TYPE_NOT_FOUND(NOT_FOUND),
    CONTENT_FILE_NOT_FOUND(NOT_FOUND),
    FILE_NOT_FOUND(NOT_FOUND),
    FILE_STATUS_NOT_FOUND(NOT_FOUND),
    PERMISSION_DENIED(FORBIDDEN),


    INT_IN_OK(40, "Сообщение успешно обработано модулем интеграции приема", OK),
    INT_IN_FORMAT_CONTROL_ERROR(41, "Ошибка форматного контроля", BAD_REQUEST),
    INT_IN_LOGIC_CONTROL_ERROR(42, "Ошибка логического контроля", BAD_REQUEST),
    INT_IN_NOT_CLASSIFIED_ERROR(43, "Неклассифицированная ошибка", BAD_REQUEST),
    INT_IN_DUPLICATED_MESSAGE_ERROR(44, "Ошибка - попытка повторной отправки сообщения", BAD_REQUEST),


    CONV_OK(50, "Сообщение успешно обработано модулем конвертации"),
    CONV_ERROR(51, "Ошибка в модуле конвертации"),

    HENDLER_OK(60, "Сообщение успешно обработано модулем ведения базы данных"),
    HENDLER_ERROR(61, "Ошибка в модуле ведения базы данных"),

    MON_OK(70, "Сообщение успешно обработано модулем ведения базы данных"),
    MON_ERROR(71, "Ошибка в модуле ведения базы данных"),

    INT_OUT_OK(80, "Сообщение успешно обработано модулем ведения базы данных"),
    INT_OUT_ERROR(81, "Ошибка в модуле ведения базы данных");

    private int code;
    private String description;
    private HttpStatus httpStatus;

    StatusCode(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    StatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    StatusCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
