package com.sonora.sonora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Sonora Spring Boot application. */
@SpringBootApplication
public final class SonoraApplication {

    /** Utility constructor. */
    private SonoraApplication() {
    }

    /**
     * Starts the Sonora application.
     *
     * @param args runtime arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(SonoraApplication.class, args);
    }

}
