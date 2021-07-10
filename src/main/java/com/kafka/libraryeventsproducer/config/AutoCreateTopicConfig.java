package com.kafka.libraryeventsproducer.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.kafka.libraryeventsproducer.config.Topics.*;

@Configuration
public class AutoCreateTopicConfig {

    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder.name(LIBRARY)
                .partitions(120)
                .replicas(2)//should be less ou equals to cluster numbers
                .build();
    }

}
