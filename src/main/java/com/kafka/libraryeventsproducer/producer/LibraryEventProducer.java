package com.kafka.libraryeventsproducer.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventsproducer.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class LibraryEventProducer {
    String library = "library";

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        for (int j = 0; j < 10000; j++) {
            for (int i = 1; i <= 10; i++) {
                libraryEvent.getBook().setBookName("Grêmio " + i);
                libraryEvent.setLibraryEventId(j);
                String value = objectMapper.writeValueAsString(libraryEvent);

                ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate
                        .send(
                                library
                                , libraryEvent.getLibraryEventId()
                                , value);

                listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        System.out.println("fail");
                        handleError(libraryEvent.getLibraryEventId(), value, ex);
                    }

                    @Override
                    public void onSuccess(SendResult<Integer, String> result) {
                        System.out.println("success" + result.getRecordMetadata().topic());
                        handleSuccess(libraryEvent.getLibraryEventId(), value, result);
                    }
                });
            }
        }
    }

    private void handleError(Integer libraryEventId, String value, Throwable ex) {
        log.error("Error sending the message and the exception is: {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure:", throwable.getMessage());
        }

    }


    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        //log.info("Message sent successFully for the key: {} and the value is: {}, partition is {}", key, value, result.getRecordMetadata().partition());
        log.info("Message number:: {} sent!", result.getProducerRecord().key());

    }


    public SendResult<Integer, String> sendLibrarySync(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        String value = objectMapper.writeValueAsString(libraryEvent);
        Integer key = libraryEvent.getLibraryEventId();
        SendResult<Integer, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.send("library", key, value).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("InterruptedException/ExecutionException Error sending the message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error on sendLibrarySync: {}", e.getMessage());
            throw e;
        }

        return sendResult;
    }

    public void sendLibrarySync_With_Header(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        for (int i = 0; i < 10_000_000; i++) {
            libraryEvent.setLibraryEventId(i);
            Integer key = libraryEvent.getLibraryEventId();
            String value = objectMapper.writeValueAsString(libraryEvent);
            ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, library);
            ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);
            listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("fail");
                    handleError(libraryEvent.getLibraryEventId(), value, ex);
                }

                @Override
                public void onSuccess(SendResult<Integer, String> result) {
//                    System.out.println("Message number:: " + result.getProducerRecord().key());
                    handleSuccess(libraryEvent.getLibraryEventId(), value, result);
                }
            });
        }

    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String library) {
        List<Header> headers = List.of(new RecordHeader("idTest", "save".getBytes()));
        return new ProducerRecord<>(library, null, key, value, null);
    }


}
