# Content Import Controller Specification

## Purpose
Move all deck import execution logic from the import tool into the backend, so the tool submits one request containing the full content manifest and the backend performs the import asynchronously.

This controller is responsible for:
- Accepting one import submission with the full content set and declared media inventory
- Starting a long-running backend job
- Accepting media uploads while the job is running (incremental/chunked)
- Exposing job status for polling
- Supporting cancellation
- Using dedicated import models (not regular flashcard CRUD DTOs)

## Goals
- One backend call from tool to start import (manifest only)
- Async execution suitable for large decks and media-heavy imports
- Upload media over multiple calls while the job runs to respect upload limits
- Pollable progress and final result (records + media)
- Cancellable jobs
- Flat `contentSet.contents` and `contentSet.concepts` arrays that enable reuse by reference
- Extendable data contract for future record/content types
- Dedicated import DTOs and models specialized for import workflows

## Non-goals
- Replacing existing flashcard CRUD endpoints for normal editing workflows
- Real-time push notifications (polling is the required mechanism)
- Guaranteeing zero-time rollback for very large imports
- Requiring all media binaries to be uploaded in the start-import request

## New Controller

### Name and Location
- Controller: `ContentImportController`
- Package: `com.explik.diybirdyapp.controller`

### Endpoints
1. `POST /content-import/jobs`
2. `POST /content-import/jobs/{jobId}/attachments`
3. `GET /content-import/jobs/{jobId}`
4. `POST /content-import/jobs/{jobId}/cancel`
5. `GET /content-import/capabilities` (optional but recommended for forward compatibility)

## Endpoint Contracts

### 1) Start import job (manifest + media declarations)
`POST /content-import/jobs`

Starts a long-running import job from a full content-set manifest.

Accepted content types:
- `application/json` (required)

Required behavior:
- Return immediately with `202 Accepted`
- Persist job metadata and submission payload reference
- Persist attachment declarations from the manifest (`fileRef`, metadata, expected constraints)
- Initialize declared attachments to `PENDING_UPLOAD`
- Queue async execution
- Do not import synchronously inside controller thread

Response (`202`):
```json
{
	"jobId": "imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A",
	"status": "QUEUED",
	"submittedAt": "2026-03-19T12:34:56Z",
	"pollAfterMs": 1500,
	"attachments": {
		"declared": 583,
		"ready": 0,
		"pending": 583
	},
	"links": {
		"status": "/content-import/jobs/imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A",
		"cancel": "/content-import/jobs/imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A/cancel",
		"uploadAttachment": "/content-import/jobs/imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A/attachments"
	}
}
```

Error codes:
- `400 Bad Request` malformed payload
- `401/403` unauthorized
- `409 Conflict` duplicate `clientRequestId` with incompatible payload
- `413 Payload Too Large` exceeds manifest size limits

### 2) Upload attachment while job is active
`POST /content-import/jobs/{jobId}/attachments`

Uploads media declared in the start request. This endpoint is called multiple times while the job is queued/validating/running.

Accepted content type:
- `multipart/form-data`

Required form fields:
- `fileRef` (must match a declared attachment)
- `chunk` (binary payload)

Optional form fields:
- `chunkIndex` (default `0`)
- `totalChunks` (default `1`)
- `chunkChecksum`
- `finalChecksum` (validated when final chunk arrives)

Required behavior:
- Accept uploads in states `QUEUED`, `VALIDATING`, `RUNNING`
- Reject uploads for terminal/cancelling jobs
- Reject undeclared `fileRef`
- Persist chunk, assemble file when all chunks are present, then mark attachment `READY`
- Enforce per-request chunk size limits
- Make upload idempotent for repeated identical chunks

Response (`202`):
```json
{
	"jobId": "imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A",
	"fileRef": "media/DE2M.mp3",
	"attachmentStatus": "UPLOADING",
	"receivedChunks": 3,
	"totalChunks": 8,
	"pollAfterMs": 1000
}
```

Response when upload is complete (`200`):
```json
{
	"jobId": "imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A",
	"fileRef": "media/DE2M.mp3",
	"attachmentStatus": "READY"
}
```

Error codes:
- `400 Bad Request` malformed multipart or invalid chunk metadata
- `401/403` unauthorized
- `404 Not Found` unknown job (or not owned by user)
- `409 Conflict` job not in upload-allowed state
- `413 Payload Too Large` chunk exceeds limits
- `422 Unprocessable Entity` checksum mismatch / invalid chunk sequence

### 3) Poll job status
`GET /content-import/jobs/{jobId}`

