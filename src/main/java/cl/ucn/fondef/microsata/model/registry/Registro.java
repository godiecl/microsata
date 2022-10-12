/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.model.registry;

import cl.ucn.fondef.microsata.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

/**
 * The Registro.
 *
 * @author Diego Urrutia-Astorga.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "registros")
public final class Registro extends BaseEntity {

    /**
     * The Entity.
     */
    @Getter
    @NonNull
    @Column(nullable = false)
    private Long idEntidad;

    /**
     * The Descripcion.
     */
    @Getter
    @Column(nullable = false, length = 512)
    private String descripcion;

    /**
     * The Date.
     */
    @Builder.Default
    @Getter
    @Column(nullable = false)
    private Instant fecha = Instant.now();

    /**
     * The Tipo.
     */
    @Getter
    @Enumerated(EnumType.STRING)
    private TipoRegistro tipoRegistro;

    /**
     * The Usuario.
     */
    @Getter
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario usuario;

    /**
     * The Usuario.
     */
    @Setter
    @Column(name = "usuario_id")
    private Long idUsuario;

    /**
     * The Tipo of Registro.
     */
    public enum TipoRegistro {
        TIPO_UNSPECIFIED,
        TIPO_CREACION_USUARIO,
        TIPO_LOGIN_USUARIO
    }

}
