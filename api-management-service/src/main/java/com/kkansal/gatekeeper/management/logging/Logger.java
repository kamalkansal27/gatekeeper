package com.kkansal.gatekeeper.management.logging;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Logger {

    public static final org.slf4j.Logger DATA_SEED_LOGGER = LoggerFactory.getLogger("DATA_SEEDING");
    public static final org.slf4j.Logger AUTH_LOGGER = LoggerFactory.getLogger("AUTH");
}
