package com.explik.diybirdyapp.model.admin;

import java.util.List;

public class ConfigurationOptionsDto {
    private String selection;
    private List<String> selectedOptions = List.of();
    private List<Option> availableOptions;
    private boolean lastSelection;

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public List<Option> getAvailableOptions() {
        return availableOptions;
    }

    public void setAvailableOptions(List<Option> availableOptions) {
        this.availableOptions = availableOptions;
    }

    public boolean isLastSelection() {
        return lastSelection;
    }

    public void setLastSelection(boolean lastSelection) {
        this.lastSelection = lastSelection;
    }

    public record Option(String id, Object option) { }
}