Returns current status, progress, upload state, warnings/errors, and final result when complete.

Response (`200`):
```json
{
	"jobId": "imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A",
	"status": "RUNNING",
	"cancellable": true,
	"cancelRequested": false,
	"submittedAt": "2026-03-19T12:34:56Z",
	"startedAt": "2026-03-19T12:34:57Z",
	"updatedAt": "2026-03-19T12:35:11Z",
	"completedAt": null,
	"stage": "WAIT_FOR_REQUIRED_MEDIA",
	"progress": {
		"totalRecords": 1200,
		"processedRecords": 434,
		"successfulRecords": 431,
		"failedRecords": 3,
		"percent": 36.17
	},
	"attachments": {
		"declared": 583,
		"ready": 240,
		"uploading": 8,
		"pending": 335,
		"failed": 0,
		"percent": 41.17,
		"missingRequiredFileRefs": ["media/DE2M.mp3"]
	},
	"result": null,
	"warnings": [],
	"errors": [],
	"pollAfterMs": 1500
}
```

Final successful result example:
```json
{
	"jobId": "imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A",
	"status": "COMPLETED",
	"stage": "FINALIZE",
	"result": {
		"createdRootType": "flashcard-deck",
		"createdRootId": "deckVertex924",
		"summary": {
			"recordsCreated": 1200,
			"mediaFilesStored": 583,
			"conceptsApplied": 799
		}
	}
}
```

Error codes:
- `404 Not Found` unknown job (or not owned by user)

### 4) Cancel job
`POST /content-import/jobs/{jobId}/cancel`

Requests cancellation of a queued/running job.

Required behavior:
- If cancellable: mark `cancelRequested=true`, return `202`
- If already terminal (`COMPLETED`, `FAILED`, `CANCELLED`): return `409`
- Cancellation is cooperative (checked between stages, record batches, and media wait points)

Response (`202`):
```json
{
	"jobId": "imp_01JQ9V3M3H2M3A9Z5Y3R1W7K0A",
	"status": "CANCELLING",
	"cancelRequested": true,
	"pollAfterMs": 1000
}
```

### 5) Capabilities discovery (recommended)
`GET /content-import/capabilities`

Returns supported schema versions and registered record/content/concept types. This lets clients evolve without hard-coding assumptions.

Example:
```json
{
	"schemaVersions": ["1.0"],
	"recordTypes": ["flashcard"],
	"contentTypes": ["text", "audio-upload", "image-upload", "video-upload"],
	"conceptTypes": ["pronunciation", "transcription"],
	"supportsDeferredMediaUpload": true,
	"maxPayloadBytes": 52428800,
	"maxAttachmentChunkBytes": 10485760,
	"maxTotalAttachmentBytesPerJob": 262144000
}
```

## Import Payload (Extendable by Design)

### Top-level request
```json
{
	"schemaVersion": "1.0",
	"clientRequestId": "tool-20260319-001",
	"importType": "content-set",
	"source": {
		"sourceSystem": "import-flashcards-tool",
		"sourceVersion": "v1.0",
		"sourceReference": "HSK_4_-_JK"
	},
	"target": {
		"containerType": "flashcard-deck",
		"metadata": {
			"name": "HSK 4 - JK",
			"description": "Imported from local tool"
		}
	},
	"contentSet": {
		"setType": "flashcard-deck",
		"setId": "hsk4-jk-2026",
		"metadata": {},
		"records": [
			{
				"recordType": "flashcard",
				"recordId": "flashcard-1",
				"attributes": {
					"deckOrder": 0
				},
				"slots": [
					{
						"slotKey": "front",
						"contentRef": "cnt-front-1",
						"conceptRefs": []
					},
					{
						"slotKey": "back",
						"contentRef": "cnt-back-1",
						"conceptRefs": ["cnc-pron-1", "cnc-trans-1"]
					}
				]
			},
			{
				"recordType": "flashcard",
				"recordId": "flashcard-2",
				"attributes": {
					"deckOrder": 1
				},
				"slots": [
					{
						"slotKey": "front",
						"contentRef": "cnt-front-2",
						"conceptRefs": []
					},
					{
						"slotKey": "back",
						"contentRef": "cnt-back-1",
						"conceptRefs": ["cnc-pron-1", "cnc-trans-1"]
					}
				]
			}
		],
		"contents": [
			{
				"contentId": "cnt-front-1",
				"contentType": "text",
				"payload": {
					"text": "get; obtain; win; earn",
					"languageId": "langVertex2"
				}
			},
			{
				"contentId": "cnt-back-1",
				"contentType": "text",
				"payload": {
					"text": "得",
					"languageId": "langVertex3"
				}
			},
			{
				"contentId": "cnt-front-2",
				"contentType": "text",
				"payload": {
					"text": "must; have to",
					"languageId": "langVertex2"
				}
			},
			{
				"contentId": "cnt-pron-audio-1",
				"contentType": "audio-upload",
				"payload": {
					"fileRef": "media/DE2M.mp3"
				}
			}
		],
		"concepts": [
			{
				"conceptId": "cnc-pron-1",
				"conceptType": "pronunciation",
				"payload": {
					"sourceContentRef": "cnt-back-1",
					"pronunciationContentRef": "cnt-pron-audio-1"
				}
			},
			{
				"conceptId": "cnc-trans-1",
				"conceptType": "transcription",
				"payload": {
					"sourceContentRef": "cnt-back-1",
					"transcription": "dé",
					"transcriptionSystem": "pinyin"
				}
			}
		],
		"attachments": [
			{
				"fileRef": "media/DE2M.mp3",
				"mimeType": "audio/mpeg",
				"sizeBytes": 34812,
				"checksum": "sha256:...",
				"required": true
			}
		]
	},
	"options": {
		"dryRun": false,
		"onError": "FAIL_FAST",
		"onCancel": "ROLLBACK"
	}
}
```

