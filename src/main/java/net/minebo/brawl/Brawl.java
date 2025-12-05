package net.minebo.brawl;

import lombok.Getter;
import lombok.Setter;
import net.minebo.brawl.cobalt.ScoreboardImpl;
import net.minebo.brawl.cobalt.completion.KitCompletionHandler;
import net.minebo.brawl.cobalt.context.KitContextResolver;
import net.minebo.brawl.cobalt.cooldown.CombatTagCooldown;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.listener.BrawlListener;
import net.minebo.brawl.listener.DeathListener;
import net.minebo.brawl.listener.ProtectionListener;
import net.minebo.brawl.listener.SoupListener;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.listener.SpawnItemListener;
import net.minebo.cobalt.acf.ACFCommandController;
import net.minebo.cobalt.acf.ACFManager;
import net.minebo.cobalt.cooldown.CooldownHandler;
import net.minebo.brawl.mongo.MongoHandler;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.menu.MenuHandler;
import net.minebo.cobalt.scoreboard.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Brawl extends JavaPlugin {

    @Getter public static Brawl instance;

    @Getter public static ACFManager acf;

    @Getter @Setter public MongoHandler mongoHandler;
    @Getter @Setter public CooldownHandler cooldownHandler;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        MenuHandler.init(this);

        acf = new ACFManager(this);

        ACFCommandController.registerCompletion("kits", new KitCompletionHandler());
        ACFCommandController.registerContext(Kit.class, new KitContextResolver());

        ACFCommandController.registerAll(this);

        new ScoreboardHandler(List.of(new ScoreboardImpl()), this);

        setMongoHandler(new MongoHandler());
        setCooldownHandler(new CooldownHandler(this));

        getCooldownHandler().registerCooldown("Combat Tag", new CombatTagCooldown());
        getCooldownHandler().registerCooldown("Free Soup", new Cooldown());

        Kit.init();

        registerListeners();
        setupEnvironment();
    }

    public void setupEnvironment() {
        Bukkit.setSpawnRadius(0);

        World world = Bukkit.getWorlds().getFirst();

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);

        world.setTime(1300);
        world.setWeatherDuration(0);
        world.setThunderDuration(0);

    }

    public void registerListeners() {
        // Etc
        Bukkit.getPluginManager().registerEvents(new BrawlListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new SoupListener(), this);

        // Spawn
        Bukkit.getPluginManager().registerEvents(new SpawnItemListener(), this);
    }

    @Override
    public void onDisable() {
        BrawlProfile.profiles.values().forEach(BrawlProfile::save);
        saveConfig();
    }

}
