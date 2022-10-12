/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.dao;

import cl.ucn.fondef.microsata.model.registry.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The Registro repository.
 *
 * @author Diego Urrutia-Astorga.
 */
@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {

}
