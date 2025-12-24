package com.explik.diybirdyapp.persistence.command;

/**
 * Domain command to create recognizability rating input for an exercise.
 * Users must rate how well they recognize the content.
 */
public class CreateRecognizabilityRatingInputCommand implements AtomicCommand {
    // Recognizability rating typically doesn't require pre-defined options
    // The rating scale is implicit in the exercise type
}
