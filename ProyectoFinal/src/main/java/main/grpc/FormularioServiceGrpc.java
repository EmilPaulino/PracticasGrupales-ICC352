package main.grpc;

import formulariogrpc.FormularioRnGrpc;
import io.grpc.stub.StreamObserver;

import main.entidades.*;
import main.servicios.FormularioServices;
import main.servicios.UsuarioService;

import java.util.ArrayList;
import java.util.List;

public class FormularioServiceGrpc extends FormularioRnGrpc.FormularioRnImplBase {

    private final FormularioServices formularioServices = FormularioServices.getInstancia();

    @Override
    public void crearFormulario(formulariogrpc.Formulario.FormularioResponse request, StreamObserver<formulariogrpc.Formulario.FormularioCreado> responseObserver) {
        Formulario f = formularioServices.crearFormulario(convertir(request));
        responseObserver.onNext(formulariogrpc.Formulario.FormularioCreado.newBuilder().setOk(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void listarFormulariosPorUsuario(formulariogrpc.Formulario.UsuarioRequest request, StreamObserver<formulariogrpc.Formulario.ListaFormulario> responseObserver) {
        int page = request.getPage();
        int size = request.getSize();
        if (page <= 0) page = 1;
        if (size <= 0) size = 5;
        int offset = (page - 1) * size;
        List<Formulario> formularios = formularioServices.listarFormulariosPorUsuarioPaginado(request.getUsuario(), offset, size);        long total = formularioServices.contarFormulariosPorUsuario(request.getUsuario());
        List<formulariogrpc.Formulario.FormularioResponse> listaResponse = new ArrayList<>();
        for (Formulario f : formularios) {
            listaResponse.add(convertirConImagen(f));
        }
        formulariogrpc.Formulario.ListaFormulario resultado = formulariogrpc.Formulario.ListaFormulario.newBuilder().addAllFormulario(listaResponse).setTotal(total).build();
        responseObserver.onNext(resultado);
        responseObserver.onCompleted();
    }

    @Override
    public void getFormularioPorId(formulariogrpc.Formulario.IdRequest request, StreamObserver<formulariogrpc.Formulario.FormularioResponse> responseObserver) {
        Formulario f = formularioServices.getFormularioPorId(request.getId());
        if (f != null) {
            responseObserver.onNext(convertirConImagen(f));
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Formulario no encontrado"));
        }
    }

    @Override
    public void login(formulariogrpc.Formulario.LoginRequest request, StreamObserver<formulariogrpc.Formulario.LoginResponse> responseObserver) {
        UsuarioService usuarioService = UsuarioService.getInstancia();
        String username = request.getUsername();
        String password = request.getPassword();

        boolean valido = false;
        Usuario usuario = usuarioService.findByUsername(username);
        if (usuario != null && password.equals(usuario.getPassword())) {
            valido = true;
        }

        formulariogrpc.Formulario.LoginResponse response =
                formulariogrpc.Formulario.LoginResponse
                        .newBuilder()
                        .setOk(valido)
                        .setUsername(username)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private formulariogrpc.Formulario.FormularioResponse convertir(Formulario f) {
        double lat = 0;
        double lng = 0;
        String usuario = "";
        if (f.getUsuario() != null) {
            usuario = f.getUsuario().getUsername();
        }
        if (f.getUbicacion() != null) {
            lat = f.getUbicacion().getLatitud();
            lng = f.getUbicacion().getLongitud();
        }
        String nivel = "";
        if (f.getNivelEscolar() != null) {
            nivel = f.getNivelEscolar().toString();
        }
        String nombre = f.getNombre() != null ? f.getNombre() : "";
        String sector = f.getSector() != null ? f.getSector() : "";
        return formulariogrpc.Formulario.FormularioResponse.newBuilder().setId(f.getId() != null ? f.getId().toString() : "").setNombre(nombre).setSector(sector).setNivelEscolar(nivel).setLatitud(lat).setLongitud(lng).setUsuario(usuario).setImagenBase64("").build();
    }

    private formulariogrpc.Formulario.FormularioResponse convertirConImagen(Formulario f) {
        double lat = 0;
        double lng = 0;
        String usuario = "";
        if (f.getUsuario() != null) {
            usuario = f.getUsuario().getUsername();
        }
        if (f.getUbicacion() != null) {
            lat = f.getUbicacion().getLatitud();
            lng = f.getUbicacion().getLongitud();
        }
        String imagen = "";
        if (f.getFotoBase64() != null) {
            imagen = f.getFotoBase64();
        }
        String nivel = "";
        if (f.getNivelEscolar() != null) {
            nivel = f.getNivelEscolar().toString();
        }
        String nombre = f.getNombre() != null ? f.getNombre() : "";
        String sector = f.getSector() != null ? f.getSector() : "";
        return formulariogrpc.Formulario.FormularioResponse.newBuilder().setId(f.getId() != null ? f.getId().toString() : "").setNombre(nombre).setSector(sector).setNivelEscolar(nivel).setLatitud(lat).setLongitud(lng).setUsuario(usuario).setImagenBase64(imagen).build();
    }

    private Formulario convertir(formulariogrpc.Formulario.FormularioResponse r) {
        Formulario f = new Formulario();
        f.setNombre(r.getNombre());
        f.setSector(r.getSector());
        if (r.getNivelEscolar() != null && !r.getNivelEscolar().isEmpty()) {
            f.setNivelEscolar(NivelEscolar.valueOf(r.getNivelEscolar()));
        }
        Ubicacion u = new Ubicacion();
        u.setLatitud(r.getLatitud());
        u.setLongitud(r.getLongitud());
        f.setUbicacion(u);
        if (r.getImagenBase64() != null) {
            f.setFotoBase64(r.getImagenBase64());
        }
        if (r.getUsuario() != null && !r.getUsuario().isEmpty()) {
            UsuarioEmbebido usuario = new UsuarioEmbebido();
            usuario.setUsername(r.getUsuario());
            f.setUsuario(usuario);
        }
        if (r.getFechaRegistro() != null && !r.getFechaRegistro().isEmpty()) {
            try {
                java.util.Date fecha = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse(r.getFechaRegistro());
                f.setFechaRegistro(fecha);
            } catch (Exception e) {
                f.setFechaRegistro(new java.util.Date());
            }
        } else {
            f.setFechaRegistro(new java.util.Date());
        }

        return f;
    }
}