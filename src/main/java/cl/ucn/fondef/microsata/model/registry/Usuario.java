/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.model.registry;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;

/**
 * The Usuario.
 *
 * @author Diego Urrutia-Astorga.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email", name = "email_unique"),
    @UniqueConstraint(columnNames = "rut", name = "rut_unique")
})
public final class Usuario extends BaseEntity {

    /**
     * The Rut.
     */
    @Getter
    @Setter
    @Column(nullable = false)
    private String rut;

    /**
     * The Email.
     */
    @Getter
    @Setter
    @Email
    @Column(nullable = false)
    private String email;

    /**
     * The Nombre.
     */
    @Getter
    @Column(nullable = false)
    private String nombre;

    /**
     * The Apellido.
     */
    @Getter
    @Column(nullable = false)
    private String apellido;

    /**
     * The Password.
     */
    @Getter
    @Setter
    @Column(nullable = false)
    private String password;

    /**
     * The Estado.
     */
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estadoUsuario;

    /**
     * The Rol.
     */
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rolUsuario;

    /**
     * Estado Enum.
     */
    public enum EstadoUsuario {
        ESTADO_UNSPECIFIED,
        ESTADO_ACTIVO,
        ESTADO_INACTIVO
    }

    /**
     * Rol Enum.
     */
    public enum RolUsuario {
        ROL_UNSPECIFIED,
        ROL_ADMINISTRADOR,
        ROL_CONFIGURADOR,
        ROL_OPERADOR
    }

}