Note:
- `contentSet.contents` and `contentSet.concepts` are flat arrays.
- `contentSet.records[*].slots[*]` only references IDs (`contentRef`, `conceptRefs`) and does not inline payload.
- A single content/concept entry can be referenced by multiple records/slots.
- `contentSet.attachments` is a declaration list only.
- Binary media is uploaded afterward via `POST /content-import/jobs/{jobId}/attachments`.

### Why this is extendable
- `recordType`, `slotKey`, `contentType`, and `conceptType` are open string keys
- Type-specific fields live in `payload` (JSON object), not fixed DTO properties per type
- New content/concept types are added by registering handlers, not by changing controller endpoint shape
- Flat arrays decouple entity identity from record placement, enabling deduplication and reuse
- Attachment declaration schema can evolve independently from transport details (single-part, chunked, resumable)

## Dedicated Import Models (Required)

The controller must use dedicated import DTOs/models and must not accept normal CRUD DTOs like `FlashcardDto` or `FlashcardDeckDto`.

### Controller DTO package
`com.explik.diybirdyapp.controller.model.imports`

Required DTOs:
- `ImportJobCreateRequestDto`
- `ImportJobCreateResponseDto`
- `ImportJobStatusDto`
- `ImportJobProgressDto`
- `ImportJobResultDto`
- `ImportIssueDto`
- `ImportSourceDto`
- `ImportTargetDto`
- `ImportContentSetDto`
- `ImportRecordDto`
- `ImportRecordSlotDto`
- `ImportContentDto`
- `ImportConceptDto`
- `ImportAttachmentDto`
- `ImportAttachmentUploadResponseDto`
- `ImportAttachmentStatusDto`
- `ImportOptionsDto`

### Internal import model package
`com.explik.diybirdyapp.model.imports`

Required models:
- `ImportJobModel`
- `ImportManifestModel`
- `ImportExecutionState`
- `ImportRecordModel`
- `ImportRecordSlotModel`
- `ImportContentModel`
- `ImportConceptModel`
- `ImportAttachmentModel`
- `ImportAttachmentChunkModel`
- `ImportResultModel`

## Async Processing Architecture

### Services and components
- `ContentImportService` (submit, poll, cancel)
- `ContentImportJobRunner` (`@Async` execution)
- `ContentImportAttachmentService` (receive chunks, assemble files, verify integrity)
- `ImportJobRepository` (job metadata/status persistence)
- `ImportPayloadStore` (manifest storage)
- `ImportBinaryStore` (chunk and assembled media storage)
- `ImportRecordHandlerRegistry`
- `ImportRecordHandler` implementations (starts with flashcard)
- `ImportContentHandler` implementations (text, audio-upload, image-upload, video-upload)
- `ImportConceptHandler` implementations (pronunciation, transcription)

### Processing stages
1. `VALIDATE_REQUEST`
2. `REGISTER_ATTACHMENTS`
3. `CREATE_TARGET_CONTAINER`
4. `IMPORT_RECORDS`
5. `WAIT_FOR_REQUIRED_MEDIA` (entered whenever required media for next record batch is still pending)
6. `FINALIZE`
7. `ROLLBACK` (only when required)

Each stage updates job status and progress for polling.

## Job States and Transitions

