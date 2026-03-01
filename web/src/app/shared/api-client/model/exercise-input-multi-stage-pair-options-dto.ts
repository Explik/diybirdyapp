import { ExerciseInputDto } from './exercise-input-dto';

export interface MultiStagePairOptionsFeedback {
    correctPairs: Array<{ leftId: string; rightId: string }>;
    incorrectPairs: Array<{ leftId: string; rightId: string }>;
    /** Replacement option for the left column – only present on a correct match when a replacement exists. */
    replacementLeft?: MultiStagePairOptionsOption;
    /** Replacement option for the right column – only present on a correct match when a replacement exists. */
    replacementRight?: MultiStagePairOptionsOption;
}

export interface MultiStagePairOptionsOption {
    id: string;
    text: string;
}

export interface ExerciseInputMultiStagePairOptionsDto extends ExerciseInputDto {
    leftOptionType: string;
    rightOptionType: string;
    leftOptions: Array<MultiStagePairOptionsOption>;
    rightOptions: Array<MultiStagePairOptionsOption>;
    answeredCount: number;
    maxPairs: number;
    matchedPairLeftIds: string[];
    selectedPair?: { leftId: string; rightId: string };
    feedback?: MultiStagePairOptionsFeedback;
}
