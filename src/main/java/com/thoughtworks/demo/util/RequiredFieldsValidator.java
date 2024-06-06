package com.thoughtworks.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.demo.annotations.Required;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Configurable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configurable
public class RequiredFieldsValidator {

    @PrePersist
    public void validateRequiredFieldsOnPersist(Object target) {
        validateRequiredFields(target, false);
    }

    @PreUpdate
    public void validateRequiredFieldsOnUpdate(Object target) {
        validateRequiredFields(target, true);
    }


    private void validateRequiredFields(Object target, boolean isUpdating) {
        List<String> missingFields = Stream.of(target.getClass().getDeclaredFields())
                .filter(field -> isRequiredField(isUpdating, field))
                .filter(field -> getField(target, field) == null)
                .map(field -> field.getDeclaredAnnotation(JsonProperty.class).value())
                .collect(Collectors.toList());
        if (!missingFields.isEmpty()) {
            throw new RuntimeException(String.format("missing required %s fields: %s",
                    target.getClass().getSimpleName(), missingFields));
        }
    }

    private boolean isRequiredField(boolean isUpdating, Field field) {
        Required requiredAnnotation = field.getDeclaredAnnotation(Required.class);
        if (requiredAnnotation == null) {
            return false;
        }
        return isUpdating ? requiredAnnotation.updating() : requiredAnnotation.persisting();
    }

    private Object getField(Object target, Field field) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

