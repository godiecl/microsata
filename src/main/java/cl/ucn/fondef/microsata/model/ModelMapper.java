/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.model;

import cl.ucn.fondef.microsata.grpc.UsuarioGrpc;
import cl.ucn.fondef.microsata.model.registry.Usuario;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * The Java -- gRPC model mapper.
 *
 * @author Diego Urrutia-Astorga.
 */
@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ModelMapper {

    /**
     * Singleton.
     */
    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    /**
     * UsuarioGrpc to Usuario.
     */
    @Named(value = "UsuarioGrpc2Usuario")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "estadoUsuario", source = "estadoUsuarioGrpc")
    @Mapping(target = "rolUsuario", source = "rolUsuarioGrpc")
    Usuario map(UsuarioGrpc usuarioGrpc);

    /**
     * Usuario to UsuarioGrpc.
     */
    @Named(value = "Usuario2UsuarioGrpc")
    @InheritInverseConfiguration
    UsuarioGrpc map(Usuario usuario);

    /**
     * UsuarioGrpc.Rol to Usuario.Rol.
     */
    @SuppressWarnings("checkstyle:Indentation")
    @ValueMappings({
            @ValueMapping(source = "ROL_USUARIO_GRPC_UNSPECIFIED", target = "ROL_UNSPECIFIED"),
            @ValueMapping(source = "ROL_USUARIO_GRPC_ADMINISTRADOR", target = "ROL_ADMINISTRADOR"),
            @ValueMapping(source = "ROL_USUARIO_GRPC_CONFIGURADOR", target = "ROL_CONFIGURADOR"),
            @ValueMapping(source = "ROL_USUARIO_GRPC_OPERADOR", target = "ROL_OPERADOR"),
    })
    Usuario.RolUsuario map(UsuarioGrpc.RolUsuarioGrpc rol);

    /**
     * Usuario.Rol to UsuarioGrpc.Rol.
     */
    @InheritInverseConfiguration
    UsuarioGrpc.RolUsuarioGrpc map(Usuario.RolUsuario rol);

    /**
     * UsuarioGrpc.Rol to Usuario.Rol.
     */
    @SuppressWarnings("checkstyle:Indentation")
    @ValueMappings({
            @ValueMapping(source = "ESTADO_USUARIO_GRPC_UNSPECIFIED", target = "ESTADO_UNSPECIFIED"),
            @ValueMapping(source = "ESTADO_USUARIO_GRPC_ACTIVO", target = "ESTADO_ACTIVO"),
            @ValueMapping(source = "ESTADO_USUARIO_GRPC_INACTIVO", target = "ESTADO_INACTIVO"),
    })
    Usuario.EstadoUsuario map(UsuarioGrpc.EstadoUsuarioGrpc estado);

    /**
     * Usuario.Estado to UsuarioGrpc.Estado.
     */
    @InheritInverseConfiguration
    UsuarioGrpc.EstadoUsuarioGrpc map(Usuario.EstadoUsuario estado);

    /**
     * List of Usuario to UsuarioGrpc.
     */
    @IterableMapping(qualifiedByName = "Usuario2UsuarioGrpc")
    List<UsuarioGrpc> toUsuarioGrpc(List<Usuario> usuarios);

    /**
     * List of UsuarioGprc to Usuario.
     */
    @IterableMapping(qualifiedByName = "UsuarioGrpc2Usuario")
    List<Usuario> toUsuario(List<UsuarioGrpc> usuariosGrpc);

    /*
     * Usuario to UsuarioGrpc without password.
     *
    @Mapping(target = "password", source = "password", ignore = true)
    UsuarioGrpc mapRemovePassword(Usuario usuario);
     */

}
