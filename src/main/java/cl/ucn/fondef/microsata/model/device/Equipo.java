/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.model.device;

import cl.ucn.fondef.microsata.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * The Equipo.
 *
 * @author Diego Urrutia-Astorga.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipos")
public final class Equipo extends BaseEntity {

    /**
     * Nombre.
     */
    @Getter
    @Column(nullable = false)
    private String nombre;

    /**
     * Descripcion.
     */
    @Getter
    @Column
    private String descripcion;

    /**
     * URL Repositorio.
     */
    @Getter
    @Column(nullable = false)
    private String urlRepositorio;

    /**
     * Estado.
     */
    @Getter
    @Enumerated(EnumType.STRING)
    private EstadoEquipo estadoEquipo;

    /**
     * Archivos.
     */
    @Getter
    @Builder.Default
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Archivo> archivos = new ArrayList<>();

    /**
     * Append a Archivo to this Equipo.
     *
     * @param archivo to append.
     */
    public void addArchivo(@NonNull final Archivo archivo) {
        this.archivos.add(archivo);
        archivo.setEquipo(this);
    }

    /**
     * The Estado Enum.
     */
    public enum EstadoEquipo {
        ESTADO_UNSPECIFIED,
        ESTADO_PROTOTIPO,
        ESTADO_CONSTRUCCION
    }

}
