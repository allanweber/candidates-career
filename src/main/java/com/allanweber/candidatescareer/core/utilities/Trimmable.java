package com.allanweber.candidatescareer.core.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
public interface Trimmable {

    Logger LOGGER = LoggerFactory.getLogger(Trimmable.class);
    String BASE_PACKAGE = "com.allanweber.candidatescareer";

    default void trim() {
        trimString(this);
    }

    private void trimString(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value instanceof String) {
                    field.set(object, ((String) value).trim());
                } else if (Objects.nonNull(field.get(object)) && field.get(object).getClass().getPackageName().contains(BASE_PACKAGE)) {
                    trimString(field.get(object));
                }
            } catch (Exception e) {
                LOGGER.error("Error to trim object {}", this.getClass().getName(), e);
            }
        }
    }
}
