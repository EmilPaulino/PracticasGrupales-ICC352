package main.servicios;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import main.entidades.Formulario;
import org.bson.types.ObjectId;

import java.util.List;

public class FormularioServices {

    private static FormularioServices instancia;
    private final Datastore ds;

    private FormularioServices() {
        this.ds = MongoDBConexion.getInstance().getDatastore();
    }

    public static FormularioServices getInstancia() {
        if (instancia == null)
            instancia = new FormularioServices();
        return instancia;
    }

    // Crear
    public Formulario crearFormulario(Formulario formulario) {
        ds.save(formulario);
        return formulario;
    }

    // Listar todos
    public List<Formulario> listarFormularios() {
        return ds.find(Formulario.class).iterator().toList();
    }

    // Buscar por id
    public Formulario getFormularioPorId(String id) {
        return ds.find(Formulario.class)
                .filter(Filters.eq("_id", new ObjectId(id)))
                .first();
    }

    // Listar por username del encuestador (requerimiento 16.1)
    public List<Formulario> listarFormulariosPorUsuario(String username) {
        return ds.find(Formulario.class)
                .filter(Filters.eq("usuario.username", username))
                .iterator().toList();
    }

    // Actualizar
    public Formulario actualizarFormulario(Formulario formulario) {
        ds.save(formulario);
        return formulario;
    }

    // Eliminar — devuelve false si no existe
    public boolean eliminarFormulario(String id) {
        Formulario formulario = getFormularioPorId(id);
        if (formulario == null) return false;
        ds.delete(formulario);
        return true;
    }
}