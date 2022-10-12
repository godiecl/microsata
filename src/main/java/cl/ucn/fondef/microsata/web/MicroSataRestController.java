/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.web;

import cl.ucn.fondef.microsata.grpc.GetUsuariosReply;
import cl.ucn.fondef.microsata.grpc.UsuarioServiceGrpc;
import cl.ucn.fondef.microsata.model.ModelMapper;
import cl.ucn.fondef.microsata.model.registry.Usuario;
import com.google.protobuf.Empty;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The Sata Rest Controller.
 */
@RestController
@Slf4j
public class MicroSataRestController {

    /**
     * the gRPC client.
     */
    @GrpcClient("usuarioservicegrpc")
    private UsuarioServiceGrpc.UsuarioServiceBlockingStub usuarioService;

    /**
     * Retrieve the Usuarios.
     *
     * @return the list of Usuario.
     */
    @GetMapping("/api/usuarios")
    public List<Usuario> getUsuarios() {

        // Call the gRPC server
        GetUsuariosReply reply = this.usuarioService.retrieveUsuarios(Empty.getDefaultInstance());

        // Return the List of Usuario from List of UsuarioGrpc
        // TODO: Remove the password
        return ModelMapper.INSTANCE.toUsuario(reply.getUsuariosGrpcList());
    }

}
