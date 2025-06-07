package storage.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Подключение к MongoDB.
 */
public class MongoConfig {
    private static MongoClient client;

    public static synchronized MongoDatabase getDatabase() {
        if (client == null) {
            client = MongoClients.create("mongodb://localhost:27017");
        }
        return client.getDatabase("lab7db");
    }
}
