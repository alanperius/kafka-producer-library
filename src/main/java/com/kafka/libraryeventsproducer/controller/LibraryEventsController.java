package com.kafka.libraryeventsproducer.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libraryeventsproducer.domain.LibraryEvent;
import com.kafka.libraryeventsproducer.domain.LibraryEventType;
import com.kafka.libraryeventsproducer.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("v1/libraryEvent")
    public ResponseEntity<LibraryEvent> createLibrary(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        log.info("before");
        long init = Calendar.getInstance().getTimeInMillis();
        libraryEventProducer.sendLibrarySync_With_Header(libraryEvent);
        long end = Calendar.getInstance().getTimeInMillis();

        log.info("after ");

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("v1/startLibraryEvent")
    public ResponseEntity<LibraryEvent> createLibraries(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        log.info("before");

        long init = Calendar.getInstance().getTimeInMillis();

        libraryEventProducer.sendLibrarySync_With_Header(libraryEvent);

        long end = Calendar.getInstance().getTimeInMillis();

        log.info("Time to Complete: {}", end-init);


        log.info("after ");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("v1/libraryEvent")
    public ResponseEntity<?> updateLibrary(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        if(libraryEvent.getLibraryEventId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("<libraryEventId> cannot be null.");
        }

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        log.info("before");
        libraryEventProducer.sendLibraryEvent(libraryEvent);
        log.info("after ");
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);

    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        System.out.println("dale");
        return ResponseEntity.status(HttpStatus.OK).body("aaa");
    }
}
