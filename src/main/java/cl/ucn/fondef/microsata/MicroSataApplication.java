/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The Sata Spring Boot application.
 *
 * @author Diego Urrutia-Astorga.
 */
@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
public class MicroSataApplication {

    /**
     * The Main.
     *
     * @param args to use.
     */
    public static void main(String[] args) {
        SpringApplication.run(MicroSataApplication.class, args);
    }

}
