package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.content.FlashcardDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.event.FlashcardAddedEvent;
import com.explik.diybirdyapp.event.FlashcardUpdatedEvent;
import com.explik.diybirdyapp.model.content.FlashcardModel;
import com.explik.diybirdyapp.service.FlashcardService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.*;

@RestController
public class FlashcardController {
    @Autowired
    GenericMapper<FlashcardDto, FlashcardModel> ingoingMapper;

    @Autowired
    GenericMapper<FlashcardModel, FlashcardDto> outgoingMapper;

    @Autowired
    FlashcardService service;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostMapping("/flashcard")
    public FlashcardDto create(@RequestBody FlashcardDto dto) {
        var model = ingoingMapper.map(dto);
        var newModel = service.add(model, null);

        eventPublisher.publishEvent(new FlashcardAddedEvent(this, newModel.getId()));

        return outgoingMapper.map(newModel);
    }

    @PostMapping("/flashcard/rich")
    public FlashcardDto createRich(
            @RequestPart("flashcard") FlashcardDto dto,
            @RequestPart(value = "files", required = false)MultipartFile[] file) {
        var model = ingoingMapper.map(dto);
        var newModel = service.add(model, file);

        eventPublisher.publishEvent(new FlashcardAddedEvent(this, newModel.getId()));

        return outgoingMapper.map(newModel);
    }

    @PutMapping("/flashcard")
    public FlashcardDto update(@RequestBody FlashcardDto dto) {
        var model = ingoingMapper.map(dto);
        var newModel = service.update(model, null);

        eventPublisher.publishEvent(new FlashcardUpdatedEvent(this, newModel.getId()));

        return outgoingMapper.map(newModel);
    }

    @PutMapping("/flashcard/rich")
    public FlashcardDto update(
            @RequestPart("flashcard") FlashcardDto dto,
            @RequestPart(value = "files", required = false)MultipartFile[] files) {
        var model = ingoingMapper.map(dto);
        var newModel = service.update(model, files);

        eventPublisher.publishEvent(new FlashcardUpdatedEvent(this, newModel.getId()));

        return outgoingMapper.map(newModel);
    }

    @GetMapping("/flashcard/{id}")
    public FlashcardDto get(@PathVariable("id") String id) {
        var model = service.get(id);

        return outgoingMapper.map(model);
    }

    @GetMapping("/flashcard")
    public List<FlashcardDto> getAll(@RequestParam(name = "deckId", required = false) String deckId) {
        var models = service.getAll(deckId);

        return models.stream()
            .sorted(Comparator.comparingInt(FlashcardModel::getDeckOrder))
            .map(s -> outgoingMapper.map(s))
            .collect(Collectors.toList());
    }

    @DeleteMapping("/flashcard/{id}")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @PostMapping("/flashcard/{id}/text-to-speech/{side}")
    public ResponseEntity<byte[]> generateTextToSpeech(@PathVariable("id") String id, @PathVariable("side") String side) {
        var result = service.generateTextToSpeech(id, side);

        return ResponseEntity
                .status(HttpStatus.SC_PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .header("Accept-Ranges", "bytes")
                .body(Arrays.copyOfRange(result.getContent(), 0, result.getContent().length));
    }
}
