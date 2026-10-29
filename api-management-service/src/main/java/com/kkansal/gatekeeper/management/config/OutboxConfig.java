package com.kkansal.gatekeeper.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
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
}
