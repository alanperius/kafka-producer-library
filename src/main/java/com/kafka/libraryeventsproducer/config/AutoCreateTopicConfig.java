package com.kafka.libraryeventsproducer.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateTopicConfig {

    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder.name("library")
                .partitions(6)
                .replicas(3)//should be less ou equals to cluster numbers
                .build();
    }

}
