package com.kafka.libraryeventsproducer.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libraryeventsproducer.domain.LibraryEvent;
import com.kafka.libraryeventsproducer.domain.LibraryEventType;
import com.kafka.libraryeventsproducer.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
//        libraryEventProducer.sendLibraryEvent(libraryEvent);
//        SendResult<Integer, String> sendResult = libraryEventProducer.sendLibrarySync(libraryEvent);

        libraryEventProducer.sendLibrarySync_With_Header(libraryEvent);
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
}
