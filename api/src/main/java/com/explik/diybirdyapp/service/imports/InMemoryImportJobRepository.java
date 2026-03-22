package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportJobModel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryImportJobRepository implements ImportJobRepository {
    private final ConcurrentMap<String, ImportJobModel> jobsById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> jobIdByClientRequest = new ConcurrentHashMap<>();

    @Override
    public ImportJobModel save(ImportJobModel job) {
        jobsById.put(job.getJobId(), job);

        if (job.getClientRequestId() != null && !job.getClientRequestId().isBlank()) {
            jobIdByClientRequest.put(buildClientRequestKey(job.getUserId(), job.getClientRequestId()), job.getJobId());
        }

        return job;
    }

    @Override
    public Optional<ImportJobModel> findById(String jobId) {
        return Optional.ofNullable(jobsById.get(jobId));
    }

    @Override
    public Optional<ImportJobModel> findByUserAndClientRequestId(String userId, String clientRequestId) {
        var key = buildClientRequestKey(userId, clientRequestId);
        var jobId = jobIdByClientRequest.get(key);
        if (jobId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(jobsById.get(jobId));
    }

    private String buildClientRequestKey(String userId, String clientRequestId) {
        return userId + "::" + clientRequestId;
    }
}
