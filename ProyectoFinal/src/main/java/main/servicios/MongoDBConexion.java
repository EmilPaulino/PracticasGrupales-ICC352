package main.servicios;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class MongoDBConexion {

    private static MongoDBConexion instancia;
    private final Datastore datastore;

    private MongoDBConexion() {
        var env    = new ProcessBuilder().environment();
        String url = env.get("URL_MONGO");
        String db  = env.get("DB_NOMBRE");

        MongoClient client = MongoClients.create(url);
        datastore = Morphia.createDatastore(client, db);
    }

    public static MongoDBConexion getInstance() {
        if (instancia == null)
            instancia = new MongoDBConexion();
        return instancia;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}