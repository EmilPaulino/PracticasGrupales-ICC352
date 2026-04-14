package main.grpc;

import formulariogrpc.Formulario;
import formulariogrpc.FormularioRnGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {

    private static GrpcClient instancia;

    private final ManagedChannel channel;
    private final FormularioRnGrpc.FormularioRnBlockingStub stub;

    private GrpcClient() {

        channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .maxInboundMessageSize(50 * 1024 * 1024)
                .build();

        stub = FormularioRnGrpc.newBlockingStub(channel);
    }

    public static GrpcClient getInstancia() {

        if (instancia == null) {
            instancia = new GrpcClient();
        }

        return instancia;
    }

    // LISTAR FORMULARIOS

    public Formulario.ListaFormulario listarFormularios(
            String usuario,
            int page,
            int size) {

        Formulario.UsuarioRequest request =
                Formulario.UsuarioRequest.newBuilder()
                        .setUsuario(usuario)
                        .setPage(page)
                        .setSize(size)
                        .build();

        return stub.listarFormulariosPorUsuario(request);
    }

    public boolean crearFormulario(
            String nombre,
            String sector,
            String nivel,
            double lat,
            double lng,
            String imagenBase64,
            String usuario,
            String fecha){

        Formulario.FormularioResponse request =
                Formulario.FormularioResponse
                        .newBuilder()
                        .setNombre(nombre)
                        .setSector(sector)
                        .setNivelEscolar(nivel)
                        .setUsuario(usuario)
                        .setLatitud(lat)
                        .setLongitud(lng)
                        .setImagenBase64(imagenBase64)
                        .setFechaRegistro(fecha)
                        .build();

        Formulario.FormularioCreado response =
                stub.crearFormulario(request);

        return response.getOk();
    }

    public Formulario.LoginResponse login(
            String username,
            String password){

        Formulario.LoginRequest request =
                Formulario.LoginRequest
                        .newBuilder()
                        .setUsername(username)
                        .setPassword(password)
                        .build();

        return stub.login(request);

    }

}