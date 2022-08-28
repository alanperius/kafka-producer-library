package com.kafka.libraryeventsproducer.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.kafka.libraryeventsproducer.config.Topics.*;

@Configuration
public class AutoCreateTopicConfig {

    @Bean
    public NewTopic topicName1() {
        return TopicBuilder.name(TOPICNAME)
                .partitions(20)
                .build();
    }

    @Bean
    public NewTopic topicName2() {
        return TopicBuilder.name(TOPICNAME2)
                .partitions(1)
                .build();
    }

}
