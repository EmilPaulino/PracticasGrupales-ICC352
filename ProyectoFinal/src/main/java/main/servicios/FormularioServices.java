package main.servicios;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.MorphiaCursor;
import dev.morphia.query.filters.Filters;
import main.entidades.Formulario;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static dev.morphia.query.filters.Filters.eq;

public class FormularioServices {

    private static FormularioServices instancia;
    private final Datastore ds;

    private FormularioServices() {
        this.ds = MongoDBConexion.getInstance().getDatastore();
    }

    public static FormularioServices getInstancia() {
        if (instancia == null) instancia = new FormularioServices();
        return instancia;
    }

    public Formulario crearFormulario(Formulario formulario) {
        ds.save(formulario);
        return formulario;
    }

    public List<Formulario> listarFormularios() {
        return ds.find(Formulario.class).iterator().toList();
    }

    public Formulario getFormularioPorId(String id) {
        if (!ObjectId.isValid(id)) return null;
        return ds.find(Formulario.class).filter(eq("_id", new ObjectId(id))).first();
    }

    public List<Formulario> listarFormulariosPorUsuario(String username) {
        return ds.find(Formulario.class).filter(eq("usuario.username", username)).iterator().toList();
    }

    public Formulario actualizarFormulario(Formulario formulario) {
        ds.save(formulario);
        return formulario;
    }

    public boolean eliminarFormulario(String id) {
        Formulario formulario = getFormularioPorId(id);
        if (formulario == null) {
            return false;
        }
        ds.delete(formulario);
        return true;
    }

    public List<Formulario> listarFormulariosPaginados(int page, int size) {
        int offset = (page - 1) * size;
        FindOptions options = new FindOptions().skip(offset).limit(size);
        List<Formulario> lista = new ArrayList<>();
        try (MorphiaCursor<Formulario> cursor = ds.find(Formulario.class).iterator(options)) {
            cursor.forEachRemaining(lista::add);
        }
        return lista;
    }

    public long contarFormularios() {
        return ds.find(Formulario.class).count();
    }

    public List<Formulario> listarFormulariosPorUsuarioPaginado(String username, int skip, int size) {

        FindOptions options = new FindOptions()
                .skip(skip)
                .limit(size);

        List<Formulario> lista = new ArrayList<>();

        try (MorphiaCursor<Formulario> cursor = ds.find(Formulario.class)
                .filter(eq("usuario.username", username))
                .iterator(options)) {
            cursor.forEachRemaining(lista::add);
        }

        return lista;
    }

    public long contarFormulariosPorUsuario(String username) {
        return ds.find(Formulario.class)
                .filter(eq("usuario.username", username))
                .count();

    }
}