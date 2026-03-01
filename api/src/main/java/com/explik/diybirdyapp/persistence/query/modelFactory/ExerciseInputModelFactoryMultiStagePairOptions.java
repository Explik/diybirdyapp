package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultiStagePairOptionsDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.PairVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds the initial {@link ExerciseInputMultiStagePairOptionsDto} when a
 * multi-stage tap-pairs exercise is first loaded.
 *
 * <p>The first {@code INITIAL_VISIBLE_COUNT} pairs from the exercise vertex are
 * placed into the visible left/right columns (shuffled independently).
 * The remaining pairs are available as replacement candidates on the server side
 * (they are not sent to the client until they are needed).
 */
@Component
public class ExerciseInputModelFactoryMultiStagePairOptions
        implements ContextualModelFactory<ExerciseVertex, ExerciseInputMultiStagePairOptionsDto, ExerciseRetrievalContext> {

    /** How many pairs are shown at a time. */
    public static final int INITIAL_VISIBLE_COUNT = 5;

    @Override
    public ExerciseInputMultiStagePairOptionsDto create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        var dto = new ExerciseInputMultiStagePairOptionsDto();
        dto.setType(ExerciseInputTypes.MULTI_STAGE_PAIR_OPTIONS);
        dto.setAnsweredCount(0);
        dto.setMaxPairs(10);
        dto.setMatchedPairLeftIds(new ArrayList<>());

        List<PairVertex> allPairs = (List<PairVertex>) vertex.getOptionPairs();
        int visibleCount = Math.min(INITIAL_VISIBLE_COUNT, allPairs.size());
        List<PairVertex> visiblePairs = allPairs.subList(0, visibleCount);

        var leftOptions = new ArrayList<ExerciseInputMultiStagePairOptionsDto.PairOptionsInputOptionDto>();
        var rightOptions = new ArrayList<ExerciseInputMultiStagePairOptionsDto.PairOptionsInputOptionDto>();

        for (PairVertex pair : visiblePairs) {
            leftOptions.add(buildTextOption(pair.getLeftContent()));
            rightOptions.add(buildTextOption(pair.getRightContent()));
        }

        // Shuffle each column independently so left/right order differs
        Collections.shuffle(leftOptions);
        Collections.shuffle(rightOptions);

        dto.setLeftOptions(leftOptions);
        dto.setRightOptions(rightOptions);

        // Persist initial server-side state so the evaluation manager can work statelessly
        vertex.setMsAnsweredCount(0);
        vertex.setMsMatchedLeftIds(new ArrayList<>());
        vertex.setMsCurrentLeftOptionIds(leftOptions.stream().map(o -> o.getId()).collect(Collectors.toList()));
        vertex.setMsCurrentRightOptionIds(rightOptions.stream().map(o -> o.getId()).collect(Collectors.toList()));

        return dto;
    }

    private ExerciseInputMultiStagePairOptionsDto.PairOptionsInputTextOptionDto buildTextOption(
            com.explik.diybirdyapp.persistence.vertex.ContentVertex content) {
        if (!(content instanceof TextContentVertex textContent)) {
            throw new IllegalArgumentException(
                    "Only text-content pairs are supported for multi-stage tap-pairs, got: "
                            + content.getClass().getSimpleName());
        }
        var opt = new ExerciseInputMultiStagePairOptionsDto.PairOptionsInputTextOptionDto();
        opt.setId(textContent.getId());
        opt.setText(textContent.getValue());
        return opt;
    }
}
