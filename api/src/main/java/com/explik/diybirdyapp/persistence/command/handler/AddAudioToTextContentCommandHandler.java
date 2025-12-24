package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.AddAudioToTextContentCommand;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Command handler for adding audio to text content.
 * Delegates to CreatePronunciationVertexCommand to perform the actual graph operation.
 */
@Component
public class AddAudioToTextContentCommandHandler implements CommandHandler<AddAudioToTextContentCommand> {
    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandHandler;

    @Override
    public void handle(AddAudioToTextContentCommand command) {
        // Create pronunciation vertex with the audio
        var createPronunciationCommand = new CreatePronunciationVertexCommand();
        createPronunciationCommand.setId(command.getId());
        createPronunciationCommand.setAudioUrl(command.getAudioUrl());
        createPronunciationCommand.setSourceVertex(command.getTextContentId());
        
        createPronunciationVertexCommandHandler.handle(createPronunciationCommand);
    }
}
