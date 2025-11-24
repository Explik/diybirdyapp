package com.explik.diybirdyapp.command;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.UUID;

@Component
@Command(name = "create-language", description = "Create a new language")
public class CreateLanguageCommand implements Runnable {
    @CommandLine.Option(names = { "-id", "--id" }, description = "The ID of the language")
    public String id;

    @CommandLine.Option(names = { "-n", "--name" }, description = "The name of the language", required = true)
    public String name;

    @CommandLine.Option(names = { "--isoCode" }, description = "The isoCode of the language", required = true)
    public String isoCode;

    @Autowired
    public LanguageRepository languageRepository;

    public void run() {
        var languageId = (id != null) ? id : UUID.randomUUID().toString();
        var language = new FlashcardLanguageDto();
        language.setId(languageId);
        language.setName(name);
        language.setIsoCode(isoCode);

        languageRepository.add(language);
    }
}
