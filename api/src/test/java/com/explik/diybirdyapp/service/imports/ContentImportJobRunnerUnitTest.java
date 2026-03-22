package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportIssueDto;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.model.imports.ImportJobState;
import com.explik.diybirdyapp.model.imports.ImportManifestModel;
import com.explik.diybirdyapp.controller.model.imports.ImportOptionsDto;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentImportJobRunnerUnitTest {
    @Test
    void givenJobWithMissingManifest_whenRunJob_thenMarksJobAsFailed() {
        var repository = new InMemoryImportJobRepository();
        var payloadStore = new InMemoryImportPayloadStore();
        var runner = createRunner(repository, payloadStore);

        var job = new ImportJobModel();
        job.setJobId("job-missing-manifest");
        job.setManifestKey("missing-key");
        repository.save(job);

        runner.runJob("job-missing-manifest");

        var updated = repository.findById("job-missing-manifest").orElseThrow();
        assertEquals(ImportJobState.FAILED, updated.getStatus());
        assertTrue(updated.getErrors().stream().map(ImportIssueDto::getCode).toList().contains("MANIFEST_NOT_FOUND"));
    }

    @Test
    void givenDryRunManifest_whenRunJob_thenCompletesWithDryRunSummary() {
        var repository = new InMemoryImportJobRepository();
        var payloadStore = new InMemoryImportPayloadStore();
        var runner = createRunner(repository, payloadStore);

        var manifest = new ImportManifestModel();
        manifest.setRecords(List.of());
        var options = new ImportOptionsDto();
        options.setDryRun(true);
        manifest.setOptions(options);

        var manifestKey = payloadStore.put(manifest);

        var job = new ImportJobModel();
        job.setJobId("job-dry-run");
        job.setManifestKey(manifestKey);
        repository.save(job);

        runner.runJob("job-dry-run");

        var updated = repository.findById("job-dry-run").orElseThrow();
        assertEquals(ImportJobState.COMPLETED, updated.getStatus());
        assertNotNull(updated.getResult());
        assertEquals(Boolean.TRUE, updated.getResult().getSummary().get("dryRun"));
    }

    @Test
    void givenCancelledJobBeforeStart_whenRunJob_thenEndsCancelled() {
        var repository = new InMemoryImportJobRepository();
        var payloadStore = new InMemoryImportPayloadStore();
        var runner = createRunner(repository, payloadStore);

        var manifest = new ImportManifestModel();
        var manifestKey = payloadStore.put(manifest);

        var job = new ImportJobModel();
        job.setJobId("job-cancelled");
        job.setManifestKey(manifestKey);
        job.setCancelRequested(true);
        repository.save(job);

        runner.runJob("job-cancelled");

        var updated = repository.findById("job-cancelled").orElseThrow();
        assertEquals(ImportJobState.CANCELLED, updated.getStatus());
        assertNotNull(updated.getCompletedAt());
    }

    private ContentImportJobRunner createRunner(
            ImportJobRepository importJobRepository,
            ImportPayloadStore importPayloadStore) {
        var traversalSource = TinkerGraph.open().traversal();
        return new ContentImportJobRunner(
                importJobRepository,
                importPayloadStore,
                new ImportRecordHandlerRegistry(List.of()),
                new ImportContentHandlerRegistry(List.of()),
                new ImportConceptHandlerRegistry(List.of()),
                traversalSource);
    }
}

