package net.minebo.brawl.mongo.model;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BrawlProfile {

    public static HashMap<UUID, BrawlProfile> profiles = new HashMap<>();

    public UUID uniqueId;
    @Setter public String lastUsername;
    @Setter public String lastKit;

    public Statistic money;
    public Statistic kills;
    public Statistic deaths;
    public Statistic killstreak;
    public Statistic highestkillstreak;

    public List<String> ownedKits;

    @Getter @Setter public Kit selectedKit;
    @Getter @Setter public boolean spawnProtected;

    // Load from Document (MongoDB, on server start)
    public BrawlProfile(Document doc) {
        this.uniqueId = UUID.fromString(doc.getString("uniqueId"));
        this.lastUsername = doc.getString("lastUsername");
        this.lastKit = doc.getString("lastKit") != null ? doc.getString("lastKit") : "";
        this.money = new Statistic(doc.getInteger("money"));
        this.kills = new Statistic(doc.getInteger("kills"));
        this.deaths = new Statistic(doc.getInteger("deaths"));
        this.killstreak = new Statistic(doc.getInteger("killstreak"));
        this.highestkillstreak = new Statistic(doc.getInteger("highestkillstreak"));
        this.ownedKits = doc.getList("ownedKits", String.class) != null ? doc.getList("ownedKits", String.class) : new ArrayList<>();
        this.spawnProtected = true;
        profiles.put(this.uniqueId, this);
    }

    public Document toDocument() {
        Document document = new Document();
        document.put("uniqueId", uniqueId.toString());
        document.put("lastUsername", lastUsername);
        document.put("lastKit", lastKit);
        document.put("money", money.value);
        document.put("kills", kills.value);
        document.put("deaths", deaths.value);
        document.put("killstreak", killstreak.value);
        document.put("highestkillstreak", highestkillstreak.value);
        document.put("ownedKits", ownedKits);
        return document;
    }

    // Full constructor (for new profiles)
    public BrawlProfile(UUID uniqueId, String lastUsername) {
        this.uniqueId = uniqueId;
        this.lastUsername = lastUsername;
        this.lastKit = "";
        this.money = new Statistic(0);
        this.kills = new Statistic(0);
        this.deaths = new Statistic(0);
        this.killstreak = new Statistic(0);
        this.highestkillstreak = new Statistic(0);
        this.ownedKits = new ArrayList<>();
        this.spawnProtected = true;
        profiles.put(this.uniqueId, this);
    }

    // If you ever want a full manual profile
    public BrawlProfile(UUID uniqueId, String lastUsername, String lastKit, Statistic money, Statistic kills, Statistic deaths, Statistic killstreak, Statistic highestkillstreak, List<String> ownedKits) {
        this.uniqueId = uniqueId;
        this.lastUsername = lastUsername;
        this.lastKit = lastKit != null ? lastKit : "";
        this.money = money != null ? money : new Statistic(0);
        this.kills = kills != null ? kills : new Statistic(0);
        this.deaths = deaths != null ? deaths : new Statistic(0);
        this.killstreak = killstreak != null ? killstreak : new Statistic(0);
        this.highestkillstreak = highestkillstreak != null ? highestkillstreak : new Statistic(0);
        this.ownedKits = ownedKits != null ? ownedKits : new ArrayList<>();
        this.spawnProtected = true;
        profiles.put(this.uniqueId, this);
    }

    public void save() {
        Brawl.getInstance().getMongoHandler().profileCollection.replaceOne(
                Filters.eq("uniqueId", uniqueId.toString()),
                toDocument(),
                new UpdateOptions().upsert(true)
        );
    }

    // NEVER auto-create!
    public static BrawlProfile get(Player player) {
        return profiles.get(player.getUniqueId());
    }
    public static BrawlProfile get(UUID uniqueId) {
        return profiles.get(uniqueId);
    }

    public Boolean ownsKit(Kit kit) {
        return kit.getPrice() == 0 || ownedKits.contains(kit.getName()) || Kit.freeKitMode;
    }

    /**
     * Only call if you know the player doesn't exist and needs a new profile.
     */
    public BrawlProfile(Player player) {
        new BrawlProfile(player.getUniqueId(), player.getName());
    }
}