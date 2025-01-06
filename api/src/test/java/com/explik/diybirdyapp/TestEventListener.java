package com.explik.diybirdyapp;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestEventListener<T> {
    private final List<T> events = new ArrayList<>();

    public List<T> getEvents() {
        return events;
    }

    @EventListener
    public void onEvent(T event) {
        events.add(event);
    }

    public void reset() {
        events.clear();
    }
}