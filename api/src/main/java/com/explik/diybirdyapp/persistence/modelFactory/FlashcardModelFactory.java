package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.model.FlashcardModel;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class FlashcardModelFactory implements ModelFactory<FlashcardModel, FlashcardVertex> {
    @Override
    public FlashcardModel create(FlashcardVertex vertex) {
        var leftContent = vertex.getLeftContent();
        var rightContent = vertex.getRightContent();

        FlashcardModel model = new FlashcardModel();
        model.setId(vertex.getId());
        model.setDeckId(vertex.getDeck().getId());

        if (leftContent.getLabel().equals(TextContentVertex.LABEL)) {
            var leftTextContent = new TextContentVertex(leftContent);
            model.setLeftValue(leftTextContent.getValue());
            model.setLeftLanguage(createLanguage(leftTextContent.getLanguage()));
        }
        else model.setLeftValue("[None text content]");

        if (rightContent.getLabel().equals(TextContentVertex.LABEL)) {
            var rightTextContent = new TextContentVertex(rightContent);
            model.setRightValue(rightTextContent.getValue());
            model.setRightLanguage(createLanguage(rightTextContent.getLanguage()));
        }
        else model.setRightValue("[None text content]");

        return model;
    }

    public FlashcardLanguageModel createLanguage(LanguageVertex vertex) {
        var model = new FlashcardLanguageModel();
        model.setId(vertex.getId());
        model.setAbbreviation(vertex.getAbbreviation());
        model.setName(vertex.getName());
        return model;
    }
}
