package com.explik.diybirdyapp.model.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentStatusDto;
import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentUploadResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentSetDto;
import com.explik.diybirdyapp.controller.model.imports.ImportIssueDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateRequestDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobProgressDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobResultDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobStatusDto;
import com.explik.diybirdyapp.controller.model.imports.ImportOptionsDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordSlotDto;
import com.explik.diybirdyapp.controller.model.imports.ImportSourceDto;
import com.explik.diybirdyapp.controller.model.imports.ImportTargetDto;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportDtoAndModelUnitTest {
    @Test
    void givenImportDtoAndModelClasses_whenUsingGettersAndSetters_thenValuesRoundTrip() throws Exception {
        var classes = List.of(
                ImportAttachmentDto.class,
                ImportAttachmentStatusDto.class,
                ImportAttachmentUploadResponseDto.class,
                ImportConceptDto.class,
                ImportContentDto.class,
                ImportContentSetDto.class,
                ImportIssueDto.class,
                ImportJobCreateRequestDto.class,
                ImportJobCreateResponseDto.class,
                ImportJobProgressDto.class,
                ImportJobResultDto.class,
                ImportJobStatusDto.class,
                ImportOptionsDto.class,
                ImportRecordDto.class,
                ImportRecordSlotDto.class,
                ImportSourceDto.class,
                ImportTargetDto.class,
                ImportAttachmentChunkModel.class,
                ImportAttachmentModel.class,
                ImportManifestModel.class,
                ImportResultModel.class,
                ImportExecutionState.class,
                ImportJobModel.class
        );

        for (var clazz : classes) {
            var instance = clazz.getDeclaredConstructor().newInstance();
            var beanInfo = Introspector.getBeanInfo(clazz, Object.class);

            for (var property : beanInfo.getPropertyDescriptors()) {
                Method writeMethod = property.getWriteMethod();
                Method readMethod = property.getReadMethod();
                if (writeMethod == null || readMethod == null) {
                    continue;
                }

                var sampleValue = sampleValue(property.getPropertyType(), property.getName());
                if (sampleValue == null) {
                    continue;
                }

                writeMethod.invoke(instance, sampleValue);
                var readValue = readMethod.invoke(instance);
                assertEquals(sampleValue, readValue,
                        "Roundtrip failed for " + clazz.getSimpleName() + "." + property.getName());
            }
        }
    }

    @Test
    void givenImportExecutionState_whenPercentRequested_thenCalculatesBoundedPercentage() {
        var state = new ImportExecutionState();

        assertEquals(0.0, state.getPercent());

        state.setTotalRecords(4);
        state.setProcessedRecords(2);
        assertEquals(50.0, state.getPercent());

        state.setProcessedRecords(10);
        assertEquals(100.0, state.getPercent());
    }

    @Test
    void givenImportAttachmentModel_whenChunksComplete_thenIsCompleteTrue() {
        var attachment = new ImportAttachmentModel();
        attachment.setTotalChunks(2);

        var chunk0 = new ImportAttachmentChunkModel();
        chunk0.setChunkIndex(0);
        var chunk1 = new ImportAttachmentChunkModel();
        chunk1.setChunkIndex(1);

        attachment.getChunks().put(0, chunk0);
        assertEquals(1, attachment.getReceivedChunks());
        assertTrue(!attachment.isComplete());

        attachment.getChunks().put(1, chunk1);
        assertTrue(attachment.isComplete());
    }

    @Test
    void givenImportJobModel_whenStatusChanges_thenTerminalAndUploadAllowedBehaveAsExpected() {
        var job = new ImportJobModel();

        assertTrue(job.isUploadAllowed());
        assertTrue(!job.isTerminal());

        job.setStatus(ImportJobState.COMPLETED);
        assertTrue(job.isTerminal());
        assertTrue(!job.isUploadAllowed());

        job.setStatus(ImportJobState.RUNNING);
        assertTrue(!job.isTerminal());
        assertTrue(job.isUploadAllowed());

        assertNotNull(job.getAttachments());
        assertNotNull(job.getWarnings());
        assertNotNull(job.getErrors());
        assertNotNull(job.getExecutionState());
    }

    @Test
    void givenImportEnums_whenAccessed_thenExposeExpectedValues() {
        assertEquals(4, ImportAttachmentState.values().length);
        assertEquals(7, ImportJobState.values().length);
        assertEquals(7, ImportStage.values().length);
        assertEquals(ImportAttachmentState.READY, ImportAttachmentState.valueOf("READY"));
        assertEquals(ImportJobState.CANCELLED, ImportJobState.valueOf("CANCELLED"));
        assertEquals(ImportStage.FINALIZE, ImportStage.valueOf("FINALIZE"));
    }

    private Object sampleValue(Class<?> type, String propertyName) throws Exception {
        if (type == String.class) {
            return propertyName + "-value";
        }

        if (type == int.class || type == Integer.class) {
            return 42;
        }

        if (type == long.class || type == Long.class) {
            return 420L;
        }

        if (type == boolean.class || type == Boolean.class) {
            return true;
        }

        if (type == double.class || type == Double.class) {
            return 42.5;
        }

        if (type == Instant.class) {
            return Instant.parse("2024-01-01T00:00:00Z");
        }

        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>(List.of("item"));
        }

        if (Map.class.isAssignableFrom(type)) {
            return new HashMap<>(Map.of("key", "value"));
        }

        if (type.isEnum()) {
            return type.getEnumConstants()[0];
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }
}

