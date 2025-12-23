package com.explik.diybirdyapp.persistence.command;

import java.util.List;

/**
 * Domain command to create arrange text options input for an exercise.
 * Users must arrange text options in the correct order.
 */
public class CreateArrangeTextOptionsInputCommand implements AtomicCommand {
    private List<String> optionIds;

    public List<String> getOptionIds() {
        return optionIds;
    }

    public void setOptionIds(List<String> optionIds) {
        this.optionIds = optionIds;
    }
}
