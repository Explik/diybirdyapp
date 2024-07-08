package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.Discriminator;
import com.explik.diybirdyapp.serializer.SerializerException;
import com.explik.diybirdyapp.model.dto.BaseDTO;
import com.syncleus.ferma.AbstractVertexFrame;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;

public class TypeHelper {
    public static List<Class<?>> modelTypes = null;
    public static List<Class<? extends BaseDTO>> dtoTypes = null;
    public static List<Class<? extends AbstractVertexFrame>> vertexTypes = null;

    public static <T>  Class<? extends T> findByDiscriminator(List<Class<? extends T>> types, String discriminator) {
        for(var type : types) {
            Discriminator annotation = findAnnotation(type, Discriminator.class);

            if (annotation == null)
                continue;

            if (annotation.value().equals(discriminator))
                return type;
        }
        throw new SerializerException("Failed to find vertex class for " + discriminator);
    }

    public static List<Class<?>> getModelTypes() {
        InitializeTypes();

        return modelTypes;
    }

    public static List<Class<? extends BaseDTO>> getDtoTypes() {
        InitializeTypes();

        return dtoTypes;
    }

    public static List<Class<? extends AbstractVertexFrame>> getVertexTypes() {
        InitializeTypes();

        return vertexTypes;
    }

    public static List<Class<? extends AbstractVertexFrame>> getVertexTypes(Class<?> superClass) {
        InitializeTypes();

        return vertexTypes
            .stream()
            .filter(superClass::isAssignableFrom)
            .toList();
    }

    private static void InitializeTypes() {
        Reflections reflections = new Reflections("com.explik.diybirdyapp");

        if (modelTypes == null) {
            Set<Class<?>> subtypes = reflections.getTypesAnnotatedWith(Discriminator.class);

            if (subtypes.isEmpty())
                throw new SerializerException("Failed to find classes with discriminator");

            modelTypes = new ArrayList<>();
            modelTypes.addAll(subtypes);
        }

        if (dtoTypes == null) {
            Set<Class<? extends BaseDTO>> subtypes = reflections.getSubTypesOf(BaseDTO.class);

            if (subtypes.isEmpty())
                throw new SerializerException("Failed to find subclasses of BaseDTO");

            dtoTypes = new ArrayList<>();
            dtoTypes.addAll(subtypes);
        }

        if (vertexTypes == null) {
            Set<Class<? extends AbstractVertexFrame>> subtypes = reflections.getSubTypesOf(AbstractVertexFrame.class);

            if (subtypes.isEmpty())
                throw new SerializerException("Failed to find subclasses of AbstractVertexFrame");

            vertexTypes = new ArrayList<>();
            vertexTypes.addAll(subtypes);
        }
    }

    // Finds annotation in inheritance hierarchy
    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationClass) {
        Set<Class<?>> visited = new HashSet<>();
        return findAnnotationRecursive(clazz, annotationClass, visited);
    }

    private static <A extends Annotation> A findAnnotationRecursive(Class<?> clazz, Class<A> annotationClass, Set<Class<?>> visited) {
        if (clazz == null || visited.contains(clazz)) {
            return null;
        }

        // Check if the current class has the annotation
        A annotation = clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }

        visited.add(clazz);

        // Check the superclass
        A foundAnnotation = findAnnotationRecursive(clazz.getSuperclass(), annotationClass, visited);
        if (foundAnnotation != null) {
            return foundAnnotation;
        }

        // Check the interfaces
        for (Class<?> iface : clazz.getInterfaces()) {
            foundAnnotation = findAnnotationRecursive(iface, annotationClass, visited);
            if (foundAnnotation != null) {
                return foundAnnotation;
            }
        }

        return null;
    }
}
