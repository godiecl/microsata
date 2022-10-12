/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.dao;

import cl.ucn.fondef.microsata.model.device.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The Equipo Repository.
 *
 * @author Diego Urrutia-Astorga.
 */
@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    /**
     * Retrieve a Equipo with Archivos.
     */
    @Query("""
            SELECT DISTINCT e
            FROM Equipo e
            LEFT JOIN FETCH e.archivos
            WHERE e.id = :id
            """)
    Equipo retrieveWithArchivos(@Param("id") Long id);

}
