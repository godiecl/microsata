/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.grpc;

import cl.ucn.fondef.microsata.model.BaseEntity;
import cl.ucn.fondef.microsata.model.ModelMapper;
import com.asarkar.grpc.test.GrpcCleanupExtension;
import com.asarkar.grpc.test.Resources;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * The gRPC Test.
 *
 * @author Diego Urrutia-Astorga.
 */
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
@ExtendWith(GrpcCleanupExtension.class)
public class TestGrpc {

    /**
     * The UsuarioServiceGrpc.
     */
    @Autowired
    private UsuarioServiceGrpc.UsuarioServiceImplBase usuarioServiceGrpc;

    /**
     * Test the RPC.
     *
     * @param resources to use to autoclose the server.
     */
    @SneakyThrows
    @Test
    public void testUsuarioServiceGrpc(Resources resources) {

        log.debug("Starting testUsuarioServiceGrpc ..");

        // Unique server name
        String serverName = InProcessServerBuilder.generateName();
        log.debug("Testing serverName <{}> ..", serverName);

        // Initialize the server
        Server server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(this.usuarioServiceGrpc)
                .build().start();
        // 10 seconds to shutdown
        resources.register(server, Duration.ofSeconds(10));

        // Initialize the channel
        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        // 10 seconds to close
        resources.register(channel, Duration.ofSeconds(10));

        // The Stub
        UsuarioServiceGrpc.UsuarioServiceBlockingStub stub = UsuarioServiceGrpc.newBlockingStub(channel);

        // Authenticate
        log.debug("Testing Login ..");
        {
            // Empty credentials
            Assertions.assertThrows(StatusRuntimeException.class, () -> {
                UsuarioGrpc usuarioGrpc = stub.authenticate(AuthenticateReq.newBuilder()
                        .build());
                log.debug("UsuarioGrpc received: {}", BaseEntity.toString(usuarioGrpc));
            });

            // Incomplete credentials
            Assertions.assertThrows(StatusRuntimeException.class, () -> {
                UsuarioGrpc usuarioGrpc = stub.authenticate(AuthenticateReq.newBuilder()
                        .setRutEmail("admin@ucn.cl")
                        .build());
                log.debug("UsuarioGrpc received: {}", BaseEntity.toString(usuarioGrpc));
            });

            // Incomplete credentials
            Assertions.assertThrows(StatusRuntimeException.class, () -> {
                UsuarioGrpc usuarioGrpc = stub.authenticate(AuthenticateReq.newBuilder()
                        .setPassword("admin123")
                        .build());
                log.debug("UsuarioGrpc received: {}", BaseEntity.toString(usuarioGrpc));
            });

            // Ok credentials
            Assertions.assertDoesNotThrow(() -> {
                UsuarioGrpc usuarioGrpc = stub.authenticate(AuthenticateReq.newBuilder()
                        .setRutEmail("admin@ucn.cl")
                        .setPassword("admin123")
                        .build());
                log.debug("UsuarioGrpc received: {}", BaseEntity.toString(usuarioGrpc));
                log.debug("Usuario received: {}", ModelMapper.INSTANCE.map(usuarioGrpc));
            });

            // Ok credentials
            Assertions.assertDoesNotThrow(() -> {
                UsuarioGrpc usuarioGrpc = stub.authenticate(AuthenticateReq.newBuilder()
                        .setRutEmail("12345678-5")
                        .setPassword("admin123")
                        .build());
                log.debug("UsuarioGrpc received: {}", BaseEntity.toString(usuarioGrpc));
                log.debug("Usuario received: {}", ModelMapper.INSTANCE.map(usuarioGrpc));
            });
        }

    }
}
