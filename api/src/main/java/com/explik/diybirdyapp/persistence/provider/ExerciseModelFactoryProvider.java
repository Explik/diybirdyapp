package com.explik.diybirdyapp.persistence.provider;

import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseAbstractModelFactory;
import com.explik.diybirdyapp.persistence.modelFactory.LimitedExerciseModelFactory;
import com.explik.diybirdyapp.persistence.modelFactory.ModelFactory;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseModelFactoryProvider implements GenericProvider<ModelFactory<ExerciseVertex, ExerciseModel>> {
    @Autowired
    private LimitedExerciseModelFactory limitedExerciseModelFactory;

    @Autowired
    private ExerciseAbstractModelFactory abstractModelFactory;

    @Override
    public ModelFactory<ExerciseVertex, ExerciseModel> get(String exerciseType) {
        if (exerciseType == null)
            return limitedExerciseModelFactory;

        var exerciseSchema = ExerciseSchemas.getByType(exerciseType);
        var exerciseFactory = abstractModelFactory.create(exerciseSchema);

        return exerciseFactory;
    }
}
