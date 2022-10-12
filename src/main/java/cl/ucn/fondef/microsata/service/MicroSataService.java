package cl.ucn.fondef.microsata.service;

import cl.ucn.fondef.ValidationUtils;
import cl.ucn.fondef.microsata.dao.EquipoRepository;
import cl.ucn.fondef.microsata.dao.UsuarioRepository;
import cl.ucn.fondef.microsata.exceptions.IntegrityException;
import cl.ucn.fondef.microsata.exceptions.PreRequisitesException;
import cl.ucn.fondef.microsata.model.device.Equipo;
import cl.ucn.fondef.microsata.model.registry.Usuario;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * The Main Service.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
@Service
@Scope("singleton")
public class MicroSataService {

    /**
     * The Argon hasher.
     */
    private static final PasswordEncoder PASSWORD_ENCODER = new Argon2PasswordEncoder(
            16,
            32,
            Runtime.getRuntime().availableProcessors() * 2, // 1 cpu
            1 << 16, // 2^16 (64MB). Official: 12
            6 // Official: 3
    );

    /**
     * The Usuario Repo.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * The Equipo Repo.
     */
    private final EquipoRepository equipoRepository;

    /**
     * The Constructor.
     *
     * @param usuarioRepository the repo.
     */
    @Autowired
    public MicroSataService(@NonNull EquipoRepository equipoRepository, @NonNull UsuarioRepository usuarioRepository) {
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * The number of Usuario.
     *
     * @return the size of {@link Usuario} table.
     */
    public Long getUsuarioSize() {
        return this.usuarioRepository.count();
    }

    /**
     * Retrieve a {@link Usuario}.
     *
     * @param rutEmail to use.
     * @return the {@link Usuario}.
     */
    // @Transactional(readOnly = true)
    public Optional<Usuario> retrieveUsuario(@NonNull String rutEmail) {
        log.debug("Retrieving Usuario for <{}> ..", rutEmail);

        // Empty
        if (StringUtils.isEmpty(rutEmail)) {
            return Optional.empty();
        }

        // Email
        if (rutEmail.contains("@")) {
            return Optional.ofNullable(this.usuarioRepository.findByEmail(rutEmail));
        }

        // Rut
        return Optional.ofNullable(this.usuarioRepository.findByRut(rutEmail));
    }

    /**
     * Retrieve a {@link Equipo}.
     *
     * @param id to use.
     * @return the {@link Equipo}.
     */
    public Optional<Usuario> retrieveUsuario(@NonNull Long id) {
        log.debug("Retrieving Usuario for id:{} ..", id);

        try {
            return Optional.of(this.usuarioRepository.getReferenceById(id));
        } catch (EntityNotFoundException | JpaObjectRetrievalFailureException ex) {
            return Optional.empty();
        }
    }

    /**
     * Authenticate a Usuario.
     *
     * @param rutEmail to use.
     * @param password to use.
     * @return the Usuario.
     */
    public Optional<Usuario> authenticate(@NonNull String rutEmail, @NonNull String password) {

        // Check empty
        if (StringUtils.isEmpty(rutEmail) || StringUtils.isEmpty(password)) {
            log.warn("RutEmail or Password empty, skipping!");
            return Optional.empty();
        }

        // Find the Usuario
        Optional<Usuario> oUsuario = this.retrieveUsuario(rutEmail);

        // Not found
        if (oUsuario.isEmpty()) {
            return Optional.empty();
        }

        // Wrong password
        if (!PASSWORD_ENCODER.matches(password, oUsuario.get().getPassword())) {
            log.warn("Usuario with login=<{}> use a wrong password!", rutEmail);
            return Optional.empty();
        }

        // All ok!
        return oUsuario;
    }

    /**
     * Create a Usuario in the backend.
     *
     * @param usuario to create.
     * @return the Usuario created.
     */
    @Transactional
    public Usuario addUsuario(@NonNull Usuario usuario) {

        // Check the empty password
        if (StringUtils.isEmpty(usuario.getPassword())) {
            throw new PreRequisitesException("Can't add Usuario: Empty Password");
        }

        // Check the RUT
        if (!ValidationUtils.isRutValid(usuario.getRut())) {
            throw new PreRequisitesException("Can't add Usuario: Invalid RUT");
        }

        // Encode the password using the Password Encoder
        usuario.setPassword(PASSWORD_ENCODER.encode(usuario.getPassword()));

        log.debug("Saving Usuario: {}", usuario);

        // Save in the backend
        try {
            return this.usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException ex) {
            throw new IntegrityException("Can't add Usuario: Data Integrity Violation", ex);
        }
    }

    /**
     * The List of Usuario.
     *
     * @return the List of Usuarios.
     */
    public List<Usuario> getUsuarios() {
        return this.usuarioRepository.findAll();
    }

    /**
     * Insert a Equipo into the backend.
     *
     * @param equipo to insert.
     * @return the Equipo created.
     */
    @Transactional
    public Equipo saveEquipo(Equipo equipo) {
        try {
            return this.equipoRepository.saveAndFlush(equipo);
        } catch (DataIntegrityViolationException ex) {
            throw new IntegrityException("Can't add Equipo: Data Integrity Violation", ex);
        }
    }

    /**
     * Retrieve a {@link Equipo}.
     *
     * @param id to use.
     * @return the {@link Equipo}.
     */
    @Transactional(readOnly = true)
    public Optional<Equipo> retrieveEquipo(@NonNull Long id) {
        log.debug("Retrieving Equipo for id:{} ..", id);

        // Retrieve with Archivos
        Equipo equipo = this.equipoRepository.retrieveWithArchivos(id);
        if (equipo == null) {
            return Optional.empty();
        }

        // Return the Equipo
        return Optional.of(equipo);
    }

}
