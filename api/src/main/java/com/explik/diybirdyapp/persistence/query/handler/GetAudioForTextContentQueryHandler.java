package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.AudioFileModel;
import com.explik.diybirdyapp.persistence.query.GetAudioForTextContentQuery;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Query handler for fetching audio pronunciation data for text content.
 */
@Component
public class GetAudioForTextContentQueryHandler implements QueryHandler<GetAudioForTextContentQuery, AudioFileModel> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private BinaryStorageService binaryStorageService;

    @Override
    public AudioFileModel handle(GetAudioForTextContentQuery query) {
        var mainPronunciation = PronunciationVertex.findByTextContentId(traversalSource, query.getTextContentId());
        if (mainPronunciation == null) {
            return null;
        }

        var audioContentVertex = mainPronunciation.getAudioContent();
        if (audioContentVertex == null) {
            return null;
        }

        var url = audioContentVertex.getUrl();
        var fileData = binaryStorageService.get(url);
        var fileExtension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        var contentType = switch (fileExtension) {
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "ogg" -> "audio/ogg";
            default -> "application/octet-stream";
        };

        return new AudioFileModel(fileData, contentType);
    }
}
