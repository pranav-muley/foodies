package com.feastora.food_ordering.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MapperUtils {

    private MapperUtils() {}

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public static <T> String convertObjectToString(T obj) {
        if (obj == null) return "";

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T> T convertObjectValueToResponseObject(Object source, Class<T> targetClass) {
        if(ObjectUtils.isEmpty(source))
            return null;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(source, targetClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T convertStringToResponseObject(String response, Class<T> claz) throws JsonProcessingException {
        if (ObjectUtils.isEmpty(response)) return null;
        try {
            return objectMapper.readValue(response, claz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> convertListOfObjectsToResponseObjects(List<?> sources, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sources))
            return Collections.emptyList();

        ObjectMapper objectMapper = new ObjectMapper();
        List<T> targets = new ArrayList<>();
        try {
            for (Object source : sources) {
                targets.add(objectMapper.convertValue(source, targetClass));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targets;
    }
}
