package com.kkansal.gatekeeper.management.publisher;

import com.kkansal.gatekeeper.management.entity.OutboxEvent;
import com.kkansal.gatekeeper.management.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
     * Blocks until every send in the batch settles. With fixedDelay this guarantees the next
     * poll never re-reads a row whose send is still in flight, which would double-publish.
     */
    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:1000}")
    public void publishPendingEvents() {

        List<OutboxEvent> batch = outboxEventRepository.findAllByOrderByCreatedAtAsc(Limit.of(batchSize));
        OUTBOX_LOGGER.debug("Outbox poll found {} pending event(s)", batch.size());
        if (batch.isEmpty()) {
            return;
        }

        CompletableFuture<?>[] sends = batch.stream()
                .map(this::send)
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(sends).exceptionally(ex -> null).join();
    }

    // The *Async variant keeps the blocking JDBC delete off Kafka's single producer I/O thread.
    private CompletableFuture<?> send(OutboxEvent event) {

        return kafkaTemplate.send(event.getTopic(), event.getPayload())
                .whenCompleteAsync((result, ex) -> {
                    if (ex == null) {
                        outboxEventRepository.deleteById(event.getId());
                    } else {
                        OUTBOX_LOGGER.warn("Failed to publish outbox event {} to {}; will retry next poll",
                                event.getId(), event.getTopic(), ex);
                    }
                });
    }
}