States:
- `QUEUED`
- `VALIDATING`
- `RUNNING`
- `CANCELLING`
- `CANCELLED`
- `COMPLETED`
- `FAILED`

Transition rules:
- `QUEUED -> VALIDATING -> RUNNING`
- `RUNNING -> COMPLETED`
- `RUNNING -> FAILED`
- `QUEUED|VALIDATING|RUNNING -> CANCELLING -> CANCELLED`

Terminal states:
- `COMPLETED`, `FAILED`, `CANCELLED`

Note:
- Waiting for media is represented by stage `WAIT_FOR_REQUIRED_MEDIA` while state remains `RUNNING`.

## Cancellation Semantics

Cancellation is cooperative and safe:
- A cancel request sets `cancelRequested=true`
- Runner checks cancellation at stage boundaries, record batch boundaries, and media wait loops
- Upload endpoint rejects new chunks once job enters `CANCELLING`
- Default behavior (`onCancel=ROLLBACK`): remove entities created by this job
- Optional future mode (`onCancel=KEEP_PARTIAL`) may be added without endpoint changes

## Validation Rules

Minimum rules at submission:
- `schemaVersion` supported
- `contentSet.records` non-empty (unless `dryRun`)
- all `recordId` unique within request
- all `contentId` values unique within request
- all `conceptId` values unique within request
- every `contentRef` in `contentSet.records[*].slots[*]` must exist in `contentSet.contents`
- every `conceptRefs[]` entry in `contentSet.records[*].slots[*]` must exist in `contentSet.concepts`
- all `fileRef` values used in content/concept payloads are declared in `contentSet.attachments`
- all declared attachment `fileRef` values are unique
- every `(recordType, contentType, conceptType)` has a registered handler
- payload-level validation delegated to matching handlers

Minimum rules at upload time:
- uploaded `fileRef` must exist in declared attachments
- `chunkIndex` and `totalChunks` must be consistent per `fileRef`
- chunk size must not exceed configured limit
- final checksum (if declared) must match assembled binary

Validation failure behavior:
- Submission-time validation errors return `400`
- Upload-time validation errors return `400/409/413/422` depending failure type
- Runtime record-level failures are captured in job `errors`
- Missing required attachment at finalize produces fatal error `MISSING_REQUIRED_ATTACHMENT`
- `onError=FAIL_FAST` stops job on first fatal record error
- Future option `onError=CONTINUE` can be added without controller change

## Security and Ownership

- Auth required for all import job endpoints
- Job belongs to authenticated user ID
- Poll/cancel/upload allowed only for owning user
- Manifest and chunk upload size limits enforced
- Per-job total media quota enforced
- Optional checksum validation for attachment integrity

## Migration from Current Tool

Current tool behavior:
- Creates deck, then loops and calls `/flashcard` or `/flashcard/rich` per card
- Calls `/text-content/{id}/upload-pronunciation` and `/text-content/{id}/add-transcription` per content

New behavior:
- Tool converts local deck data to one `ImportJobCreateRequestDto` with flat `contentSet.contents` and `contentSet.concepts` arrays, while records contain only references
- Tool includes all media declarations in `contentSet.attachments`
- Tool sends one `POST /content-import/jobs` call (manifest only)
- Tool uploads media progressively via `POST /content-import/jobs/{jobId}/attachments`
- Tool polls `GET /content-import/jobs/{jobId}` until terminal state
- Tool optionally calls `POST /content-import/jobs/{jobId}/cancel`

Benefits:
- Avoids single-request upload size limits
- Fewer control-path network round trips
- Backend-controlled retries, sequencing, and validation
- Better observability and failure reporting
- Cleaner tool code (controller does orchestration)

## Backward Compatibility and Rollout

Recommended rollout:
1. Implement new controller endpoints and job service
2. Keep existing `/flashcard*` endpoints unchanged for normal CRUD
3. Add tool feature flag to switch import path
4. Migrate import tool to manifest-first + progressive media upload flow
5. Remove old import-loop code from tool after verification

## Acceptance Criteria

1. Tool can start full import with one manifest call.
2. Payload imports content and concepts via flat arrays (`contentSet.contents`, `contentSet.concepts`) rather than nested records.
3. Records reference content/concepts by ID and support reuse across multiple records/slots.
4. All media references are declared in the start request before import processing.
5. Tool can upload media in multiple requests while job is running.
6. Status endpoint provides stage + progress for records and declared attachments.
7. Cancel endpoint stops queued/running jobs and reports terminal state.
8. Controller uses import-specific DTOs/models only.
9. Payload supports extension via handler registries without endpoint redesign.
