/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.service;

import cl.ucn.fondef.microsata.dao.DatabaseSeeder;
import cl.ucn.fondef.microsata.exceptions.IntegrityException;
import cl.ucn.fondef.microsata.exceptions.PreRequisitesException;
import cl.ucn.fondef.microsata.model.device.Archivo;
import cl.ucn.fondef.microsata.model.device.Equipo;
import cl.ucn.fondef.microsata.model.registry.Usuario;
import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The Registro.
 *
 * @author Diego Urrutia-Astorga.
 */
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class TestMicroSataService {

    /**
     * The Sata Service.
     */
    @Autowired
    private MicroSataService sataService;

    /**
     * The Test of Usuarios.
     */
    @SneakyThrows
    @Test
    public void testUsuarios() {

        log.debug("Starting the Test of Usuarios ..");

        // Run the database seeder
        new DatabaseSeeder(sataService).run();

        // Retrieve the DatabaseSeeder Usuario
        {
            // By id ..
            Assertions.assertTrue(sataService.retrieveUsuario(0L).isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario(1L).isPresent());

            // By rut, email
            Assertions.assertTrue(sataService.retrieveUsuario("12345678-5").isPresent());
            Assertions.assertTrue(sataService.retrieveUsuario("admin@ucn.cl").isPresent());

            // Not valid
            Assertions.assertTrue(sataService.retrieveUsuario("").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("null").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("11-9").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("i+d@ucn.cl").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("12345678-5@ucn.cl").isEmpty());
        }

        // Insert a Usuario
        {
            Usuario usuario = Usuario.builder()
                    .rut("13014491-8")
                    .email("durrutia@ucn.cl")
                    .nombre("Diego")
                    .apellido("Urrutia")
                    .password("durrutia123")
                    .estadoUsuario(Usuario.EstadoUsuario.ESTADO_ACTIVO)
                    .rolUsuario(Usuario.RolUsuario.ROL_ADMINISTRADOR)
                    .build();
            log.debug("Usuario to save: {}", usuario);

            Usuario usuarioBd = this.sataService.addUsuario(usuario);
            log.debug("Usuario saved: {}", usuarioBd);
        }
        // Insert a third Usuario
        {
            Usuario usuario = Usuario.builder()
                    .id(0L)
                    .rut("12345678-5")
                    .email("admin@ucn.cl")
                    .nombre("Universidad Catolica")
                    .apellido("del Norte")
                    .estadoUsuario(Usuario.EstadoUsuario.ESTADO_ACTIVO)
                    .rolUsuario(Usuario.RolUsuario.ROL_ADMINISTRADOR)
                    .build();

            // No pasword
            Assertions.assertThrows(PreRequisitesException.class,
                    () -> this.sataService.addUsuario(usuario));

            // No password
            usuario.setPassword("");
            Assertions.assertThrows(PreRequisitesException.class,
                    () -> this.sataService.addUsuario(usuario));
            usuario.setPassword("admin123"); // OK

            // Integrity: rut
            Assertions.assertThrows(IntegrityException.class,
                    () -> this.sataService.addUsuario(usuario));
            usuario.setRut("81518400-X"); // Not ok

            // PreRequisites: rut
            Assertions.assertThrows(PreRequisitesException.class,
                    () -> this.sataService.addUsuario(usuario));
            usuario.setRut("81518400-9"); // ok

            // Integrity: email
            Assertions.assertThrows(IntegrityException.class,
                    () -> this.sataService.addUsuario(usuario));
            usuario.setEmail("ucn@ucn.cl");

            // All ok
            Assertions.assertDoesNotThrow(() -> {
                this.sataService.addUsuario(usuario);
            });
        }

        // Retrieve a Usuario
        {
            Assertions.assertTrue(sataService.retrieveUsuario("13014491-8").isPresent());
            Assertions.assertTrue(sataService.retrieveUsuario("durrutia@ucn.cl").isPresent());

            Assertions.assertTrue(sataService.retrieveUsuario("").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("null").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("11-9").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("i+d@ucn.cl").isEmpty());
            Assertions.assertTrue(sataService.retrieveUsuario("12345678-5@ucn.cl").isEmpty());
        }

        // Authenticate
        {
            // Ok
            {
                Optional<Usuario> oUsuario = this.sataService.authenticate("13014491-8", "durrutia123");
                Assertions.assertTrue(oUsuario.isPresent());
            }
            {
                Optional<Usuario> oUsuario = this.sataService.authenticate("durrutia@ucn.cl", "durrutia123");
                Assertions.assertTrue(oUsuario.isPresent());
            }
            // Wrong
            {
                Optional<Usuario> oUsuario = this.sataService.authenticate("", "");
                Assertions.assertFalse(oUsuario.isPresent());
            }
            {
                Optional<Usuario> oUsuario = this.sataService.authenticate("13014491-8", "");
                Assertions.assertFalse(oUsuario.isPresent());
            }
            {
                Optional<Usuario> oUsuario = this.sataService.authenticate("13014491-8", "durrutia");
                Assertions.assertFalse(oUsuario.isPresent());
            }
            {
                Optional<Usuario> oUsuario = this.sataService.authenticate("13014491-0", "durrutia123");
                Assertions.assertFalse(oUsuario.isPresent());
            }
        }

        // Retrieve all the Usuarios
        {
            List<Usuario> usuarios = this.sataService.getUsuarios();
            Assertions.assertNotNull(usuarios);
            Assertions.assertEquals(3, usuarios.size());
            for (Usuario usuario : usuarios) {
                log.debug("Usuario: {}", usuario);
            }
        }

        log.debug("Done.");

    }

    /**
     * The Test of Equipos.
     */
    @SneakyThrows
    @Test
    public void testEquipos() {

        log.debug("Starting the Test of Equipos ..");

        // Run the database seeder
        new DatabaseSeeder(sataService).run();

        // Insert a Equipo
        log.debug("Add a Equipo ..");
        {
            Equipo equipo = Equipo.builder()
                    .nombre("Simulador de Lluvia")
                    .descripcion("Mix de Hardware y Software")
                    .estadoEquipo(Equipo.EstadoEquipo.ESTADO_PROTOTIPO)
                    .urlRepositorio("https://sata.disc.cl")
                    .build();
            log.debug("Equipo to Save: {}", equipo);
            this.sataService.saveEquipo(equipo);
        }

        // Retrieve Equipo
        {
            Stopwatch sw = Stopwatch.createStarted();
            Optional<Equipo> oEquipo = this.sataService.retrieveEquipo(2L);
            log.debug("RetrieveEquipo: {} -> {}", sw, oEquipo);
            Assertions.assertTrue(oEquipo.isPresent());
            Assertions.assertNotNull(oEquipo.get().getArchivos());
            Assertions.assertEquals(0, oEquipo.get().getArchivos().size());

            // Append Archivos & Tarjetas & Componentes
            {
                Equipo equipo = oEquipo.get();
                equipo.addArchivo(Archivo.builder()
                        .nombre("One")
                        .size(1L)
                        .tipoArchivo(Archivo.TipoArchivo.TIPO_JPG)
                        .build());
                equipo.addArchivo(Archivo.builder()
                        .nombre("Two")
                        .size(2L)
                        .tipoArchivo(Archivo.TipoArchivo.TIPO_PDF)
                        .build());
                equipo.addArchivo(Archivo.builder()
                        .nombre("Three")
                        .size(3L)
                        .tipoArchivo(Archivo.TipoArchivo.TIPO_PNG)
                        .build());

                sw.reset();
                sw.start();
                this.sataService.saveEquipo(equipo);
                log.debug("Equipo saved with associations: {} -> {}", sw, equipo);
            }

        }

        // Retrieve Equipo
        {
            Stopwatch sw = Stopwatch.createStarted();
            Optional<Equipo> oEquipo = this.sataService.retrieveEquipo(2L);
            log.debug("RetrieveEquipo: {} -> {}", sw, oEquipo);
            Assertions.assertTrue(oEquipo.isPresent());

            Equipo equipo = oEquipo.get();

            Assertions.assertNotNull(equipo.getArchivos());
            Assertions.assertEquals(3, oEquipo.get().getArchivos().size());

            equipo.addArchivo(Archivo.builder()
                    .nombre("Four")
                    .size(4L)
                    .tipoArchivo(Archivo.TipoArchivo.TIPO_PDF)
                    .build());

            // Save the Equipo
            this.sataService.saveEquipo(equipo);
        }

        // Retrieve Equipo (check the previous insert)
        {
            Stopwatch sw = Stopwatch.createStarted();
            Optional<Equipo> oEquipo = this.sataService.retrieveEquipo(2L);
            log.debug("RetrieveEquipo: {} -> {}", sw, oEquipo);
            Assertions.assertTrue(oEquipo.isPresent());

            Equipo equipo = oEquipo.get();

            Assertions.assertNotNull(equipo.getArchivos());
            Assertions.assertEquals(4, equipo.getArchivos().size());
            for (Archivo archivo : equipo.getArchivos()) {
                log.debug("Archivo: {}", archivo);
            }

        }

        log.debug("Test Equipos: Done.");

    }

}
