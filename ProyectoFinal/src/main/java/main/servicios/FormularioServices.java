package main.servicios;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.InsertOneResult;
import main.entidades.Formulario;
import main.entidades.NivelEscolar;
import main.entidades.Ubicacion;
import main.entidades.Usuario;
import main.util.TablasMongo;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FormularioServices {

    private static FormularioServices instancia;
    private MongoDBConexion mongoDBConexion;

    private FormularioServices(){
        mongoDBConexion = MongoDBConexion.getInstance();
        mongoDBConexion.getBaseDatos();
    }

    public static FormularioServices getInstancia(){
        if(instancia == null){
            instancia = new FormularioServices();
        }
        return instancia;
    }

    public Formulario crearFormulario(@NotNull Formulario formulario){
        Document ubicacionDoc = new Document("latitud", formulario.getUbicacion().getLatitud()).append("longitud", formulario.getUbicacion().getLongitud());
        Document usuarioDoc = new Document("username", formulario.getUsuario().getUsername());
        Document document = new Document("nombre", formulario.getNombre()).append("sector", formulario.getSector()).append("nivelEscolar", formulario.getNivelEscolar().name()).append("usuario", usuarioDoc).append("ubicacion", ubicacionDoc).append("fotoBase64", formulario.getFotoBase64()).append("fechaRegistro", formulario.getFechaRegistro().toString());
        MongoCollection<Document> formularios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.FORMULARIOS.getValor());
        InsertOneResult result = formularios.insertOne(document);
        if(result.getInsertedId()!=null){
            formulario.setId(result.getInsertedId().asObjectId().getValue().toHexString());
        }
        System.out.println("Insertado formulario: " + result.getInsertedId());
        return formulario;
    }

    public List<Formulario> listarFormularios(){
        List<Formulario> lista = new ArrayList<>();
        MongoCollection<Document> formularios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.FORMULARIOS.getValor());
        MongoCursor<Document> iterator = formularios.find().iterator();
        while(iterator.hasNext()){
            Document doc = iterator.next();
            lista.add(documentToFormulario(doc));
        }
        return lista;
    }

    public List<Formulario> listarFormulariosPorUsuario(String username){
        List<Formulario> lista = new ArrayList<>();
        MongoCollection<Document> formularios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.FORMULARIOS.getValor());
        Document filtro = new Document("usuario.username", username);
        MongoCursor<Document> iterator = formularios.find(filtro).iterator();
        while(iterator.hasNext()){
            Document doc = iterator.next();
            lista.add(documentToFormulario(doc));
        }
        return lista;
    }

    public Formulario getFormularioPorId(String id){
        MongoCollection<Document> formularios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.FORMULARIOS.getValor());
        Document filtro = new Document("_id", new ObjectId(id));
        Document doc = formularios.find(filtro).first();
        if(doc!=null){
            return documentToFormulario(doc);
        }
        return null;
    }

    public Formulario actualizarFormulario(@NotNull Formulario formulario){
        MongoCollection<Document> formularios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.FORMULARIOS.getValor());
        Document filtro = new Document("_id", new ObjectId(formulario.getId()));
        Document ubicacionDoc = new Document("latitud", formulario.getUbicacion().getLatitud()).append("longitud", formulario.getUbicacion().getLongitud());
        Document usuarioDoc = new Document("username", formulario.getUsuario().getUsername());
        Document document = new Document("nombre", formulario.getNombre()).append("sector", formulario.getSector()).append("nivelEscolar", formulario.getNivelEscolar().name()).append("usuario", usuarioDoc).append("ubicacion", ubicacionDoc).append("fotoBase64", formulario.getFotoBase64()).append("fechaRegistro", formulario.getFechaRegistro().toString()).append("_id", new ObjectId(formulario.getId()));
        formularios.findOneAndUpdate(filtro, new Document("$set", document));
        return formulario;
    }

    public boolean eliminarFormulario(String id){
        MongoCollection<Document> formularios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.FORMULARIOS.getValor());
        Document filtro = new Document("_id", new ObjectId(id));
        return formularios.findOneAndDelete(filtro) != null;
    }

    private Formulario documentToFormulario(Document doc){
        Formulario formulario = new Formulario();
        formulario.setId(doc.getObjectId("_id").toHexString());
        formulario.setNombre(doc.getString("nombre"));
        formulario.setSector(doc.getString("sector"));
        formulario.setNivelEscolar(NivelEscolar.valueOf(doc.getString("nivelEscolar")));
        Document usuarioDoc = (Document) doc.get("usuario");
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDoc.getString("username"));
        formulario.setUsuario(usuario);
        Document ubicacionDoc = (Document) doc.get("ubicacion");
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(ubicacionDoc.getDouble("latitud"));
        ubicacion.setLongitud(ubicacionDoc.getDouble("longitud"));
        formulario.setUbicacion(ubicacion);
        formulario.setFotoBase64(doc.getString("fotoBase64"));
        formulario.setFechaRegistro(LocalDateTime.parse(doc.getString("fechaRegistro")));
        return formulario;
    }
}