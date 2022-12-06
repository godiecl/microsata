/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.grpc;

import cl.ucn.fondef.microsata.model.ModelMapper;
import cl.ucn.fondef.microsata.model.simulation.Simulacion;
import cl.ucn.fondef.microsata.service.MicroSataService;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * The SimulacionService implementation.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
@GrpcService
public final class SimulacionServiceGrpcImpl extends SimulacionServiceGrpc.SimulacionServiceImplBase {

    // [Frontend] --[api rest]-> [Backend] --[grpc]-> [Core] --> [Base de Datos]

    // [Base de Datos] --> Simulacion.java, SimulacionRepository.java
    // [Core] --> MicroSataService.java
    // [Grpc] --> SimulacionServiceGrpcImpl

    /**
     * The Sata Controller.
     */
    private final MicroSataService sataService;

    /**
     * The Constructor.
     *
     * @param sataService to use.
     */
    @Autowired
    public SimulacionServiceGrpcImpl(@NonNull MicroSataService sataService) {
        this.sataService = sataService;
    }

    /**
     * Retrieve all the Simulaciones from one Equipo
     */
    @Override
    public void retrieveSimulaciones(RetrieveSimulacionesReq request, StreamObserver<RetrieveSimulacionesReply> responseObserver) {

        // Call the core service
        List<Simulacion> simulaciones =
                this.sataService.retrieveSimulaciones(request.getIdEquipo());

        // List<Simulacion> --> List<SimulacionGrpc>
        List<SimulacionGrpc> simulacionesGrpc =
                ModelMapper.INSTANCE.toSimulacionGrpc(simulaciones);

        // Send the List over the grpc wire
        responseObserver.onNext(RetrieveSimulacionesReply.newBuilder()
                .addAllSimulacionesGrpc(simulacionesGrpc)
                .build());

        // Notify the end of the request
        responseObserver.onCompleted();

    }
}
