package com.explik.diybirdyapp.persistence.generalCommand;

import com.explik.diybirdyapp.persistence.service.BinaryStorageService;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FetchAudioForTextContentCommandHandler implements SyncCommandHandler<FetchAudioForTextContentCommand, FileContentCommandResult> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private BinaryStorageService binaryStorageService;

    @Override
    public FileContentCommandResult handle(FetchAudioForTextContentCommand command) {
        var textContentId = command.getTextId();
        if (textContentId == null || textContentId.isEmpty())
            throw new RuntimeException("Text ID is empty");

        var textContentVertex = TextContentVertex.findById(traversalSource, command.getTextId());
        if (textContentVertex == null)
            throw new RuntimeException("Text content not found: " + command.getTextId());

        var mainPronunciation = textContentVertex.getMainPronunciation();
        if (mainPronunciation == null)
            return null;

        var audioContentVertex = mainPronunciation.getAudioContent();
        if (audioContentVertex == null)
            return null;

        var url = audioContentVertex.getUrl();
        var fileData = binaryStorageService.get(url);
        var fileExtension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        var contentType = switch (fileExtension) {
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "ogg" -> "audio/ogg";
            default -> "application/octet-stream";
        };

        return new FileContentCommandResult(fileData, contentType);
    }
}
