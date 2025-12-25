package com.explik.diybirdyapp.manager.configurationManager;

import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;

/**
 * Interface for managing configuration option selection processes.
 * Each configuration type can have its own manager to handle multi-step selection.
 */
public interface ConfigurationManager {
    /**
     * Process the current selection and return the next available options.
     * 
     * @param request The current selection state
     * @return The next set of available options or final configuration
     */
    ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto request);
    
    /**
     * Check if this manager can handle the given selection state.
     * 
     * @param request The selection state to check
     * @return true if this manager can handle the request
     */
    boolean canHandle(ConfigurationOptionsDto request);
}
