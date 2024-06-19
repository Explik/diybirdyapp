package com.explik.diybirdyapp.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
import org.reflections.Reflections;

public abstract class GenericSerializer<T1, T2 extends Annotation> implements Serializer<T1> {
    private final String discriminator;
    private final Class<T1> objectClass;
    private final Class<T2> annotationClass;
    private final Function<T2, String> annotationConverter;
    private final ObjectMapper objectMapper;

    private HashMap<String, Class<?>> objectTypes;

    public GenericSerializer(String discriminator, Class<T1> objectClass, Class<T2> annotationClass, Function<T2, String> annotationConverter) {
        this.discriminator = discriminator;
        this.objectClass = objectClass;
        this.annotationClass = annotationClass;
        this.annotationConverter = annotationConverter;

        objectMapper =  new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String serialize(T1 obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        }
        catch (Exception ex) {
            throw new SerializerException("Failed to serialize object", ex);
        }
    }

    /*
     * Deserializes a polymorphic class (from project package) based on annotation type
     * Based on https://www.baeldung.com/java-jackson-polymorphic-deserialization
     */
    @Override
    public T1 deserialize(String json) throws SerializerException {
        try {
            InitializeObjectTypes();

            JsonNode node = objectMapper.readTree(json);
            String discriminatorValue = node.get(discriminator).asText();
            return (T1)objectMapper.treeToValue(node, objectTypes.get(discriminatorValue));
        }
        catch (Exception ex) {
            throw new SerializerException("Failed to deserialize object", ex);
        }
    }

    private void InitializeObjectTypes() {
        if (objectTypes != null)
            return;

        objectTypes = new HashMap<>();

        Reflections reflections = new Reflections("com.explik.diybirdyapp");
        Set<Class<?>> subtypes = reflections.getTypesAnnotatedWith(annotationClass);

        if (subtypes.isEmpty())
            throw new SerializerException("Failed to find annotated with " + annotationClass.getName());

        for (Class<?> subType : subtypes) {
            T2 annotation = (T2) subType.getAnnotation(annotationClass);
            if (annotation == null)
                throw new SerializerException("Failed to retrieve attribute " + annotationClass.getName() + "from " + subType.getName());

            String typeName = annotationConverter.apply(annotation);
            objectTypes.put(typeName, subType);
        }
    }
}
