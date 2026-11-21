package com.kkansal.gatekeeper.management.config;

import com.kkansal.gatekeeper.management.aspect.ApiDataChangeOutboxAspect;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The transaction advisor must wrap the outbox aspects so their writes join the
 * business transaction. Both default to LOWEST_PRECEDENCE (undefined relative order),
 * so the transaction advisor is pinned one step higher here.
 */
@Configuration
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.LOWEST_PRECEDENCE - 1)
public class OutboxConfig {

    @Bean
    public NewTopic apiDataChangeTopic(@Value("${outbox.topic.partitions:3}") int partitions) {
        return TopicBuilder.name(ApiDataChangeOutboxAspect.TOPIC)
                .partitions(partitions)
                .replicas(1)
                .build();
    }
}
