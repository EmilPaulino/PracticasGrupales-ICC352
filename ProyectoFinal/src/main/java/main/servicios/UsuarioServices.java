package main.servicios;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.InsertOneResult;
import main.entidades.Rol;
import main.entidades.Usuario;
import main.util.TablasMongo;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UsuarioServices {

    private static UsuarioServices instancia;
    private MongoDBConexion mongoDBConexion;

    private UsuarioServices(){
        mongoDBConexion = MongoDBConexion.getInstance();
        mongoDBConexion.getBaseDatos();
    }

    public static UsuarioServices getInstancia(){
        if(instancia == null){
            instancia = new UsuarioServices();
        }
        return instancia;
    }

    public Usuario crearUsuario(@NotNull Usuario usuario){
        if(getUsuarioByUsername(usuario.getUsername()) != null){
            System.out.println("Usuario ya existe...");
            return null;
        }
        Document document = new Document("username", usuario.getUsername()).append("password", usuario.getPassword()).append("rol", usuario.getRol().name());
        MongoCollection<Document> usuarios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.USUARIOS.getValor());
        InsertOneResult result = usuarios.insertOne(document);
        System.out.println("Insertado usuario: " + result.getInsertedId());
        return usuario;
    }

    public List<Usuario> listarUsuarios(){
        List<Usuario> lista = new ArrayList<>();
        MongoCollection<Document> usuarios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.USUARIOS.getValor());
        MongoCursor<Document> iterator = usuarios.find().iterator();
        while(iterator.hasNext()){
            Document doc = iterator.next();
            Usuario usuario = new Usuario();
            usuario.setId(doc.getObjectId("_id").toHexString());
            usuario.setUsername(doc.getString("username"));
            usuario.setPassword(doc.getString("password"));
            usuario.setRol(Rol.valueOf(doc.getString("rol")));
            lista.add(usuario);
        }
        return lista;
    }

    public Usuario getUsuarioByUsername(String username){
        Usuario usuario = null;
        MongoCollection<Document> usuarios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.USUARIOS.getValor());
        Document filtro = new Document("username", username);
        Document doc = usuarios.find(filtro).first();
        if(doc != null){
            usuario = new Usuario();
            usuario.setId(doc.getObjectId("_id").toHexString());
            usuario.setUsername(doc.getString("username"));
            usuario.setPassword(doc.getString("password"));
            usuario.setRol(Rol.valueOf(doc.getString("rol")));
        }
        return usuario;
    }

    public Usuario actualizarUsuario(@NotNull Usuario usuario){
        Usuario tmp = getUsuarioByUsername(usuario.getUsername());
        if(tmp == null){
            throw new RuntimeException("No existe el usuario: " + usuario.getUsername());
        }
        MongoCollection<Document> usuarios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.USUARIOS.getValor());
        Document filtro = new Document("_id", new ObjectId(usuario.getId()));
        Document document = new Document("username", usuario.getUsername()).append("password", usuario.getPassword()).append("rol", usuario.getRol().name()).append("_id", new ObjectId(usuario.getId()));
        usuarios.findOneAndUpdate(filtro, new Document("$set", document));
        return usuario;
    }

    public boolean eliminarUsuario(String id){
        MongoCollection<Document> usuarios = mongoDBConexion.getBaseDatos().getCollection(TablasMongo.USUARIOS.getValor());
        Document filtro = new Document("_id", new ObjectId(id));
        return usuarios.findOneAndDelete(filtro) != null;
    }
}