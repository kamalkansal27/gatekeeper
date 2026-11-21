package com.kkansal.gatekeeper.management.publisher;

import com.kkansal.gatekeeper.management.entity.OutboxEvent;
import com.kkansal.gatekeeper.management.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletionException;

import static com.kkansal.gatekeeper.management.logging.Logger.OUTBOX_LOGGER;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final int batchSize;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
                        KafkaTemplate<String, String> kafkaTemplate,
                        @Value("${outbox.batch-size:100}") int batchSize) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.batchSize = batchSize;
    }

    /**
     * Sends one event at a time and stops at the first failure, so a later event for a tenant can
     * never reach Kafka ahead of an earlier one that is still waiting to be retried.
     * A send that succeeds but whose delete fails is re-sent next poll; consumers drop it by version.
     */
    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:1000}")
    public void publishPendingEvents() {

        List<OutboxEvent> batch = outboxEventRepository.findAllByOrderByCreatedAtAsc(Limit.of(batchSize));
        OUTBOX_LOGGER.debug("Outbox poll found {} pending event(s)", batch.size());

        for (OutboxEvent event : batch) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload()).join();
            } catch (CompletionException ex) {
                OUTBOX_LOGGER.warn("Failed to publish outbox event {} to {}; will retry next poll",
                        event.getId(), event.getTopic(), ex.getCause());
                return;
            }
            outboxEventRepository.deleteById(event.getId());
        }
    }
}
