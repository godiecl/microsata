/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.dao;

import cl.ucn.fondef.Timer;
import cl.ucn.fondef.microsata.model.registry.Usuario;
import cl.ucn.fondef.microsata.service.MicroSataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * The Database loader.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
@Component
public class DatabaseSeeder implements CommandLineRunner {

    /**
     * The SataService.
     */
    private final MicroSataService sataService;

    /**
     * The Constructor.
     *
     * @param sataService to use.
     */
    @Autowired
    public DatabaseSeeder(final MicroSataService sataService) {
        this.sataService = sataService;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        final Timer timer = Timer.start();
        log.debug("Starting the database seeder ..");

        // Check Usuarios
        if (this.sataService.getUsuarioSize() != 0) {
            log.warn("Database already seeded, skipping!");
            return;
        }
        this.seedUsuarios();
        log.debug("Seeder done in: {}ms.", timer.millis());
    }

    /**
     * Seed the Usuarios.
     */
    private void seedUsuarios() {

        log.debug("Seeding Usuarios into the database ..");

        // The Admin Usuario
        Usuario usuario = Usuario.builder()
                .rut("12345678-5")
                .email("admin@ucn.cl")
                .nombre("Administrator")
                .apellido("Administrator")
                .password("admin123")
                .estadoUsuario(Usuario.EstadoUsuario.ESTADO_ACTIVO)
                .rolUsuario(Usuario.RolUsuario.ROL_ADMINISTRADOR)
                .build();
        log.debug("Usuario to save: {}", usuario);

        Usuario usuarioFromBd = sataService.addUsuario(usuario);
        log.debug("Usuario saved: {}", usuarioFromBd);

        log.debug("SeedUsuarios Done.");
    }
}
