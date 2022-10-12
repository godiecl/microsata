/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.model.device;

import cl.ucn.fondef.JsonUtils;
import cl.ucn.fondef.microsata.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Archivo.
 *
 * @author Diego Urrutia-Astorga.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "archivos")
public final class Archivo extends BaseEntity {

    /**
     * The Nombre.
     */
    @Getter
    @Column(nullable = false)
    private String nombre;

    /**
     * The Size.
     */
    @Getter
    @Column(nullable = false)
    private Long size;

    /**
     * The Equipo.
     */
    @JsonUtils.InvisibleJson
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Equipo equipo;

    /**
     * The Tipo.
     */
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArchivo tipoArchivo;

    /**
     * The Tipo Enum.
     */
    public enum TipoArchivo {
        TIPO_UNSPECIFIED,
        TIPO_PNG,
        TIPO_JPG,
        TIPO_PDF
    }
}
