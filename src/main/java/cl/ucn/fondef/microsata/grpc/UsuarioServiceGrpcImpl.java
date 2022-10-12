/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.grpc;

import cl.ucn.fondef.microsata.dao.RegistroRepository;
import cl.ucn.fondef.microsata.model.BaseEntity;
import cl.ucn.fondef.microsata.model.ModelMapper;
import cl.ucn.fondef.microsata.model.registry.Registro;
import cl.ucn.fondef.microsata.model.registry.Usuario;
import cl.ucn.fondef.microsata.service.MicroSataService;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * The gRPC implementation of UsuarioService.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
@GrpcService
public class UsuarioServiceGrpcImpl extends UsuarioServiceGrpc.UsuarioServiceImplBase {

    /**
     * The Sata Controller.
     */
    private final MicroSataService sataService;

    /**
     * The Registro repository.
     */
    private final RegistroRepository registroRepository;

    /**
     * The Constructor.
     *
     * @param sataService to use.
     */
    @Autowired
    public UsuarioServiceGrpcImpl(
            @NonNull MicroSataService sataService,
            @NonNull RegistroRepository registroRepository) {
        this.sataService = sataService;
        this.registroRepository = registroRepository;
    }

    /**
     * The Login.
     */
    public void authenticate(AuthenticateReq request, StreamObserver<UsuarioGrpc> responseObserver) {

        log.debug("LoginReq: {}", BaseEntity.toString(request));

        // Retrieve the Usuario.
        Optional<Usuario> oUsuario = this.sataService.authenticate(request.getRutEmail(), request.getPassword());
        oUsuario.ifPresentOrElse(usuario -> {
            // Register the operation
            Registro registro = Registro.builder()
                    .id(null)
                    .descripcion("Login with: " + request.getRutEmail())
                    .idEntidad(usuario.getId())
                    .idUsuario(usuario.getId())
                    .tipoRegistro(Registro.TipoRegistro.TIPO_LOGIN_USUARIO)
                    .build();

            // Save the Registro
            this.registroRepository.save(registro);
            log.debug("Registro saved: {}", registro);

            // Send the UsuarioGrpc
            // responseObserver.onNext(ModelMapper.INSTANCE.mapRemovePassword(usuario));
            // FIXME: Remove the password on sent!
            responseObserver.onNext(ModelMapper.INSTANCE.map(usuario));
            responseObserver.onCompleted();
        }, () -> {
            responseObserver.onError(buildException(Code.PERMISSION_DENIED, "Wrong Credentials"));
        });

    }

    /**
     * Add a Usuario.
     * FIXME (durrutia): Add the try/catch to detect errors
     */
    @Override
    public void addUsuario(AddUsuarioReq request, StreamObserver<UsuarioGrpc> responseObserver) {

        log.debug("AddUsuario: {}", BaseEntity.toString(request));

        // Check for id of Usuario
        if (request.getIdUsuario() == 0L) {
            responseObserver.onError(buildException(Code.FAILED_PRECONDITION, "Don't receive the id of Usuario"));
            return;
        }

        // The UsuarioGrpc to create
        UsuarioGrpc usuarioGrpc = request.getUsuarioGrpc();
        log.debug("UsuarioGrpc to save: {}", BaseEntity.toString(usuarioGrpc));

        // Create the usuario
        Usuario usuario = this.sataService.addUsuario(ModelMapper.INSTANCE.map(usuarioGrpc));
        log.debug("Usuario saved: {}", usuario);

        // Register the operation
        Registro registro = Registro.builder()
                .id(null)
                .descripcion("AddUsuario with: " + usuario.getEmail())
                .idEntidad(usuario.getId())
                .idUsuario(request.getIdUsuario())
                .tipoRegistro(Registro.TipoRegistro.TIPO_CREACION_USUARIO)
                .build();

        // Save the Registro
        this.registroRepository.save(registro);
        log.debug("Registro saved: {}", registro);

        // FIXME: Remove the password on sent!
        // responseObserver.onNext(ModelMapper.INSTANCE.mapRemovePassword(usuario));
        responseObserver.onNext(ModelMapper.INSTANCE.map(usuario));
        responseObserver.onCompleted();
    }

    /**
     * Get a specific Usuario.
     */
    @Override
    public void retrieveUsuario(RetrieveUsuarioReq request, StreamObserver<UsuarioGrpc> responseObserver) {

        log.debug("RetrieveUsuario: {}", BaseEntity.toString(request));

        Optional<Usuario> oUsuario = this.sataService.retrieveUsuario(request.getRutEmail());
        oUsuario.ifPresentOrElse(usuario -> {
            // Send the UsuarioGrpc
            // FIXME: Remove the password on sent!
            // responseObserver.onNext(ModelMapper.INSTANCE.mapRemovePassword(usuario));
            responseObserver.onNext(ModelMapper.INSTANCE.map(usuario));
            responseObserver.onCompleted();
        }, () -> {
            responseObserver.onError(buildException(Code.NOT_FOUND,
                    "Usuario <" + request.getRutEmail() + "> not found"));
        });

    }

    /**
     * Retrieve all the Usuarios.
     */
    @Override
    public void retrieveUsuarios(Empty request, StreamObserver<GetUsuariosReply> responseObserver) {

        log.debug("RetrieveUsuarios: {}", BaseEntity.toString(request));

        List<Usuario> usuarios = this.sataService.getUsuarios();
        List<UsuarioGrpc> usuariosGrpc = ModelMapper.INSTANCE.toUsuarioGrpc(usuarios);

        responseObserver.onNext(GetUsuariosReply.newBuilder()
                .addAllUsuariosGrpc(usuariosGrpc)
                .build());
        responseObserver.onCompleted();
    }

    /**
     * Build a StatusRuntimeException with a message.
     *
     * @param code    to use.
     * @param message to use.
     * @return the {@link StatusRuntimeException}.
     */
    private static StatusRuntimeException buildException(final Code code, final String message) {
        return StatusProto.toStatusRuntimeException(Status.newBuilder()
                .setCode(code.getNumber())
                .setMessage(message)
                .addDetails(Any.pack(ErrorInfo.newBuilder()
                        .setReason(message)
                        // .putMetadata(, )
                        .build()))
                .build());
    }
}
