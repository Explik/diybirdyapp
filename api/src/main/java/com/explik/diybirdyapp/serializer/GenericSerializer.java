package com.explik.diybirdyapp.serializer;

import com.explik.diybirdyapp.annotations.DtoType;
import com.explik.diybirdyapp.model.TypeHelper;
import com.explik.diybirdyapp.model.dto.BaseDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

public abstract class GenericSerializer<T1> implements Serializer<T1> {
    private final String discriminator;
    private final Class<T1> objectClass;
    private final ObjectMapper objectMapper;

    private ModelMapper modelMapper;
    private List<Class<?>> modelTypes;

    public GenericSerializer(String discriminator, Class<T1> objectClass) {
        this.discriminator = discriminator;
        this.objectClass = objectClass;

        modelMapper = new ModelMapper();
        modelTypes = TypeHelper.getModelTypes();

        objectMapper =  new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String serialize(T1 obj) throws SerializerException {
        try {
            var objCopy = getSerializationObject(obj);
            return objectMapper.writeValueAsString(objCopy);
        }
        catch (Exception ex) {
            throw new SerializerException("Failed to serialize object", ex);
        }
    }

    @Override
    public String serializeList(List<T1> list) throws SerializerException {
        try {
            var listCopy = new ArrayList<Object>();
            for (var item : list)
                listCopy.add(getSerializationObject(item));

            return objectMapper.writeValueAsString(listCopy);
        }
        catch (Exception ex) {
            throw new SerializerException("Failed to serialize object", ex);
        }
    }

    private Object getSerializationObject(T1 obj) {
        var dtoAnnotation = TypeHelper.findAnnotation(obj.getClass(), DtoType.class);
        var dtoType = dtoAnnotation.value();

        return modelMapper.map(obj, dtoType);
    }

    /*
     * Deserializes a polymorphic class (from project package) based on annotation type
     * Based on https://www.baeldung.com/java-jackson-polymorphic-deserialization
     */
    @Override
    public T1 deserialize(String json) throws SerializerException {
        try {
            //InitializeObjectTypes();

            JsonNode node = objectMapper.readTree(json);
            String discriminatorValue = node.get(discriminator).asText();

            Class<?> type =   TypeHelper.findByDiscriminator(modelTypes, discriminatorValue);
            return (T1)objectMapper.treeToValue(node, type);
        }
        catch (Exception ex) {
            throw new SerializerException("Failed to deserialize object", ex);
        }
    }

    @Override
    public List<T1> deserializeList(String json) throws SerializerException {
        return List.of();
    }

}
