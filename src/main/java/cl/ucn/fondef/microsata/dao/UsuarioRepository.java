/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.dao;

import cl.ucn.fondef.microsata.model.registry.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The Usuario repository.
 *
 * @author Diego Urrutia-Astorga.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // @Transactional(readOnly = true)
    Usuario findByEmail(String email);

    // @Transactional(readOnly = true)
    Usuario findByRut(String rut);

}
