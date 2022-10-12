/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Test the timming of PasswordEncoder.
 */
@Slf4j
public class TestPasswordEncoder {

    /**
     * The Hasher.
     */
    private static final PasswordEncoder PASSWORD_ENCODER = new Argon2PasswordEncoder(
            16,
            32,
            Runtime.getRuntime().availableProcessors() * 2, // 1 cpu
            1 << 16, // 2^16 (64MB). Official: 12
            10 // Official: 3
    );

    /**
     * The Encoding Test.
     */
    @Test
    public void testParameters() {

        // Rounds to test
        int rounds = 20;

        // The password
        String password = "durrutia123";

        for (int i = 0; i < rounds; i++) {
            // Encode test
            Instant start = Instant.now();
            String hash = PASSWORD_ENCODER.encode(password);
            Instant end = Instant.now();
            log.debug("Time encode {}: {}ms.", (i + 1), ChronoUnit.MILLIS.between(start, end));

            // Check!
            PASSWORD_ENCODER.matches(password, hash);
        }
    }

}
