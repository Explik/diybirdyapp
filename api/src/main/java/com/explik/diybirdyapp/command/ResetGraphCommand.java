package com.explik.diybirdyapp.command;

import com.explik.diybirdyapp.service.DataInitializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "reset-graph", description = "Clears graph and inserts dummy data")
public class ResetGraphCommand implements Runnable{
    @Autowired
    private DataInitializerService dataInitializerService;

    @Override
    public void run() {
        dataInitializerService.resetInitialData();
    }
}
