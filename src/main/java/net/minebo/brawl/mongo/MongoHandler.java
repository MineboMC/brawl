package net.minebo.brawl.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.mongo.model.BrawlProfile;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.function.Consumer;

public class MongoHandler {

    MongoClient client;
    MongoDatabase mongoDatabase;

    public MongoCollection<Document> profileCollection;

    public MongoHandler() {
        connect();
    }

    public void connect() {
        FileConfiguration config = Brawl.getInstance().getConfig();

        String uri = config.getString("mongo.uri");
        String database =  config.getString("mongo.database");

        try {
            client = MongoClients.create(uri);

            mongoDatabase = client.getDatabase(database);
            profileCollection = mongoDatabase.getCollection("profiles");

            profileCollection.find().forEach((Consumer<? super Document>) BrawlProfile::new);

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.shutdown(); // shutdown the server if we arent able to connect to the database
        }
    }

}
